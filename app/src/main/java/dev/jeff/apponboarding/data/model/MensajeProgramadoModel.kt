package dev.jeff.apponboarding.data.model

data class MensajeProgramadoModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val titulo: String,
    val descripcion: String,
    val tipo: TipoMensaje, // Proactivo o Recordatorio
    val fechaProgramada: String, // ISO String
    val canal: CanalEnvio,
    val condicionActivacion: CondicionActivacion,
    val estado: EstadoMensaje = EstadoMensaje.PENDIENTE
)

enum class TipoMensaje {
    PROACTIVO, RECORDATORIO
}

enum class CanalEnvio {
    NOTIFICACION_VISUAL, CORREO
}

enum class CondicionActivacion {
    FECHA_LIMITE, RETRASO_TAREA, EVENTO_AVANCE, FECHA_FIJA
}

enum class EstadoMensaje {
    PENDIENTE, ENVIADO, VENCIDO, COMPLETADO
}