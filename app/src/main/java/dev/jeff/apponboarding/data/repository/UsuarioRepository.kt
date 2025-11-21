package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.remote.RetrofitInstance
import dev.jeff.apponboarding.data.remote.usuario.LoginRequest
import retrofit2.HttpException

class UsuarioRepository {

    suspend fun login(correo: String, password: String): UsuarioModel? {
        return try {
            val response = RetrofitInstance.api.login(
                LoginRequest(correo, password)
            )
            response.usuario
        } catch (e: HttpException) {
            Log.e("LOGIN", "Error HTTP: ${e.code()} - ${e.message()}")
            null
        } catch (e: Exception) {
            Log.e("LOGIN", "Error inesperado: ${e.message}")
            null
        }
    }

    suspend fun getUsuarios(): List<UsuarioModel> {
        return try {
            RetrofitInstance.api.getUsuarios()
        } catch (e: Exception) {
            Log.e("USUARIOS", "Error obteniendo usuarios: ${e.message}")
            emptyList()
        }
    }

    suspend fun crearUsuario(data: Map<String, Any>): String {
        return try {
            val res = RetrofitInstance.api.createUsuario(data)
            res["message"] ?: "Usuario creado"
        } catch (e: Exception) {
            "Error creando usuario"
        }
    }
}
