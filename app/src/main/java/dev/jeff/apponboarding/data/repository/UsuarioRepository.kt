package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.model.UsuarioRequest
import dev.jeff.apponboarding.data.remote.RetrofitInstance
import dev.jeff.apponboarding.data.remote.usuario.LoginRequest
import retrofit2.HttpException

class UsuarioRepository {

    private val api = RetrofitInstance.usuarioApi

    suspend fun login(correo: String, password: String): UsuarioModel? {
        return try {
            val response = api.login(LoginRequest(correo, password))
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
            api.getUsuarios()
        } catch (e: Exception) {
            Log.e("USUARIOS", "Error obteniendo usuarios: ${e.message}")
            emptyList()
        }
    }

    suspend fun getUsuarioById(id: String): UsuarioModel? {
        return try {
            api.getUsuarioById(id)
        } catch (e: Exception) {
            Log.e("USUARIOS", "Error getById: ${e.message}")
            null
        }
    }

    suspend fun createUsuario(usuario: UsuarioRequest): Boolean {
        return try {
            api.createUsuario(usuario)
            true
        } catch (e: Exception) {
            Log.e("USUARIOS", "Error creando: ${e.message}")
            false
        }
    }

    suspend fun updateUsuario(id: String, usuario: UsuarioRequest): Boolean {
        return try {
            api.updateUsuario(id, usuario)
            true
        } catch (e: Exception) {
            Log.e("USUARIOS", "Error actualizando: ${e.message}")
            false
        }
    }

    // --- CORREGIDO PARA US-17 ---
    suspend fun deleteUsuario(id: String): Boolean {
        return try {
            Log.d("USUARIOS", "Intentando eliminar ID: $id")
            val response = api.deleteUsuario(id)

            if (response.isSuccessful) {
                Log.d("USUARIOS", "Eliminación exitosa: ${response.code()}")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("USUARIOS", "Error API ${response.code()}: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e("USUARIOS", "Excepción al eliminar: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}