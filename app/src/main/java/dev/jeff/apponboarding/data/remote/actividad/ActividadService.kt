package dev.jeff.apponboarding.data.remote.actividad

import dev.jeff.apponboarding.data.model.ActividadCountResponse
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.data.model.ActividadRequest
import retrofit2.http.*

interface ActividadService {

    // Obtener todas las actividades
    @GET("Actividad")
    suspend fun getActividades(): List<ActividadModel>

    // Crear una actividad
    @POST("Actividad")
    suspend fun createActividad(@Body actividad: ActividadRequest): ActividadModel

    // Obtener actividad por ID
    @GET("Actividad/{id}")
    suspend fun getActividadById(@Path("id") id: String): ActividadModel

    // Actualizar actividad
    @PUT("Actividad/{id}")
    suspend fun updateActividad(
        @Path("id") id: String,
        @Body actividad: ActividadRequest
    ): ActividadModel

    // Eliminar actividad
    @DELETE("Actividad/{id}")
    suspend fun deleteActividad(@Path("id") id: String): Map<String, String>

    // Obtener actividades por usuario
    @GET("Actividad/usuario/{usuarioRef}")
    suspend fun getActividadesByUsuario(@Path("usuarioRef") usuarioRef: String): List<ActividadModel>

    // Obtener actividades pendientes de un usuario
    @GET("Actividad/pendientes/{usuarioRef}")
    suspend fun getActividadesPendientes(@Path("usuarioRef") usuarioRef: String): List<ActividadModel>

    // Obtener actividades por estado
    @GET("Actividad/estado/{estado}")
    suspend fun getActividadesByEstado(@Path("estado") estado: String): List<ActividadModel>

    // Obtener actividades por rango de fechas
    @GET("Actividad/rango-fechas")
    suspend fun getActividadesByRangoFechas(
        @Query("fechaInicio") fechaInicio: String,
        @Query("fechaFin") fechaFin: String
    ): List<ActividadModel>

    // Obtener conteo de actividades de un usuario
    @GET("Actividad/count/{usuarioRef}")
    suspend fun getActividadesCount(@Path("usuarioRef") usuarioRef: String): ActividadCountResponse
}