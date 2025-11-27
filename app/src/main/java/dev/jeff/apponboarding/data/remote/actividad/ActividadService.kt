package dev.jeff.apponboarding.data.remote.actividad

import dev.jeff.apponboarding.data.model.ActividadCountResponse
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.data.model.ActividadRequest
import dev.jeff.apponboarding.data.model.ResumenGlobalResponse
import dev.jeff.apponboarding.data.model.ResumenUsuarioResponse
import retrofit2.Response
import retrofit2.http.*

interface ActividadService {

    // 🔹 CRUD Básico (Asumiendo que siguen el estándar del controlador)
    @GET("Actividad")
    suspend fun getActividades(): List<ActividadModel>

    @GET("Actividad/{id}")
    suspend fun getActividadById(@Path("id") id: String): ActividadModel

    @POST("Actividad")
    suspend fun createActividad(@Body actividad: ActividadRequest): ActividadModel

    @PUT("Actividad/{id}")
    suspend fun updateActividad(@Path("id") id: String, @Body actividad: ActividadRequest): ActividadModel

    @DELETE("Actividad/{id}")
    suspend fun deleteActividad(@Path("id") id: String): Response<Unit>

    // 🔹 Endpoints Específicos de la Imagen

    // GET /api/Actividad/usuario/{usuarioRef} (Asumido estándar para listas por usuario)
    @GET("Actividad/usuario/{usuarioRef}")
    suspend fun getActividadesByUsuario(@Path("usuarioRef") usuarioRef: String): List<ActividadModel>

    // GET /api/Actividad/rango-fechas
    @GET("Actividad/rango-fechas")
    suspend fun getActividadesByRangoFechas(
        @Query("fechaInicio") fechaInicio: String,
        @Query("fechaFin") fechaFin: String
    ): List<ActividadModel>

    // GET /api/Actividad/count/{usuarioRef}
    @GET("Actividad/count/{usuarioRef}")
    suspend fun getActividadesCount(@Path("usuarioRef") usuarioRef: String): ActividadCountResponse

    // PATCH /api/Actividad/{id}/estado
    @PATCH("Actividad/{id}/estado")
    suspend fun cambiarEstadoActividad(
        @Path("id") id: String,
        @Body nuevoEstado: String // O un objeto según requiera tu API (ej. PatchDoc)
    ): ActividadModel

    // GET /api/Actividad/resumen/{usuarioRef} (Para Dashboard Usuario)
    @GET("Actividad/resumen/{usuarioRef}")
    suspend fun getResumenUsuario(@Path("usuarioRef") usuarioRef: String): ResumenUsuarioResponse

    // GET /api/Actividad/resumen-global (Para Dashboard Global)
    @GET("Actividad/resumen-global")
    suspend fun getResumenGlobal(): ResumenGlobalResponse
}