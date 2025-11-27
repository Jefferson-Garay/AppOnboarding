package dev.jeff.apponboarding.data.model

// 1. MODELO DE LECTURA (Lo que devuelve el servidor)
data class ActividadModel(
    val id: String?,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val estado: String,
    val fechaInicio: String,
    val fechaFin: String,
    val usuarioRef: String
)

// 2. MODELO DE SOLICITUD (Lo que se envía para crear/actualizar)
data class ActividadRequest(
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val fechaInicio: String,
    val fechaFin: String,
    val usuarioRef: String,
    val estado: String
)

// 3. MODELO DE CONTEO (Utilizado para endpoints de conteo)
data class ActividadCountResponse(
    val usuarioRef: String,
    val totalActividades: Int
)

// 4. MODELOS DEL DASHBOARD (Asegúrate de que estos también estén definidos)
data class ResumenGlobalResponse(
    val rango_0_25: Int,
    val rango_26_50: Int,
    val rango_51_75: Int,
    val rango_76_100: Int
)

data class ResumenUsuarioResponse(
    val usuarioRef: String,
    val total: Int,
    val completadas: Int,
    val pendientes: Int,
    val progreso: Int
)