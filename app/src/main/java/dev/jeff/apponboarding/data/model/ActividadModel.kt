package dev.jeff.apponboarding.data.model

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

data class ActividadRequest(
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val fechaInicio: String,
    val fechaFin: String,
    val usuarioRef: String,
    val estado: String
)

data class ActividadCountResponse(
    val usuarioRef: String,
    val totalActividades: Int
)

/* 🔹 LO NUEVO QUE FALTABA — NO QUITÉ NADA */
data class ResumenUsuarioResponse(
    val rango_0_25: Int,
    val rango_26_50: Int,
    val rango_51_75: Int,
    val rango_76_100: Int
)

data class ResumenIndividualResponse(
    val usuarioRef: String,
    val rango_0_25: Int,
    val rango_26_50: Int,
    val rango_51_75: Int,
    val rango_76_100: Int
)
data class ResumenGlobalResponse(
    val rango_0_25: Int,
    val rango_26_50: Int,
    val rango_51_75: Int,
    val rango_76_100: Int
)
