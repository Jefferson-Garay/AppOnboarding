package dev.jeff.apponboarding.data.repository


import android.util.Log
import dev.jeff.apponboarding.data.model.RecursoEstadoRequest
import dev.jeff.apponboarding.data.model.RecursoModel
import dev.jeff.apponboarding.data.model.RecursoRequest
import dev.jeff.apponboarding.data.remote.RetrofitInstance
import retrofit2.HttpException

class RecursoRepository {

    private val api = RetrofitInstance.recursoApi

    // Obtener todos los recursos
    suspend fun getRecursos(): List<RecursoModel> {
        return try {
            api.getRecursos()
        } catch (e: HttpException) {
            Log.e("RECURSO", "Error HTTP: ${e.code()} - ${e.message()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("RECURSO", "Error inesperado: ${e.message}")
            emptyList()
        }
    }

    // Crear recurso
    suspend fun createRecurso(recurso: RecursoRequest): String? {
        return try {
            val response = api.createRecurso(recurso)
            response.message
        } catch (e: HttpException) {
            Log.e("RECURSO", "Error HTTP creando: ${e.code()} - ${e.message()}")
            null
        } catch (e: Exception) {
            Log.e("RECURSO", "Error creando recurso: ${e.message}")
            null
        }
    }

    // Obtener recurso por ID
    suspend fun getRecursoById(id: String): RecursoModel? {
        return try {
            api.getRecursoById(id)
        } catch (e: HttpException) {
            Log.e("RECURSO", "Error HTTP obteniendo recurso: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("RECURSO", "Error obteniendo recurso: ${e.message}")
            null
        }
    }

    // Actualizar recurso
    suspend fun updateRecurso(id: String, recurso: RecursoRequest): String? {
        return try {
            val response = api.updateRecurso(id, recurso)
            response.message
        } catch (e: HttpException) {
            Log.e("RECURSO", "Error HTTP actualizando: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("RECURSO", "Error actualizando recurso: ${e.message}")
            null
        }
    }

    // Eliminar recurso
    suspend fun deleteRecurso(id: String): Boolean {
        return try {
            api.deleteRecurso(id)
            true
        } catch (e: HttpException) {
            Log.e("RECURSO", "Error HTTP eliminando: ${e.code()}")
            false
        } catch (e: Exception) {
            Log.e("RECURSO", "Error eliminando recurso: ${e.message}")
            false
        }
    }

    // Obtener recursos por administrador
    suspend fun getRecursosByAdmin(adminId: String): List<RecursoModel> {
        return try {
            api.getRecursosByAdmin(adminId)
        } catch (e: HttpException) {
            Log.e("RECURSO", "Error HTTP obteniendo por admin: ${e.code()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("RECURSO", "Error obteniendo recursos de admin: ${e.message}")
            emptyList()
        }
    }

    // Obtener recursos por rango de fechas
    suspend fun getRecursosByFecha(desde: String, hasta: String): List<RecursoModel> {
        return try {
            api.getRecursosByFecha(desde, hasta)
        } catch (e: HttpException) {
            Log.e("RECURSO", "Error HTTP rango fechas: ${e.code()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("RECURSO", "Error obteniendo por rango: ${e.message}")
            emptyList()
        }
    }

    // Actualizar estado del recurso
    suspend fun updateEstadoRecurso(id: String, estado: String): String? {
        return try {
            val response = api.updateEstadoRecurso(id, RecursoEstadoRequest(estado))
            response.message
        } catch (e: HttpException) {
            Log.e("RECURSO", "Error HTTP actualizando estado: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("RECURSO", "Error actualizando estado: ${e.message}")
            null
        }
    }
}