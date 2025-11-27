// Archivo: dev/jeff/apponboarding/presentation/dashboard/DashboardViewModel.kt (Versión Final)

package dev.jeff.apponboarding.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.ResumenGlobalResponse
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados posibles para la carga del resumen global
sealed class ResumenGlobalState {
    object Loading : ResumenGlobalState()
    data class Success(val resumen: ResumenGlobalResponse) : ResumenGlobalState()
    data class Error(val message: String) : ResumenGlobalState()
}

// Estados posibles para la carga de la lista de usuarios
sealed class UsuariosDashboardState {
    object Loading : UsuariosDashboardState()
    data class Success(val usuarios: List<UsuarioModel>) : UsuariosDashboardState()
    data class Error(val message: String) : UsuariosDashboardState()
}

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _resumenGlobalState = MutableStateFlow<ResumenGlobalState>(ResumenGlobalState.Loading)
    val resumenGlobalState: StateFlow<ResumenGlobalState> = _resumenGlobalState

    private val _usuariosState = MutableStateFlow<UsuariosDashboardState>(UsuariosDashboardState.Loading)
    val usuariosState: StateFlow<UsuariosDashboardState> = _usuariosState

    init {
        // Carga inicial
        loadResumenGlobal()
        loadUsuarios()
    }

    fun loadResumenGlobal() {
        viewModelScope.launch {
            _resumenGlobalState.value = ResumenGlobalState.Loading
            val result = repository.getResumenGlobal()

            if (result != null) {
                _resumenGlobalState.value = ResumenGlobalState.Success(result)
            } else {
                _resumenGlobalState.value = ResumenGlobalState.Error("No se pudo cargar el resumen global.")
            }
        }
    }

    fun loadUsuarios() {
        viewModelScope.launch {
            _usuariosState.value = UsuariosDashboardState.Loading
            try {
                val usuarios = repository.getUsuariosParaDashboard()
                _usuariosState.value = UsuariosDashboardState.Success(usuarios)
            } catch (e: Exception) {
                _usuariosState.value = UsuariosDashboardState.Error("Fallo al cargar la lista de empleados.")
            }
        }
    }
}