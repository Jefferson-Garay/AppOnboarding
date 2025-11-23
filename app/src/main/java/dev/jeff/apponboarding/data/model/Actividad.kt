package dev.jeff.apponboarding.data.model

import com.google.gson.annotations.SerializedName

data class Actividad(
    // ¡CORREGIDO! Anotación eliminada. GSON mapeará "id" del JSON a este campo.
    val id: String?,

    @SerializedName("titulo")
    val titulo: String?,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("fecha_inicio")
    val fechaInicio: String?,

    @SerializedName("estado")
    val estado: String?,

    @SerializedName("tipo")
    val categoria: String?
) {
    fun isCompleted() = estado.equals("Completada", ignoreCase = true)
}