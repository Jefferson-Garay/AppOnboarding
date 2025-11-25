package dev.jeff.apponboarding.data.model

data class ConversationHistoryItem(
    val id: String?,
    val usuarioRef: String,
    val usuarioNombre: String?,
    val mensajeUsuario: String,
    val respuestaChatbot: String,
    val fecha: String,
    val recursosCompartidos: Int?
)

data class HistoryStats(
    val totalConversations: Int,
    val totalResourcesShared: Int,
    val lastConversationDate: String?
)
