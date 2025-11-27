package dev.jeff.apponboarding.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.ConversationHistoryItem
import dev.jeff.apponboarding.data.model.HistoryStats
import dev.jeff.apponboarding.data.model.SalaHistoryItem
import dev.jeff.apponboarding.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {

    // Cambiamos el tipo de estado a List<SalaHistoryItem>
    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Idle)
    val historyState: StateFlow<HistoryState> = _historyState

    private val _statsState = MutableStateFlow<HistoryStats?>(null)
    val statsState: StateFlow<HistoryStats?> = _statsState

    // Filters
    // ESTABLECEMOS UN USUARIO POR DEFECTO PARA PROBAR LA CONEXION
    private val _filterUsuario = MutableStateFlow("6922230d6601c7660cf3979e")
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
            
            // Para que aparezcan todos por defecto si el filtro está vacío
            val usuarioRef = if (_filterUsuario.value.isBlank()) null else _filterUsuario.value

            try {
                val history = repository.getHistory(usuarioRef, startDateStr, endDateStr)
                
                if (history.isEmpty()) {
                     _historyState.value = HistoryState.Empty
                } else {
                    // Agrupamos los mensajes por usuario para mostrar "Salas"
                    val salas = groupMessagesByRoom(history)
                    _historyState.value = HistoryState.Success(salas)
                    calculateStats(history)
                }
            } catch (e: Exception) {
                _historyState.value = HistoryState.Error("Error cargando historial: ${e.message}")
            }
        }
    }

    private fun groupMessagesByRoom(messages: List<ConversationHistoryItem>): List<SalaHistoryItem> {
        // Agrupar por usuarioRef
        val grouped = messages.groupBy { it.usuarioRef }
        
        return grouped.map { (userId, userMessages) ->
            // Ordenar mensajes por fecha descendente para obtener el último
            val sortedMessages = userMessages.sortedByDescending { it.fecha }
            val lastMsg = sortedMessages.first()
            val userName = lastMsg.usuarioNombre ?: "Usuario Desconocido"
            
            // Determinar el contenido del último mensaje (usuario o bot)
            // La logica de comparacion de fechas era incorrecta (lastMsg.fecha > lastMsg.fecha siempre es false)
            // Asumimos que el ultimo mensaje es el que queremos mostrar, sea de quien sea.
            
            SalaHistoryItem(
                usuarioRef = userId,
                usuarioNombre = userName,
                ultimoMensaje = lastMsg.mensajeUsuario, // Mostramos lo último que dijo el usuario para identificar
                ultimaFecha = lastMsg.fecha,
                totalMensajes = userMessages.size,
                mensajes = sortedMessages
            )
        }.sortedByDescending { it.ultimaFecha }
    }

    private fun calculateStats(history: List<ConversationHistoryItem>) {
        val totalConversations = history.size // Total de interacciones
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
    // Ahora Success devuelve lista de SALAS, no mensajes sueltos
    data class Success(val items: List<SalaHistoryItem>) : HistoryState()
    data class Error(val message: String) : HistoryState()
}
