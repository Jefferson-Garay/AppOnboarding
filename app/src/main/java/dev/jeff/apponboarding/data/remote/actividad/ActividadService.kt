package dev.jeff.apponboarding.data.remote.actividad

import dev.jeff.apponboarding.data.model.ActividadCountResponse
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.data.model.ActividadRequest
import dev.jeff.apponboarding.data.model.ResumenGlobalResponse
import dev.jeff.apponboarding.data.model.ResumenUsuarioResponse
import retrofit2.http.*

interface ActividadService {

    @GET("Actividad")
    suspend fun getActividades(): List<ActividadModel>

    @POST("Actividad")
    suspend fun createActividad(@Body actividad: ActividadRequest): ActividadModel

    @GET("Actividad/{id}")
    suspend fun getActividadById(@Path("id") id: String): ActividadModel

    @PUT("Actividad/{id}")
    suspend fun updateActividad(
        @Path("id") id: String,
        @Body actividad: ActividadRequest
    ): ActividadModel

    // NUEVO: Para US 11 - Actualizar estado específicamente
    @PATCH("Actividad/{id}/estado")
    suspend fun updateEstadoActividad(
        @Path("id") id: String,
        @Body estado: String // El API espera un string simple o un JSON con string, verificaremos envío
    )

    @DELETE("Actividad/{id}")
    suspend fun deleteActividad(@Path("id") id: String): Map<String, String>

    @GET("Actividad/usuario/{usuarioRef}")
    suspend fun getActividadesByUsuario(@Path("usuarioRef") usuarioRef: String): List<ActividadModel>

    @GET("Actividad/pendientes/{usuarioRef}")
    suspend fun getActividadesPendientes(@Path("usuarioRef") usuarioRef: String): List<ActividadModel>

    @GET("Actividad/estado/{estado}")
    suspend fun getActividadesByEstado(@Path("estado") estado: String): List<ActividadModel>

    @GET("Actividad/rango-fechas")
    suspend fun getActividadesByRangoFechas(
        @Query("fechaInicio") fechaInicio: String,
        @Query("fechaFin") fechaFin: String
    ): List<ActividadModel>

    @GET("Actividad/count/{usuarioRef}")
    suspend fun getActividadesCount(@Path("usuarioRef") usuarioRef: String): ActividadCountResponse
    @GET("Actividad/resumen-global")
    suspend fun getResumenGlobal(): ResumenGlobalResponse // <-- ESTA LÍNEA DEBE EXISTIR

    @GET("Actividad/resumen/{usuarioRef}")
    suspend fun getResumenUsuario(@Path("usuarioRef") usuarioRef: String): ResumenUsuarioResponse // <-- ESTA LÍNEA DEBE EXISTIR
}