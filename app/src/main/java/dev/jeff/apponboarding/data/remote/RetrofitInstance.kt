package dev.jeff.apponboarding.data.remote

import dev.jeff.apponboarding.data.remote.usuario.UsuarioService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://backend-daw.onrender.com/api/"

    val api: UsuarioService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuarioService::class.java)
    }
}
