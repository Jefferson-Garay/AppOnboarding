package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.*
import dev.jeff.apponboarding.data.remote.RetrofitInstance
import retrofit2.HttpException

class ChatRepository {

    private val salaChatApi = RetrofitInstance.salaChatApi
    private val interaccionChatApi = RetrofitInstance.interaccionChatApi

    // ===== SALA CHAT =====

    // Obtener sala por usuario
    suspend fun getSalaByUsuario(usuarioRef: String): SalaChatModel? {
        return try {
            salaChatApi.getSalaByUsuario(usuarioRef)
        } catch (e: HttpException) {
            Log.e("CHAT", "Error HTTP obteniendo sala: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("CHAT", "Error obteniendo sala: ${e.message}")
            null
        }
    }

    // Crear sala
    suspend fun createSala(sala: SalaChatRequest): SalaChatModel? {
        return try {
            salaChatApi.createSala(sala)
        } catch (e: HttpException) {
            Log.e("CHAT", "Error HTTP creando sala: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("CHAT", "Error creando sala: ${e.message}")
            null
        }
    }

    // Actualizar estado de sala
    suspend fun updateEstadoSala(usuarioRef: String, estado: SalaEstadoRequest): SalaChatModel? {
        return try {
            salaChatApi.updateEstadoSala(usuarioRef, estado)
        } catch (e: HttpException) {
            Log.e("CHAT", "Error HTTP actualizando estado: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("CHAT", "Error actualizando estado: ${e.message}")
            null
        }
    }

    // Actualizar contexto de sala
    suspend fun updateContextoSala(usuarioRef: String, contexto: SalaContextoRequest): SalaChatModel? {
        return try {
            salaChatApi.updateContextoSala(usuarioRef, contexto)
        } catch (e: HttpException) {
            Log.e("CHAT", "Error HTTP actualizando contexto: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("CHAT", "Error actualizando contexto: ${e.message}")
            null
        }
    }

    // Eliminar sala
    suspend fun deleteSala(usuarioRef: String): Boolean {
        return try {
            salaChatApi.deleteSala(usuarioRef)
            true
        } catch (e: HttpException) {
            Log.e("CHAT", "Error HTTP eliminando sala: ${e.code()}")
            false
        } catch (e: Exception) {
            Log.e("CHAT", "Error eliminando sala: ${e.message}")
            false
        }
    }

    // ===== INTERACCION CHAT =====

    // Obtener IP de render
    suspend fun getRenderIp(): String? {
        return try {
            val response = interaccionChatApi.getRenderIp()
            response.outbound_ip
        } catch (e: HttpException) {
            Log.e("CHAT", "Error HTTP obteniendo IP: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("CHAT", "Error obteniendo IP: ${e.message}")
            null
        }
    }

    // Enviar mensaje al chatbot
    suspend fun sendMessage(
        usuarioRef: String,
        mensajeUsuario: String,
        contexto: String = ""
    ): ChatResponse? {
        return try {
            val request = ChatRequest(
                usuarioRef = usuarioRef,
                mensajeUsuario = mensajeUsuario,
                respuestaChatbot = "",
                contexto = contexto
            )
            interaccionChatApi.sendMessage(request)
        } catch (e: HttpException) {
            Log.e("CHAT", "Error HTTP enviando mensaje: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("CHAT", "Error enviando mensaje: ${e.message}")
            null
        }
    }
}