package com.example.telemedicine.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.telemedicine.BuildConfig
import com.example.telemedicine.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SymptomsUiState(
    val isRecording: Boolean = false,
    val isTranscribing: Boolean = false,
    val isAnalyzing: Boolean = false,
    val transcript: String = "",
    val result: SymptomAiResult? = null,
    val error: String? = null,
    val manualInput: String = ""           // NEW
)

class SymptomAnalysisViewModel(app: Application): AndroidViewModel(app) {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                SymptomAnalysisViewModel(app)
            }
        }
    }

    private val repo = provideSymptomRepository()
    private val recorder = AudioRecorder(app)
    private val tts = TtsPlayer(app)

    private var currentFile: java.io.File? = null

    private val _state = MutableStateFlow(SymptomsUiState())
    val state: StateFlow<SymptomsUiState> = _state

    fun startRecording() {
        if (_state.value.isRecording) return
        currentFile = recorder.start()
        _state.value = _state.value.copy(isRecording = true, error = null)
    }

    fun stopAndTranscribe() {
        val f = recorder.stop() ?: return
        _state.value = _state.value.copy(isRecording = false, isTranscribing = true, error = null)
        viewModelScope.launch {
            when (val res = repo.transcribeAudio(f)) {
                is RepoResult.Success -> {
                    _state.value = _state.value.copy(isTranscribing = false, transcript = res.data)
                    // If you want auto-analysis after STT, uncomment the next line:
                    // analyze()
                }
                is RepoResult.Error -> {
                    _state.value = _state.value.copy(isTranscribing = false, error = res.message)
                }
            }
        }
    }

    /** Existing "Analyze" continues to use the voice transcript */
    fun analyze() {
        val text = _state.value.transcript.trim()
        if (text.isBlank()) {
            _state.value = _state.value.copy(error = "Please record first.")
            return
        }
        _state.value = _state.value.copy(isAnalyzing = true, error = null)
        viewModelScope.launch {
            when (val res = repo.analyzeTranscript(text)) {
                is RepoResult.Success -> _state.value = _state.value.copy(isAnalyzing = false, result = res.data)
                is RepoResult.Error -> _state.value = _state.value.copy(isAnalyzing = false, error = res.message)
            }
        }
    }

    /** NEW: keep the typed text in state */
    fun updateManualInput(text: String) {
        _state.value = _state.value.copy(manualInput = text, error = null)
    }

    /** NEW: analyze the typed text directly with Gemini */
    fun analyzeManual() {
        val text = _state.value.manualInput.trim()
        if (text.isBlank()) {
            _state.value = _state.value.copy(error = "Please enter some text.")
            return
        }
        _state.value = _state.value.copy(isAnalyzing = true, error = null)
        viewModelScope.launch {
            when (val res = repo.analyzeTranscript(text)) {
                is RepoResult.Success -> _state.value = _state.value.copy(isAnalyzing = false, result = res.data)
                is RepoResult.Error -> _state.value = _state.value.copy(isAnalyzing = false, error = res.message)
            }
        }
    }

    fun speak() {
        val r = _state.value.result ?: return
        val text = buildString {
            append(r.summary)
            if (r.immediateRelief.isNotEmpty()) {
                append(". Immediate relief: ")
                append(r.immediateRelief.joinToString("; "))
            }
        }
        viewModelScope.launch {
            if (BuildConfig.USE_ELEVENLABS_TTS && BuildConfig.ELEVENLABS_API_KEY.isNotBlank()) {
                when (val ttsRes = repo.synthesize(text)) {
                    is RepoResult.Success -> {
                        val bytes = ttsRes.data.bytes()
                        tts.playMp3Bytes(bytes)
                    }
                    is RepoResult.Error -> {
                        // fallback to Android TTS
                        tts.speakAndroidTts(text)
                        _state.value = _state.value.copy(error = "Using Android TTS fallback: ${ttsRes.message}")
                    }
                }
            } else {
                tts.speakAndroidTts(text)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts.release()
    }
}
