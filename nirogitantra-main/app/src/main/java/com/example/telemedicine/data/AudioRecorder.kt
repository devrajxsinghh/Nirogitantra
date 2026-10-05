package com.example.telemedicine.data

import android.content.Context
import android.media.MediaRecorder
import java.io.File

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var output: File? = null

    fun start(): File {
        stop() // ensure clean
        val f = File.createTempFile("symptom_", ".m4a", context.cacheDir)
        output = f
        val r = MediaRecorder()
        r.setAudioSource(MediaRecorder.AudioSource.MIC)
        r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        r.setAudioSamplingRate(44100)
        r.setAudioEncodingBitRate(96000)
        r.setOutputFile(f.absolutePath)
        r.prepare()
        r.start()
        recorder = r
        return f
    }

    fun stop(): File? {
        return try {
            recorder?.run { stop(); release() }
            recorder = null
            output
        } catch (_: Exception) {
            recorder = null
            null
        }
    }
}
