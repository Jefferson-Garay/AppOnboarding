package dev.jeff.apponboarding.data.remote.usuario

import dev.jeff.apponboarding.data.model.UsuarioModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
    
    @GET("Usuario/{id}")
    suspend fun getUsuarioById(@Path("id") id: String): UsuarioModel

    // Endpoint genérico PUT para actualizar usuario
    // Retorna Any? para evitar errores de parsing si devuelve un objeto complejo
    @PUT("Usuario/{id}")
    suspend fun updateUsuario(
        @Path("id") id: String,
        @Body body: Map<String, Any?> 
    ): Any?
}
