package dev.jeff.apponboarding.data.remote.actividad

import dev.jeff.apponboarding.data.model.Actividad
import dev.jeff.apponboarding.data.model.Recurso
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ActividadService {

    @GET("actividad")
    suspend fun getActividades(): Response<List<Actividad>>

    @GET("recurso")
    suspend fun getRecursos(): Response<List<Recurso>>

    // --- ENDPOINT AÑADIDO ---
    @PUT("actividad/{id}")
    suspend fun updateActividad(
        @Path("id") id: String,
        @Body actividad: Actividad
    ): Response<Actividad>
}