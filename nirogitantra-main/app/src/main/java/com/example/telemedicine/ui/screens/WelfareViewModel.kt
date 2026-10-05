package com.example.telemedicine.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telemedicine.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WelfareUiState(
    val areas: List<Area> = WelfareMock.defaultAreas(),
    val selectedArea: Area? = null,
    val snapshot: WelfareSnapshot? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class WelfareViewModel(
    private val repo: WelfareRepository = WelfareRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(WelfareUiState())
    val state: StateFlow<WelfareUiState> = _state.asStateFlow()

    init {
        // Load first area by default
        val first = WelfareMock.defaultAreas().first()
        selectArea(first)
    }

    fun selectArea(area: Area) {
        _state.update { it.copy(selectedArea = area, loading = true, error = null) }
        viewModelScope.launch {
            repo.loadDashboard(area).collect { snap ->
                _state.update { it.copy(snapshot = snap, loading = false) }
            }
        }
    }

    fun refresh() {
        _state.value.selectedArea?.let { selectArea(it) }
    }
}
