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
    
    suspend fun getUsuarioById(id: String): UsuarioModel? {
        return try {
            RetrofitInstance.api.getUsuarioById(id)
        } catch (e: Exception) {
             Log.e("USUARIOS", "Error obteniendo usuario por id: ${e.message}")
             null
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

    suspend fun changePassword(userId: String, newPassword: String): Boolean {
        return try {
            // 1. Obtener datos actuales del usuario
            val currentUser = getUsuarioById(userId) ?: run {
                Log.e("PASSWORD", "No se pudo obtener el usuario con ID: $userId")
                return false
            }
            
            // 2. Construir un Map con SOLO los campos necesarios
            // nombre, correo, password, rolRef, telefono
            // Usamos Any? para permitir nulos si el backend lo soporta
            val updateBody = mutableMapOf<String, Any?>()
            updateBody["nombre"] = currentUser.nombre
            updateBody["correo"] = currentUser.correo
            updateBody["password"] = newPassword
            
            // Enviar rolRef solo si no es nulo, o enviarlo como null explícitamente si es necesario
            updateBody["rolRef"] = currentUser.rolRef 
            
            // Enviar telefono solo si no es nulo
            updateBody["telefono"] = currentUser.telefono

            // 3. Enviar el Map al endpoint PUT
            RetrofitInstance.api.updateUsuario(userId, updateBody)
            Log.d("PASSWORD", "Contraseña actualizada correctamente para ID: $userId")
            true
        } catch (e: HttpException) {
            Log.e("PASSWORD", "Error HTTP al cambiar contraseña: ${e.code()} - ${e.message()}")
            try {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("PASSWORD", "Cuerpo del error: $errorBody")
            } catch (ex: Exception) {
                Log.e("PASSWORD", "No se pudo leer el cuerpo del error")
            }
            false
        } catch (e: Exception) {
            Log.e("PASSWORD", "Error inesperado cambiando contraseña: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
