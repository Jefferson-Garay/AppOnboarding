package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.ActividadCountResponse
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.data.model.ActividadRequest
import dev.jeff.apponboarding.data.remote.RetrofitInstance
import retrofit2.HttpException

class ActividadRepository {

    private val api = RetrofitInstance.actividadApi

    suspend fun getActividades(): List<ActividadModel> {
        return try {
            api.getActividades()
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error getActividades: ${e.message}")
            emptyList()
        }
    }

    suspend fun createActividad(actividad: ActividadRequest): ActividadModel? {
        return try {
            api.createActividad(actividad)
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error createActividad: ${e.message}")
            null
        }
    }

    suspend fun getActividadById(id: String): ActividadModel? {
        return try {
            api.getActividadById(id)
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error getActividadById: ${e.message}")
            null
        }
    }

    suspend fun updateActividad(id: String, actividad: ActividadRequest): ActividadModel? {
        return try {
            api.updateActividad(id, actividad)
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error updateActividad: ${e.message}")
            null
        }
    }

    // NUEVO: Actualizar solo el estado
    suspend fun updateEstadoActividad(id: String, estado: String): Boolean {
        return try {
            // El API Swagger dice que recibe un string en el body.
            // Retrofit serializará el string como JSON ("Completada")
            api.updateEstadoActividad(id, estado)
            true
        } catch (e: HttpException) {
            Log.e("ACTIVIDAD", "Error HTTP updateEstado: ${e.code()}")
            false
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error updateEstado: ${e.message}")
            false
        }
    }

    suspend fun deleteActividad(id: String): Boolean {
        return try {
            api.deleteActividad(id)
            true
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error deleteActividad: ${e.message}")
            false
        }
    }

    suspend fun getActividadesByUsuario(usuarioRef: String): List<ActividadModel> {
        return try {
            api.getActividadesByUsuario(usuarioRef)
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error getByUsuario: ${e.message}")
            emptyList()
        }
    }

    suspend fun getActividadesPendientes(usuarioRef: String): List<ActividadModel> {
        return try {
            api.getActividadesPendientes(usuarioRef)
        } catch (e: Exception) {
            Log.e("ACTIVIDAD", "Error getPendientes: ${e.message}")
            emptyList()
        }
    }

    suspend fun getActividadesByEstado(estado: String): List<ActividadModel> {
        return try {
            api.getActividadesByEstado(estado)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getActividadesByRangoFechas(fechaInicio: String, fechaFin: String): List<ActividadModel> {
        return try {
            api.getActividadesByRangoFechas(fechaInicio, fechaFin)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getActividadesCount(usuarioRef: String): ActividadCountResponse? {
        return try {
            api.getActividadesCount(usuarioRef)
        } catch (e: Exception) {
            null
        }
    }
}