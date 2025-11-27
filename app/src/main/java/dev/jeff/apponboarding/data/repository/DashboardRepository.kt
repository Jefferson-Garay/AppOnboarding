package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.ResumenGlobalResponse
import dev.jeff.apponboarding.data.model.ResumenUsuarioResponse
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.remote.actividad.ActividadService
import dev.jeff.apponboarding.data.repository.UsuarioRepository // <-- ¡IMPORTACIÓN AÑADIDA!
import retrofit2.HttpException

class DashboardRepository(
    private val actividadService: ActividadService,
    // Dependencia del repositorio de usuarios
    private val usuarioRepository: UsuarioRepository
) {

    // --- Funciones para el Gráfico Global de Actividades ---

    suspend fun getResumenGlobal(): ResumenGlobalResponse? {
        return try {
            actividadService.getResumenGlobal()
        } catch (e: HttpException) {
            Log.e("DASHBOARD", "Error HTTP obteniendo resumen global: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("DASHBOARD", "Error inesperado resumen global: ${e.message}")
            null
        }
    }

    // --- Función para obtener el listado de empleados ---
    suspend fun getUsuariosParaDashboard(): List<UsuarioModel> {
        // Llama a la función getUsuarios() de tu UsuarioRepository
        return usuarioRepository.getUsuarios()
    }

    // --- Función para el resumen individual (si se usa) ---

    suspend fun getResumenUsuario(usuarioRef: String): ResumenUsuarioResponse? {
        return try {
            actividadService.getResumenUsuario(usuarioRef)
        } catch (e: HttpException) {
            Log.e("DASHBOARD", "Error HTTP obteniendo resumen usuario: ${e.code()}")
            null
        } catch (e: Exception) {
            Log.e("DASHBOARD", "Error inesperado resumen usuario: ${e.message}")
            null
        }
    }
}