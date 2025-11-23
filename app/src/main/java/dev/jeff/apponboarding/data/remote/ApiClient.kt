package dev.jeff.apponboarding.data.remote

import dev.jeff.apponboarding.data.remote.actividad.ActividadService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://backend-daw.onrender.com/api/"

    val instance: ActividadService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ActividadService::class.java)
    }
}