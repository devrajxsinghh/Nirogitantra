package com.example.telemedicine.network

import retrofit2.http.Part
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/** -------- Speech to Text (Scribe v1) --------
 * POST /v1/speech-to-text (multipart)
 * Docs: https://api.elevenlabs.io/ (Create transcript)
 */
interface ElevenLabsSttApi {
    @Multipart
    @Headers("Accept: application/json")
    @POST("v1/speech-to-text")
    suspend fun transcribe(
        @Part file: MultipartBody.Part,
        @Part("model_id") modelId: RequestBody,
        @Part("file_format") fileFormat: RequestBody?,
        @Part("language_code") language: RequestBody?
    ): Response<SttResponse>
}

@Serializable
data class SttResponse(
    @SerialName("text") val text: String? = null,
    @SerialName("language_code") val languageCode: String? = null
)

/** -------- Text to Speech --------
 * POST /v1/text-to-speech/{voice_id}
 * Returns audio bytes (audio/mpeg by default)
 */
interface ElevenLabsTtsApi {
    @Headers("Accept: audio/mpeg")
    @POST("v1/text-to-speech/{voice_id}")
    @Streaming
    suspend fun synthesize(
        @Path("voice_id") voiceId: String,
        @Body body: TtsRequest
    ): Response<ResponseBody>
}

@Serializable
data class TtsRequest(
    val text: String,
    @SerialName("model_id") val modelId: String = "eleven_turbo_v2_5",
    @SerialName("voice_settings") val voiceSettings: VoiceSettings = VoiceSettings(),
    @SerialName("output_format") val outputFormat: String = "mp3_44100_128"
)

@Serializable
data class VoiceSettings(
    val stability: Double = 0.4,
    @SerialName("similarity_boost") val similarityBoost: Double = 0.8,
    val style: Double = 0.2,
    @SerialName("use_speaker_boost") val useSpeakerBoost: Boolean = true
)
