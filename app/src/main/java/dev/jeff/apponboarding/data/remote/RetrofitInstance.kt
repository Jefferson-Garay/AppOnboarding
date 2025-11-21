package dev.jeff.apponboarding.data.remote

import dev.jeff.apponboarding.data.remote.actividad.ActividadService
import dev.jeff.apponboarding.data.remote.usuario.UsuarioService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://backend-daw.onrender.com/api/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuarioApi: UsuarioService by lazy {
        retrofit.create(UsuarioService::class.java)
    }

    val actividadApi: ActividadService by lazy {
        retrofit.create(ActividadService::class.java)
    }

    // Para mantener compatibilidad con código existente
    val api: UsuarioService by lazy {
        usuarioApi
    }





}
