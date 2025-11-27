package dev.jeff.apponboarding.data.remote.usuario

import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.model.UsuarioRequest
import retrofit2.http.*

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

    // --- MÉTODOS US-17 ---

    @POST("Usuario")
    suspend fun createUsuario(@Body usuario: UsuarioRequest): Map<String, String>

    @GET("Usuario/{id}")
    suspend fun getUsuarioById(@Path("id") id: String): UsuarioModel

    @PUT("Usuario/{id}")
    suspend fun updateUsuario(
        @Path("id") id: String,
        @Body usuario: UsuarioRequest
    ): Map<String, String>

    @DELETE("Usuario/{id}")
    suspend fun deleteUsuario(@Path("id") id: String): Map<String, String>
}