package dev.jeff.apponboarding.data.model

import com.google.gson.annotations.SerializedName

data class Recurso(
    @SerializedName("_id")
    val id: String,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("link")
    val link: String?,

    @SerializedName("tipo")
    val tipo: String?,

    @SerializedName("estado")
    val estado: String?
)
