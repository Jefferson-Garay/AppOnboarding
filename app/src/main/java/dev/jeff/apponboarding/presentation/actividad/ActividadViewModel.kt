package dev.jeff.apponboarding.presentation.actividad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.data.model.ActividadRequest
import dev.jeff.apponboarding.data.repository.ActividadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActividadViewModel(
    private val repository: ActividadRepository
) : ViewModel() {

    private val _actividadesState = MutableStateFlow<ActividadesState>(ActividadesState.Idle)
    val actividadesState: StateFlow<ActividadesState> = _actividadesState

    private val _createState = MutableStateFlow<CreateActividadState>(CreateActividadState.Idle)
    val createState: StateFlow<CreateActividadState> = _createState

    private val _deleteState = MutableStateFlow<DeleteActividadState>(DeleteActividadState.Idle)
    val deleteState: StateFlow<DeleteActividadState> = _deleteState

    private val _updateState = MutableStateFlow<UpdateActividadState>(UpdateActividadState.Idle)
    val updateState: StateFlow<UpdateActividadState> = _updateState

    // Cargar todas las actividades
    fun loadActividades() {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividades()
            _actividadesState.value = ActividadesState.Success(actividades)
        }
    }

    // Cargar actividades de un usuario
    fun loadActividadesByUsuario(usuarioRef: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividadesByUsuario(usuarioRef)
            _actividadesState.value = ActividadesState.Success(actividades)
        }
    }

    // Cargar actividades pendientes
    fun loadActividadesPendientes(usuarioRef: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividadesPendientes(usuarioRef)
            _actividadesState.value = ActividadesState.Success(actividades)
        }
    }

    // Cargar actividades por estado
    fun loadActividadesByEstado(estado: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividadesByEstado(estado)
            _actividadesState.value = ActividadesState.Success(actividades)
        }
    }

    // Cargar actividades por rango de fechas
    fun loadActividadesByRangoFechas(fechaInicio: String, fechaFin: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividadesByRangoFechas(fechaInicio, fechaFin)
            _actividadesState.value = ActividadesState.Success(actividades)
        }
    }

    // Crear actividad
    fun createActividad(actividad: ActividadRequest) {
        viewModelScope.launch {
            _createState.value = CreateActividadState.Loading
            val result = repository.createActividad(actividad)
            if (result != null) {
                _createState.value = CreateActividadState.Success(result)
            } else {
                _createState.value = CreateActividadState.Error("Error al crear la actividad")
            }
        }
    }

    // Actualizar actividad
    fun updateActividad(id: String, actividad: ActividadRequest) {
        viewModelScope.launch {
            _updateState.value = UpdateActividadState.Loading
            val result = repository.updateActividad(id, actividad)
            if (result != null) {
                _updateState.value = UpdateActividadState.Success(result)
            } else {
                _updateState.value = UpdateActividadState.Error("Error al actualizar la actividad")
            }
        }
    }

    // Eliminar actividad
    fun deleteActividad(id: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteActividadState.Loading
            val success = repository.deleteActividad(id)
            if (success) {
                _deleteState.value = DeleteActividadState.Success
            } else {
                _deleteState.value = DeleteActividadState.Error("Error al eliminar la actividad")
            }
        }
    }

    // Resetear estados
    fun resetCreateState() {
        _createState.value = CreateActividadState.Idle
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteActividadState.Idle
    }

    fun resetUpdateState() {
        _updateState.value = UpdateActividadState.Idle
    }
}

// Estados para la lista de actividades
sealed class ActividadesState {
    object Idle : ActividadesState()
    object Loading : ActividadesState()
    data class Success(val actividades: List<ActividadModel>) : ActividadesState()
    data class Error(val message: String) : ActividadesState()
}

// Estados para crear actividad
sealed class CreateActividadState {
    object Idle : CreateActividadState()
    object Loading : CreateActividadState()
    data class Success(val actividad: ActividadModel) : CreateActividadState()
    data class Error(val message: String) : CreateActividadState()
}

// Estados para eliminar actividad
sealed class DeleteActividadState {
    object Idle : DeleteActividadState()
    object Loading : DeleteActividadState()
    object Success : DeleteActividadState()
    data class Error(val message: String) : DeleteActividadState()
}

// Estados para actualizar actividad
sealed class UpdateActividadState {
    object Idle : UpdateActividadState()
    object Loading : UpdateActividadState()
    data class Success(val actividad: ActividadModel) : UpdateActividadState()
    data class Error(val message: String) : UpdateActividadState()
}