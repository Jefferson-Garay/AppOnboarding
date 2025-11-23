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