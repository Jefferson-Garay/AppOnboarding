package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.ActividadCountResponse
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.data.model.ActividadRequest
import dev.jeff.apponboarding.data.remote.RetrofitInstance
import retrofit2.HttpException

class ActividadRepository {

    private val api = RetrofitInstance.actividadApi

    // Obtener todas las actividades
    suspend fun getActividades(): List<ActividadModel> {
        return try {
            api.getActividades()
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP: ${e.code()} - ${e.message()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error inesperado: ${e.message}")
            emptyList()
        }
    }

    // Crear actividad
    suspend fun createActividad(actividad: ActividadRequest): ActividadModel? {
        return try {
            api.createActividad(actividad)
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP creando: ${e.code()} - ${e.message()}")
            null
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error creando actividad: ${e.message}")
            null
        }
    }

    // Obtener actividad por ID
    suspend fun getActividadById(id: String): ActividadModel? {
        return try {
            api.getActividadById(id)
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP obteniendo actividad: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error obteniendo actividad: ${e.message}")
            null
        }
    }

    // Actualizar actividad
    suspend fun updateActividad(id: String, actividad: ActividadRequest): ActividadModel? {
        return try {
            api.updateActividad(id, actividad)
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP actualizando: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error actualizando actividad: ${e.message}")
            null
        }
    }

    // Eliminar actividad
    suspend fun deleteActividad(id: String): Boolean {
        return try {
            api.deleteActividad(id)
            true
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP eliminando: ${e.code()}")
            false
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error eliminando actividad: ${e.message}")
            false
        }
    }

    // Obtener actividades por usuario
    suspend fun getActividadesByUsuario(usuarioRef: String): List<ActividadModel> {
        return try {
            api.getActividadesByUsuario(usuarioRef)
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP obteniendo por usuario: ${e.code()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error obteniendo actividades de usuario: ${e.message}")
            emptyList()
        }
    }

    // Obtener actividades pendientes
    suspend fun getActividadesPendientes(usuarioRef: String): List<ActividadModel> {
        return try {
            api.getActividadesPendientes(usuarioRef)
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP obteniendo pendientes: ${e.code()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error obteniendo pendientes: ${e.message}")
            emptyList()
        }
    }

    // Obtener actividades por estado
    suspend fun getActividadesByEstado(estado: String): List<ActividadModel> {
        return try {
            api.getActividadesByEstado(estado)
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP obteniendo por estado: ${e.code()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error obteniendo por estado: ${e.message}")
            emptyList()
        }
    }

    // Obtener actividades por rango de fechas
    suspend fun getActividadesByRangoFechas(
        fechaInicio: String,
        fechaFin: String
    ): List<ActividadModel> {
        return try {
            api.getActividadesByRangoFechas(fechaInicio, fechaFin)
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP rango fechas: ${e.code()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error obteniendo por rango: ${e.message}")
            emptyList()
        }
    }

    // Obtener conteo de actividades
    suspend fun getActividadesCount(usuarioRef: String): ActividadCountResponse? {
        return try {
            api.getActividadesCount(usuarioRef)
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP obteniendo conteo: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error obteniendo conteo: ${e.message}")
            null
        }
    }
}
