package dev.jeff.apponboarding.data.model

import com.google.gson.annotations.SerializedName

data class UsuarioModel(
    val id: Any?,
    val nombre: String,
    val correo: String,
    @SerializedName("password")
    val passwordHash: String,
    val area: String?,
    val rolRef: String?,
    val telefono: String?,
    val estado: String?,
    val nivelOnboarding: NivelOnboarding
)

data class NivelOnboarding(
    val etapa: String,
    val porcentaje: Int,
    val ultimaActualizacion: String
)
