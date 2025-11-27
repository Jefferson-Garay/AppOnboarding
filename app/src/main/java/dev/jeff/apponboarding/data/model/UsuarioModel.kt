package dev.jeff.apponboarding.data.model

import com.google.gson.annotations.SerializedName

data class UsuarioModel(
    // Aceptamos Any? porque el backend a veces manda String y a veces un Objeto complejo
    @SerializedName("id", alternate = ["_id"])
    val id: Any?,
    val nombre: String,
    val correo: String,
    val passwordHash: String?,
    val area: String?,
    val rolRef: String?,
    val telefono: String?,
    val estado: String?,
    val nivelOnboarding: NivelOnboarding?
) {
    /**
     * MAGIA PURA: Reconstruye el ID de MongoDB a partir del objeto descompuesto.
     * Si el backend envía {timestamp=..., machine=...}, esta función lo convierte
     * de vuelta al string hexadecimal "6927eb..." que la API espera.
     */
    fun obtenerIdReal(): String {
        // 1. Si ya es un texto simple, devolverlo
        if (id is String) return id

        // 2. Si es el objeto complejo de MongoDB (Map)
        if (id is Map<*, *>) {
            try {
                // Extraemos los componentes numéricos
                val timestamp = (id["timestamp"] as? Number)?.toInt() ?: 0
                val machine = (id["machine"] as? Number)?.toInt() ?: 0
                val pid = (id["pid"] as? Number)?.toInt() ?: 0
                val increment = (id["increment"] as? Number)?.toInt() ?: 0

                // Si todo es cero, no es un ID válido
                if (timestamp == 0) return ""

                // Convertimos cada parte a Hexadecimal y rellenamos con ceros (Padding)
                // Estándar MongoDB: 8 chars tiempo + 6 chars máquina + 4 chars PID + 6 chars incremento
                val t = "%08x".format(timestamp)
                val m = "%06x".format(machine)
                val p = "%04x".format(pid)
                val i = "%06x".format(increment)

                // Retornamos el ID reconstruido (ej: 6927eb71bb0b10930b593be1)
                return (t + m + p + i).lowercase()
            } catch (e: Exception) {
                return ""
            }
        }

        // 3. Intento final: convertir a string y buscar patrón hexadecimal por si acaso
        val raw = id.toString()
        val regex = Regex("[0-9a-fA-F]{24}")
        return regex.find(raw)?.value ?: ""
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