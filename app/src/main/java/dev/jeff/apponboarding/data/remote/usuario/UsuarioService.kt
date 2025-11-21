package dev.jeff.apponboarding.data.remote.usuario

import dev.jeff.apponboarding.data.remote.usuario.UsuarioService
import dev.jeff.apponboarding.data.model.UsuarioModel
import retrofit2.http.GET

interface UsuarioService {
    @GET("Usuario")
    suspend fun getUsuarios(): List<UsuarioModel>
}