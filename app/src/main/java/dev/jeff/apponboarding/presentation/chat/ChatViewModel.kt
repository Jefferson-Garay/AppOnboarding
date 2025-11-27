package dev.jeff.apponboarding.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.ChatResponse
import dev.jeff.apponboarding.data.model.SalaChatModel
import dev.jeff.apponboarding.data.model.SalaChatRequest
import dev.jeff.apponboarding.data.model.SalaContextoRequest
import dev.jeff.apponboarding.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Modelo para representar un mensaje en el chat
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _salaState = MutableStateFlow<SalaState>(SalaState.Idle)
    val salaState: StateFlow<SalaState> = _salaState

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _sendState = MutableStateFlow<SendMessageState>(SendMessageState.Idle)
    val sendState: StateFlow<SendMessageState> = _sendState

    private val _currentSala = MutableStateFlow<SalaChatModel?>(null)
    val currentSala: StateFlow<SalaChatModel?> = _currentSala

    // Cargar o crear sala para el usuario
    fun loadOrCreateSala(
        usuarioRef: String,
        nombre: String,
        correo: String,
        area: String,
        rolRef: String
    ) {
        viewModelScope.launch {
            _salaState.value = SalaState.Loading

            // Intentar obtener sala existente
            var sala = repository.getSalaByUsuario(usuarioRef)

            // Si no existe, crear una nueva
            if (sala == null) {
                val request = SalaChatRequest(
                    usuarioRef = usuarioRef,
                    nombre = nombre,
                    correo = correo,
                    area = area,
                    rolRef = rolRef
                )
                sala = repository.createSala(request)
            }

            if (sala != null) {
                _currentSala.value = sala
                _salaState.value = SalaState.Success(sala)

                // Agregar mensaje de bienvenida si no hay mensajes
                if (_messages.value.isEmpty()) {
                    addWelcomeMessage(nombre)
                }
            } else {
                _salaState.value = SalaState.Error("No se pudo cargar la sala de chat")
            }
        }
    }

    // Agregar mensaje de bienvenida
    private fun addWelcomeMessage(nombre: String) {
        val welcomeMessage = ChatMessage(
            id = "welcome_${System.currentTimeMillis()}",
            content = "¡Hola $nombre! 👋 Soy tu asistente virtual de onboarding. Estoy aquí para ayudarte en tu proceso de integración a la empresa. ¿En qué puedo ayudarte hoy?",
            isFromUser = false
        )
        _messages.value = listOf(welcomeMessage)
    }

    // Enviar mensaje al chatbot
    fun sendMessage(usuarioRef: String, message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            // Agregar mensaje del usuario a la lista
            val userMessage = ChatMessage(
                id = "user_${System.currentTimeMillis()}",
                content = message,
                isFromUser = true
            )
            _messages.value = _messages.value + userMessage

            _sendState.value = SendMessageState.Loading

            // Enviar mensaje al backend
            val response = repository.sendMessage(
                usuarioRef = usuarioRef,
                mensajeUsuario = message,
                contexto = _currentSala.value?.contextoPersistente ?: ""
            )

            if (response != null) {
                // Agregar respuesta del chatbot
                val botMessage = ChatMessage(
                    id = "bot_${System.currentTimeMillis()}",
                    content = response.respuesta_chatbot,
                    isFromUser = false
                )
                _messages.value = _messages.value + botMessage
                _sendState.value = SendMessageState.Success(response)

                // Guardar contexto y último mensaje en la sala
                saveContextoToSala(usuarioRef, message, response.respuesta_chatbot)
            } else {
                // Agregar mensaje de error
                val errorMessage = ChatMessage(
                    id = "error_${System.currentTimeMillis()}",
                    content = "Lo siento, hubo un error al procesar tu mensaje. Por favor, intenta de nuevo.",
                    isFromUser = false
                )
                _messages.value = _messages.value + errorMessage
                _sendState.value = SendMessageState.Error("Error al enviar mensaje")
            }
        }
    }

    // Guardar contexto en la sala después de cada interacción
    private fun saveContextoToSala(usuarioRef: String, mensajeUsuario: String, respuestaBot: String) {
        viewModelScope.launch {
            try {
                // Construir contexto persistente con el historial
                val contextoPersistente = buildString {
                    append(_currentSala.value?.contextoPersistente ?: "")
                    append("\nUsuario: $mensajeUsuario")
                    append("\nAsistente: $respuestaBot")
                }

                val request = SalaContextoRequest(
                    contextoPersistente = contextoPersistente,
                    ultimoMensaje = respuestaBot
                )

                val salaActualizada = repository.updateContextoSala(usuarioRef, request)
                if (salaActualizada != null) {
                    _currentSala.value = salaActualizada
                    Log.d("CHAT", "Contexto guardado exitosamente")
                }
            } catch (e: Exception) {
                Log.e("CHAT", "Error guardando contexto: ${e.message}")
            }
        }
    }

    // Limpiar chat
    fun clearChat() {
        _messages.value = emptyList()
        _currentSala.value?.let {
            addWelcomeMessage(it.nombre)
        }
    }

    // Resetear estados
    fun resetSendState() {
        _sendState.value = SendMessageState.Idle
    }
}

// Estados para la sala
sealed class SalaState {
    object Idle : SalaState()
    object Loading : SalaState()
    data class Success(val sala: SalaChatModel) : SalaState()
    data class Error(val message: String) : SalaState()
}

// Estados para enviar mensaje
sealed class SendMessageState {
    object Idle : SendMessageState()
    object Loading : SendMessageState()
    data class Success(val response: ChatResponse) : SendMessageState()
    data class Error(val message: String) : SendMessageState()
}