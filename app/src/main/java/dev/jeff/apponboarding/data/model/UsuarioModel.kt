package dev.jeff.apponboarding.data.model

import com.google.gson.annotations.SerializedName

data class UsuarioModel(
    @SerializedName("id", alternate = ["_id"])
    val id: Any?,
    val nombre: String,
    val correo: String,
    val passwordHash: String?,
    val area: String?,

    // este se usa cuando viene de GET /api/Usuario
    val rolRef: String?,

    // este se usa cuando viene de POST /api/Usuario/login
    @SerializedName("rol")
    val rolNombre: String? = null,

    val telefono: String?,
    val estado: String?,
    val nivelOnboarding: NivelOnboarding?
) {
    /**
     * Reconstruye el ID de MongoDB de forma SEGURA y ESTRICTA.
     * Garantiza que el resultado sea siempre un string hexadecimal de 24 caracteres.
     */
    fun obtenerIdReal(): String {
        // 1. Obtener la representación cruda
        val rawString = when (id) {
            null -> ""
            is String -> id
            is Map<*, *> -> reconstruirDesdeMapa(id)
            else -> id.toString()
        }

        // 2. LIMPIEZA FINAL (El paso más importante):
        // Buscamos exactamente una secuencia de 24 caracteres hexadecimales (0-9, a-f).
        // Esto elimina comillas, espacios o caracteres extra al final que causaban el error.
        val regex = Regex("[0-9a-fA-F]{24}")
        return regex.find(rawString)?.value ?: ""
    }

    private fun reconstruirDesdeMapa(map: Map<*, *>): String {
        try {
            // Si tiene formato $oid (común en exports), usarlo directo
            val oid = map["\$oid"] as? String
            if (!oid.isNullOrBlank()) return oid

            // Extracción de componentes numéricos con MÁSCARAS DE BITS (Safety)
            // Usamos 'and' para evitar que números negativos generen cadenas hex demasiado largas (FFFFFFFF...)
            val timestamp = ((map["timestamp"] as? Number)?.toInt() ?: 0)
            val machine = ((map["machine"] as? Number)?.toInt() ?: 0) and 0xFFFFFF // Asegura máx 6 chars
            val pid = ((map["pid"] as? Number)?.toInt() ?: 0) and 0xFFFF         // Asegura máx 4 chars
            val increment = ((map["increment"] as? Number)?.toInt() ?: 0) and 0xFFFFFF // Asegura máx 6 chars

            // Si todo es cero, el objeto está vacío o inválido
            if (timestamp == 0) return ""

            // Formateo estricto
            val t = "%08x".format(timestamp)
            val m = "%06x".format(machine)
            val p = "%04x".format(pid)
            val i = "%06x".format(increment)

            return (t + m + p + i).lowercase()
        } catch (e: Exception) {
            return ""
        }
    }
}

data class NivelOnboarding(
    val etapa: String,
    val porcentaje: Int,
    val ultimaActualizacion: String
)

data class UsuarioRequest(
    val nombre: String,
    val correo: String,
    val password: String,
    val rolRef: String,
    val telefono: String?
)