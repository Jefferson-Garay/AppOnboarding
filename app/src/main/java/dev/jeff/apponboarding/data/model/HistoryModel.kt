package dev.jeff.apponboarding.data.model

import com.google.gson.annotations.SerializedName

// Agrupación de mensajes por sala
data class SalaHistoryItem(
    val usuarioRef: String,
    val usuarioNombre: String,
    val ultimoMensaje: String,
    val ultimaFecha: String,
    val totalMensajes: Int,
    val mensajes: List<ConversationHistoryItem> // Opcional: si queremos guardar los detalles aquí
)

data class ConversationHistoryItem(
    @SerializedName("_id")
    val id: String?,

    @SerializedName("usuario_ref")
    val usuarioRef: String,

    @SerializedName("usuario_nombre")
    val usuarioNombre: String?,

    @SerializedName("mensaje_usuario")
    val mensajeUsuario: String,

    @SerializedName("respuesta_chatbot")
    val respuestaChatbot: String,

    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("recursos_compartidos")
    val recursosCompartidos: Int?
)

data class HistoryStats(
    val totalConversations: Int,
    val totalResourcesShared: Int,
    val lastConversationDate: String?
)
