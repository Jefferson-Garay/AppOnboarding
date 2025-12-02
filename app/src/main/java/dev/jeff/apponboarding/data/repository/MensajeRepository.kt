package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.*
import dev.jeff.apponboarding.data.remote.RetrofitInstance

class MensajeRepository {

    private val api = RetrofitInstance.actividadApi

    // Obtener mensajes (Filtrando Actividades que empiezan con "MSG_")
    suspend fun getMensajes(usuarioRef: String): List<MensajeProgramadoModel> {
        return try {
            val actividades = api.getActividadesByUsuario(usuarioRef)

            actividades.filter { it.tipo.startsWith("MSG_") }
                .map { actividad ->
                    MensajeProgramadoModel(
                        // IMPORTANTE: Usamos el ID del backend (actividad.id)
                        // Si es nulo, no podremos borrarlo, así que cuidado
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

    // Crear mensaje (Guardándolo como Actividad)
    suspend fun crearMensaje(mensaje: MensajeProgramadoModel, usuarioRef: String): Boolean {
        return try {
            val tipoString = if (mensaje.tipo == TipoMensaje.PROACTIVO) "MSG_PROACTIVO" else "MSG_RECORDATORIO"

            val actividadRequest = ActividadRequest(
                titulo = mensaje.titulo,
                descripcion = mensaje.descripcion,
                tipo = tipoString, // MARCA ESPECIAL
                fechaInicio = mensaje.fechaProgramada,
                fechaFin = mensaje.fechaProgramada,
                usuarioRef = usuarioRef,
                estado = "PENDIENTE"
            )

            val response = api.createActividad(actividadRequest)
            response != null
        } catch (e: Exception) {
            Log.e("MENSAJE_REPO", "Error creando mensaje: ${e.message}")
            false
        }
    }

    // Eliminar mensaje
    suspend fun deleteMensaje(id: String): Boolean {
        if (id.isBlank()) return false

        return try {
            // La API de Actividad devuelve un mapa o void, solo nos importa que no lance error 404/500
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