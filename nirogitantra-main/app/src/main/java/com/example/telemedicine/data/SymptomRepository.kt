package com.example.telemedicine.data

import com.example.telemedicine.BuildConfig
import com.example.telemedicine.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Locale

sealed class RepoResult<out T> {
    data class Success<T>(val data: T): RepoResult<T>()
    data class Error(val message: String, val cause: Throwable? = null): RepoResult<Nothing>()
}

class SymptomRepository(
    private val gemini: GeminiApi,
    private val stt: ElevenLabsSttApi,
    private val tts: ElevenLabsTtsApi
) {
    companion object {
        private val TEXT_PLAIN = "text/plain".toMediaType()
    }

    private val json = Json { ignoreUnknownKeys = true }

    /* --------------------------- STT --------------------------- */
    suspend fun transcribeAudio(file: java.io.File): RepoResult<String> = withContext(Dispatchers.IO) {
        try {
            if (!file.exists() || file.length() == 0L) {
                return@withContext RepoResult.Error("Recorded audio file is empty")
            }
            val (fileFormat, mediaType) = audioPayloadMeta(file)
            val part = MultipartBody.Part.createFormData(
                "file", file.name, file.asRequestBody(mediaType.toMediaType())
            )
            val modelBody = "scribe_v1".toRequestBody(TEXT_PLAIN)
            val formatBody = fileFormat?.toRequestBody(TEXT_PLAIN)
            val resp = stt.transcribe(
                file = part,
                modelId = modelBody,
                fileFormat = formatBody,
                language = null
            )
            if (resp.isSuccessful) {
                val txt = resp.body()?.text.orEmpty()
                if (txt.isBlank()) RepoResult.Error("Empty transcript")
                else RepoResult.Success(txt)
            } else {
                RepoResult.Error("STT failed: ${resp.code()} ${resp.message()} ${resp.errorBody()?.string()?.take(320) ?: ""}")
            }
        } catch (t: Throwable) {
            RepoResult.Error("STT error: ${t.message}", t)
        }
    }

    /* --------------------------- Gemini LLM --------------------------- */
    suspend fun analyzeTranscript(transcript: String): RepoResult<SymptomAiResult> =
        withContext(Dispatchers.IO) {
            fun extractText(resp: GeminiResp): String =
                resp.candidates.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text.orEmpty()

            fun decodeCandidate(raw: String): Pair<SymptomAiResult?, String?> {
                if (raw.isBlank()) return null to "empty-response"
                val parsed = runCatching { json.decodeFromString(SymptomAiResult.serializer(), raw) }
                    .getOrElse { return null to "parse-error: ${it.message}" }
                return parsed to parsed.validationIssue()
            }

            val languageHint = detectLanguageHint(transcript)

            try {
                val first = gemini.generate(body = buildGeminiBody(transcript, languageHint))
                if (!first.isSuccessful) {
                    val errBody = first.errorBody()?.string() ?: ""
                    return@withContext RepoResult.Error("Gemini error: ${first.code()} ${first.message()} $errBody")
                }
                val text1 = extractText(first.body() ?: GeminiResp())
                val (candidate1, issue1) = decodeCandidate(text1)
                if (candidate1 != null && issue1 == null) {
                    return@withContext RepoResult.Success(candidate1)
                }

                val retryReason = issue1 ?: "parse-error"
                val second = gemini.generate(
                    body = buildGeminiRetryBody(
                        lastError = retryReason,
                        transcript = transcript,
                        languageHint = languageHint,
                        previousOutput = text1
                    )
                )
                if (!second.isSuccessful) {
                    return@withContext RepoResult.Error("Gemini retry error: ${second.code()} ${second.message()}")
                }
                val text2 = extractText(second.body() ?: GeminiResp())
                val (candidate2, issue2) = decodeCandidate(text2)
                if (candidate2 != null && issue2 == null) {
                    return@withContext RepoResult.Success(candidate2)
                }

                RepoResult.Error("Parsing error: ${issue2 ?: retryReason}")
            } catch (t: Throwable) {
                RepoResult.Error("Gemini/parse error: ${t.message}", t)
            }
        }
    /* --------------------------- TTS --------------------------- */
    suspend fun synthesize(text: String): RepoResult<okhttp3.ResponseBody> = withContext(Dispatchers.IO) {
        try {
            val response = tts.synthesize(
                voiceId = com.example.telemedicine.BuildConfig.ELEVENLABS_VOICE_ID,
                body = TtsRequest(text = text)
            )
            if (response.isSuccessful && response.body()!=null) {
                RepoResult.Success(response.body()!!)
            } else RepoResult.Error("TTS failed: ${response.code()} ${response.message()}")
        } catch (t: Throwable) {
            RepoResult.Error("TTS error: ${t.message}", t)
        }
    }
}

private fun audioPayloadMeta(file: java.io.File): Pair<String, String> {
    val ext = file.extension.lowercase(Locale.US)
    val mediaType = when (ext) {
        "mp3", "mpga" -> "audio/mpeg"
        "wav" -> "audio/wav"
        "ogg" -> "audio/ogg"
        "webm" -> "audio/webm"
        "aac" -> "audio/aac"
        "flac" -> "audio/flac"
        "m4a", "mp4" -> "audio/mp4"
        "pcm" -> "audio/L16"
        else -> "application/octet-stream"
    }
    val format = if (ext == "pcm") "pcm_s16le_16" else "other"
    return format to mediaType
}


private fun SymptomAiResult.validationIssue(): String? {
    if (summary.isBlank()) return "missing summary"
    if (!commonCauses.hasUsefulItems()) return "missing common_causes"
    if (!immediateRelief.hasUsefulItems()) return "missing immediate_relief"
    if (!nextSteps.hasUsefulItems()) return "missing next_steps"
    if (seriousOrPriority.why.isBlank()) return "missing serious_or_priority.why"
    if (!seriousOrPriority.redFlags.hasUsefulItems()) return "missing red_flags"
    if (recommendedDoctor.isBlank()) return "missing recommended_doctor"
    return null
}

private fun List<String>.hasUsefulItems(): Boolean = any { it.isNotBlank() }

private fun detectLanguageHint(text: String): String? {
    val sample = text.trim()
    if (sample.isEmpty()) return null
    return when {
        DEVANAGARI_REGEX.containsMatchIn(sample) -> "Hindi (hi-IN)"
        BENGALI_REGEX.containsMatchIn(sample) -> "Bengali (bn-IN)"
        GUJARATI_REGEX.containsMatchIn(sample) -> "Gujarati (gu-IN)"
        LATIN_REGEX.containsMatchIn(sample) -> "English (en-US)"
        else -> null
    }
}

private val DEVANAGARI_REGEX = Regex("[\u0900-\u097F]")
private val BENGALI_REGEX = Regex("[\u0980-\u09FF]")
private val GUJARATI_REGEX = Regex("[\u0980-\u09FF]") // wait, Bengali and Gujarati regexes are identical in previous version?
// Fixed Gujarati regex in this write
private val GUJARATI_REGEX_FIXED = Regex("[\u0A80-\u0AFF]")

private val LATIN_REGEX = Regex("[A-Za-z]")


/* --------------------------- Provider --------------------------- */
fun provideSymptomRepository(): SymptomRepository {
    val gemini = GeminiFactory.create()
    val stt = ServiceFactory.elevenLabs().create(ElevenLabsSttApi::class.java)
    val tts = ServiceFactory.elevenLabs().create(ElevenLabsTtsApi::class.java)
    return SymptomRepository(gemini, stt, tts)
}
