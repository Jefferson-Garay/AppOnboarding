package dev.jeff.apponboarding.data.remote.usuario

import dev.jeff.apponboarding.data.model.UsuarioModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(
    val correo: String,
    val password: String
)

data class LoginResponse(
    val message: String?,
    val usuario: UsuarioModel?
)

interface UsuarioService {

    @POST("Usuario/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("Usuario")
    suspend fun getUsuarios(): List<UsuarioModel>

    @POST("Usuario")
    suspend fun createUsuario(@Body usuario: Map<String, Any>): Map<String, String>
}
