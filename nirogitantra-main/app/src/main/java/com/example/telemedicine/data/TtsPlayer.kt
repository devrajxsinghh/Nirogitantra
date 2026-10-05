package com.example.telemedicine.data

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.telemedicine.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

class TtsPlayer(private val context: Context) {
    private var exo: ExoPlayer? = null
    private var tts: TextToSpeech? = null

    suspend fun playMp3Bytes(bytes: ByteArray) = withContext(Dispatchers.Main) {
        stop()
        val tmp = File.createTempFile("tts_", ".mp3", context.cacheDir).apply {
            writeBytes(bytes)
        }
        exo = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(tmp.absolutePath))
            prepare()
            playWhenReady = true
        }
    }

    fun speakAndroidTts(text: String) {
        if (tts == null) {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "symptom_tts")
                }
            }
        } else {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "symptom_tts")
        }
    }

    fun stop() {
        exo?.run { stop(); release() }
        exo = null
        tts?.stop()
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
