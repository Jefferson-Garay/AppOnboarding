package dev.jeff.apponboarding.data.remote.recurso


import dev.jeff.apponboarding.data.model.RecursoEstadoRequest
import dev.jeff.apponboarding.data.model.RecursoModel
import dev.jeff.apponboarding.data.model.RecursoRequest
import dev.jeff.apponboarding.data.model.RecursoResponse
import retrofit2.http.*

interface RecursoService {

    // Obtener todos los recursos
    @GET("Recurso")
    suspend fun getRecursos(): List<RecursoModel>

    // Crear un recurso
    @POST("Recurso")
    suspend fun createRecurso(@Body recurso: RecursoRequest): RecursoResponse

    // Obtener recurso por ID
    @GET("Recurso/{id}")
    suspend fun getRecursoById(@Path("id") id: String): RecursoModel

    // Actualizar recurso
    @PUT("Recurso/{id}")
    suspend fun updateRecurso(
        @Path("id") id: String,
        @Body recurso: RecursoRequest
    ): RecursoResponse

    // Eliminar recurso
    @DELETE("Recurso/{id}")
    suspend fun deleteRecurso(@Path("id") id: String): RecursoResponse

    // Obtener recursos por administrador
    @GET("Recurso/admin/{adminId}")
    suspend fun getRecursosByAdmin(@Path("adminId") adminId: String): List<RecursoModel>

    // Obtener recursos por rango de fechas
    @GET("Recurso/fecha")
    suspend fun getRecursosByFecha(
        @Query("desde") desde: String,
        @Query("hasta") hasta: String
    ): List<RecursoModel>

    // Actualizar estado del recurso
    @PATCH("Recurso/{id}/estado")
    suspend fun updateEstadoRecurso(
        @Path("id") id: String,
        @Body estado: RecursoEstadoRequest
    ): RecursoResponse
}