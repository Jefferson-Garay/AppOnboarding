package dev.jeff.apponboarding.data.model

// ===== MODELOS DE SALA CHAT =====

data class SalaChatModel(
    val id: String?,
    val usuarioRef: String,
    val nombre: String,
    val correo: String,
    val area: String,
    val rolRef: String,
    val nivelOnboarding: NivelOnboardingSala?,
    val estadoOnboardingIA: EstadoOnboardingIA?,
    val contextoPersistente: String?,
    val ultimoMensaje: String?,
    val ultimaActualizacion: String?
)

data class NivelOnboardingSala(
    val etapa: String,
    val porcentaje: Int,
    val ultimaActualizacion: String,
    val estado: String
)

data class EstadoOnboardingIA(
    val pasoActual: String,
    val haVistoDocumentos: Boolean,
    val haConsultadoSupervisor: Boolean,
    val haSolicitadoAdmin: Boolean
)

data class SalaChatRequest(
    val usuarioRef: String,
    val nombre: String,
    val correo: String,
    val area: String,
    val rolRef: String
)

data class SalaEstadoRequest(
    val nivelOnboarding: NivelOnboardingSala,
    val estadoOnboardingIA: EstadoOnboardingIA,
    val ultimoMensaje: String
)

data class SalaContextoRequest(
    val contextoPersistente: String,
    val ultimoMensaje: String
)

// ===== MODELOS DE INTERACCION CHAT =====

data class ChatRequest(
    val usuarioRef: String,
    val mensajeUsuario: String,
    val respuestaChatbot: String,
    val contexto: String
)

data class ChatResponse(
    val mensaje_usuario: String,
    val respuesta_chatbot: String,
    val guardado: Boolean
)

data class RenderIpResponse(
    val outbound_ip: String
)