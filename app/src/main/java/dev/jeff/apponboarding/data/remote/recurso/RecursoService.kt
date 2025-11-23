package dev.jeff.apponboarding.data.remote.recurso

import dev.jeff.apponboarding.data.model.Recurso
import retrofit2.Response
import retrofit2.http.GET

interface RecursoService {
    @GET("recurso") // O la ruta que uses en tu backend
    suspend fun getRecursos(): Response<List<Recurso>>
}