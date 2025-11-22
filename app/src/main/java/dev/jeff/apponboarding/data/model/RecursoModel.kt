package dev.jeff.apponboarding.data.model

data class RecursoModel(
    val id: String?,
    val descripcion: String,
    val link: String,
    val tipo: String,
    val estado: String,
    val fechaSubida: String
)

data class RecursoRequest(
    val descripcion: String,
    val link: String,
    val tipo: String,
    val adminRef: String
)

data class RecursoEstadoRequest(
    val estado: String
)

data class RecursoResponse(
    val message: String
)