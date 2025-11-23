package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.RolModel
import dev.jeff.apponboarding.data.model.RolRequest
import dev.jeff.apponboarding.data.remote.RetrofitInstance
import retrofit2.HttpException

class RolRepository {

    private val api = RetrofitInstance.rolApi

    // Obtener todos los roles
    suspend fun getRoles(): List<RolModel> {
        return try {
            api.getRoles()
        } catch (e: HttpException) {
            Log.e("ROL", "Error HTTP: ${e.code()} - ${e.message()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("ROL", "Error inesperado: ${e.message}")
            emptyList()
        }
    }

    // Crear rol
    suspend fun createRol(rol: RolRequest): RolModel? {
        return try {
            api.createRol(rol)
        } catch (e: HttpException) {
            Log.e("ROL", "Error HTTP creando: ${e.code()} - ${e.message()}")
            null
        } catch (e: Exception) {
            Log.e("ROL", "Error creando rol: ${e.message}")
            null
        }
    }

    // Obtener rol por ID
    suspend fun getRolById(id: String): RolModel? {
        return try {
            api.getRolById(id)
        } catch (e: HttpException) {
            Log.e("ROL", "Error HTTP obteniendo rol: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("ROL", "Error obteniendo rol: ${e.message}")
            null
        }
    }

    // Actualizar rol
    suspend fun updateRol(id: String, rol: RolRequest): RolModel? {
        return try {
            api.updateRol(id, rol)
        } catch (e: HttpException) {
            Log.e("ROL", "Error HTTP actualizando: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("ROL", "Error actualizando rol: ${e.message}")
            null
        }
    }

    // Eliminar rol
    suspend fun deleteRol(id: String): Boolean {
        return try {
            api.deleteRol(id)
            true
        } catch (e: HttpException) {
            Log.e("ROL", "Error HTTP eliminando: ${e.code()}")
            false
        } catch (e: Exception) {
            Log.e("ROL", "Error eliminando rol: ${e.message}")
            false
        }
    }
}