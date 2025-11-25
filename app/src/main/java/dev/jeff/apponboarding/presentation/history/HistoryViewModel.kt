package dev.jeff.apponboarding.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.ConversationHistoryItem
import dev.jeff.apponboarding.data.model.HistoryStats
import dev.jeff.apponboarding.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Idle)
    val historyState: StateFlow<HistoryState> = _historyState

    private val _statsState = MutableStateFlow<HistoryStats?>(null)
    val statsState: StateFlow<HistoryStats?> = _statsState

    // Filters
    private val _filterUsuario = MutableStateFlow("")
    val filterUsuario: StateFlow<String> = _filterUsuario

    private val _filterStartDate = MutableStateFlow<Long?>(null)
    val filterStartDate: StateFlow<Long?> = _filterStartDate

    private val _filterEndDate = MutableStateFlow<Long?>(null)
    val filterEndDate: StateFlow<Long?> = _filterEndDate

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = HistoryState.Loading
            
            val startDateStr = _filterStartDate.value?.let { formatDateForApi(it) }
            val endDateStr = _filterEndDate.value?.let { formatDateForApi(it) }
            
            // IMPORTANTE: Para que aparezcan todos por defecto, si el filtro de usuario está vacío,
            // enviamos null al repositorio.
            val usuarioRef = if (_filterUsuario.value.isBlank()) null else _filterUsuario.value

            // Llamamos al repositorio con usuarioRef=null para obtener todo el historial
            val history = repository.getHistory(usuarioRef, startDateStr, endDateStr)
            
            if (history.isEmpty()) {
                 _historyState.value = HistoryState.Empty
            } else {
                _historyState.value = HistoryState.Success(history)
                calculateStats(history)
            }
        }
    }

    private fun calculateStats(history: List<ConversationHistoryItem>) {
        val totalConversations = history.size
        val totalResources = history.sumOf { it.recursosCompartidos ?: 0 }
        val lastDate = history.maxByOrNull { it.fecha }?.fecha

        _statsState.value = HistoryStats(
            totalConversations = totalConversations,
            totalResourcesShared = totalResources,
            lastConversationDate = lastDate
        )
    }

    fun updateUsuarioFilter(query: String) {
        _filterUsuario.value = query
    }

    fun updateStartDate(date: Long?) {
        _filterStartDate.value = date
    }

    fun updateEndDate(date: Long?) {
        _filterEndDate.value = date
    }

    fun clearFilters() {
        _filterUsuario.value = ""
        _filterStartDate.value = null
        _filterEndDate.value = null
        loadHistory()
    }

    private fun formatDateForApi(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(millis)
    }
}

sealed class HistoryState {
    object Idle : HistoryState()
    object Loading : HistoryState()
    object Empty : HistoryState()
    data class Success(val items: List<ConversationHistoryItem>) : HistoryState()
    data class Error(val message: String) : HistoryState()
}
