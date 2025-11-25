package dev.jeff.apponboarding.data.model

// ===== MODELOS DE SALA CHAT =====

data class SalaChatModel(  // Modelo para representar una sala de chat del usuario
    val id: String?,
    val usuarioRef: String,
    val nombre: String,
    val correo: String,
    val area: String,
    val rolRef: String,
    val nivelOnboarding: NivelOnboardingSala?,
    val estadoOnboardingIA: EstadoOnboardingIA?,
    val contextoPersistente: String?,
    val ultimoMensaje: String?,     // Último mensaje enviado en la sala
    val ultimaActualizacion: String?        // Fecha y hora de la última actualización
)

data class NivelOnboardingSala(  //  Nivel de progreso del empleado
    val etapa: String,
    val porcentaje: Int,
    val ultimaActualizacion: String,
    val estado: String
)

data class EstadoOnboardingIA(   //  Lo que la IA sabe del usuario
    val pasoActual: String,
    val haVistoDocumentos: Boolean,
    val haConsultadoSupervisor: Boolean,
    val haSolicitadoAdmin: Boolean
)

data class SalaChatRequest(  // Modelo para crear una nueva sala de chat
    val usuarioRef: String,
    val nombre: String,
    val correo: String,
    val area: String,
    val rolRef: String
)

data class SalaEstadoRequest(   // Modelo para actualizar el estado de una sala de chat
    val nivelOnboarding: NivelOnboardingSala,
    val estadoOnboardingIA: EstadoOnboardingIA,
    val ultimoMensaje: String
)

data class SalaContextoRequest(  // Modelo para actualizar el contexto de una sala de chat
    val contextoPersistente: String,
    val ultimoMensaje: String
)

// ===== MODELOS DE INTERACCION CHAT =====

data class ChatRequest(  // Modelo para enviar un mensaje al chatbot
    val usuarioRef: String,
    val mensajeUsuario: String,
    val respuestaChatbot: String,
    val contexto: String
)

data class ChatResponse(  // Modelo para recibir una respuesta del chatbot
    val mensaje_usuario: String,
    val respuesta_chatbot: String,
    val guardado: Boolean
)

data class RenderIpResponse(   // Modelo para obtener la IP de render
    val outbound_ip: String
)