package dev.jeff.apponboarding.data.remote.rol

import dev.jeff.apponboarding.data.model.RolModel
import dev.jeff.apponboarding.data.model.RolRequest
import retrofit2.http.*

interface RolService {

    // Obtener todos los roles
    @GET("Rol")
    suspend fun getRoles(): List<RolModel>

    // Crear un rol
    @POST("Rol")
    suspend fun createRol(@Body rol: RolRequest): RolModel

    // Obtener rol por ID
    @GET("Rol/{id}")
    suspend fun getRolById(@Path("id") id: String): RolModel

    // Actualizar rol
    @PUT("Rol/{id}")
    suspend fun updateRol(
        @Path("id") id: String,
        @Body rol: RolRequest
    ): RolModel

    // Eliminar rol
    @DELETE("Rol/{id}")
    suspend fun deleteRol(@Path("id") id: String): Map<String, String>
}