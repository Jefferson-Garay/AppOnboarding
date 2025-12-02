package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.*
import dev.jeff.apponboarding.data.remote.RetrofitInstance
import retrofit2.HttpException

class MensajeRepository {

    private val api = RetrofitInstance.actividadApi

    // Obtener mensajes (Filtrando Actividades que empiezan con "MSG_")
    suspend fun getMensajes(usuarioRef: String): List<MensajeProgramadoModel> {
        return try {
            val actividades = api.getActividadesByUsuario(usuarioRef)

            actividades.filter { it.tipo.startsWith("MSG_") }
                .map { actividad ->
                    MensajeProgramadoModel(
                        id = actividad.id ?: "",
                        titulo = actividad.titulo,
                        descripcion = actividad.descripcion,
                        tipo = if (actividad.tipo == "MSG_PROACTIVO") TipoMensaje.PROACTIVO else TipoMensaje.RECORDATORIO,
                        fechaProgramada = actividad.fechaInicio,
                        canal = CanalEnvio.NOTIFICACION_VISUAL,
                        condicionActivacion = CondicionActivacion.FECHA_FIJA,
                        estado = mapEstado(actividad.estado)
                    )
                }
        } catch (e: Exception) {
            Log.e("MENSAJE_REPO", "Error obteniendo mensajes: ${e.message}")
            emptyList()
        }
    }

    /**
     * Crear mensaje.
     * Retorna un String? -> NULL si fue exitoso, o el MENSAJE DE ERROR si falló.
     */
    suspend fun crearMensaje(mensaje: MensajeProgramadoModel, usuarioRef: String): String? {
        return try {
            val tipoString = if (mensaje.tipo == TipoMensaje.PROACTIVO) "MSG_PROACTIVO" else "MSG_RECORDATORIO"

            val actividadRequest = ActividadRequest(
                titulo = mensaje.titulo,
                descripcion = mensaje.descripcion,
                tipo = tipoString,
                fechaInicio = mensaje.fechaProgramada,
                fechaFin = mensaje.fechaProgramada,
                usuarioRef = usuarioRef,
                estado = "PENDIENTE"
            )

            val response = api.createActividad(actividadRequest)
            if (response != null) null else "Error desconocido al crear" // Éxito retorna null

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Error HTTP ${e.code()}"
            Log.e("MENSAJE_REPO", "Error HTTP: $errorBody")
            "Error del servidor: $errorBody"
        } catch (e: Exception) {
            Log.e("MENSAJE_REPO", "Excepción creando mensaje: ${e.message}")
            "Error de conexión: ${e.message}"
        }
    }

    // Eliminar mensaje
    suspend fun deleteMensaje(id: String): Boolean {
        if (id.isBlank()) return false

        return try {
            api.deleteActividad(id)
            true
        } catch (e: Exception) {
            Log.e("MENSAJE_REPO", "Error al borrar mensaje $id: ${e.message}")
            false
        }
    }

    private fun mapEstado(estadoBd: String): EstadoMensaje {
        return when (estadoBd.uppercase()) {
            "PENDIENTE" -> EstadoMensaje.PENDIENTE
            "ENVIADO" -> EstadoMensaje.ENVIADO
            "COMPLETADA", "VISTO" -> EstadoMensaje.COMPLETADO
            else -> EstadoMensaje.PENDIENTE
        }
    }
}