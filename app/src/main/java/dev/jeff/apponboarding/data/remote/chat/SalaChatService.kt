package dev.jeff.apponboarding.data.remote.chat

import dev.jeff.apponboarding.data.model.SalaChatModel
import dev.jeff.apponboarding.data.model.SalaChatRequest
import dev.jeff.apponboarding.data.model.SalaContextoRequest
import dev.jeff.apponboarding.data.model.SalaEstadoRequest
import retrofit2.http.*

interface SalaChatService {

    // Obtener sala por usuario
    @GET("salas/{usuarioRef}")  // Obtiene la sala de chat del usuario si existe
    suspend fun getSalaByUsuario(@Path("usuarioRef") usuarioRef: String): SalaChatModel

    // Crear una sala
    @POST("salas")
    suspend fun createSala(@Body sala: SalaChatRequest): SalaChatModel

    // Actualizar estado de la sala
    @PUT("salas/{usuarioRef}/estado")  // Actualiza el progreso (ej: "completó 50% del onboarding")
    suspend fun updateEstadoSala(
        @Path("usuarioRef") usuarioRef: String,
        @Body estado: SalaEstadoRequest
    ): SalaChatModel

    // Actualizar contexto de la sala
    @PUT("salas/{usuarioRef}/contexto")   // Guarda el contexto de la conversación
    suspend fun updateContextoSala(
        @Path("usuarioRef") usuarioRef: String,
        @Body contexto: SalaContextoRequest
    ): SalaChatModel

    // Eliminar sala si es necesario
    @DELETE("salas/{usuarioRef}")
    suspend fun deleteSala(@Path("usuarioRef") usuarioRef: String): Map<String, String>
}