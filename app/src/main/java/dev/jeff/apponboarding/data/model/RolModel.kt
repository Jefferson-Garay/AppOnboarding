package dev.jeff.apponboarding.data.model

data class RolModel(
    val id: String?,
    val nombre: String,
    val descripcion: String,
    val permisos: List<String>
)

data class RolRequest(
    val nombre: String,
    val descripcion: String,
    val permisos: List<String>
)