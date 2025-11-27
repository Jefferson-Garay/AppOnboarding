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

    // Estado para contador de pendientes
    private val _pendientesCount = MutableStateFlow(0)
    val pendientesCount: StateFlow<Int> = _pendientesCount


    // -----------------------------------------------------------
    // ---------------------- LOADERS ----------------------------
    // -----------------------------------------------------------

    fun loadActividades() {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividades()
            _actividadesState.value = ActividadesState.Success(actividades)
        }
    }

    fun loadActividadesByUsuario(usuarioRef: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividadesByUsuario(usuarioRef)
            _actividadesState.value = ActividadesState.Success(actividades)
        }
    }

    fun loadActividadesPendientes(usuarioRef: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividadesPendientes(usuarioRef)
            _actividadesState.value = ActividadesState.Success(actividades)

            // Actualizar contador
            _pendientesCount.value = actividades.size
        }
    }

    fun loadPendientesCount(usuarioRef: String) {
        viewModelScope.launch {
            val actividades = repository.getActividadesPendientes(usuarioRef)
            _pendientesCount.value = actividades.size
        }
    }

    fun loadActividadesByEstado(estado: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividadesByEstado(estado)
            _actividadesState.value = ActividadesState.Success(actividades)
        }
    }

    fun loadActividadesByRangoFechas(fechaInicio: String, fechaFin: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            val actividades = repository.getActividadesByRangoFechas(fechaInicio, fechaFin)
            _actividadesState.value = ActividadesState.Success(actividades)
        }
    }


    // -----------------------------------------------------------
    // ---------------------- CREATE -----------------------------
    // -----------------------------------------------------------

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


    // -----------------------------------------------------------
    // ---------------------- UPDATE -----------------------------
    // -----------------------------------------------------------

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

    fun updateEstado(id: String, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                _updateState.value = UpdateActividadState.Loading

                val actual = repository.getActividadById(id)
                if (actual == null) {
                    _updateState.value = UpdateActividadState.Error("Actividad no encontrada")
                    return@launch
                }

                val actividadRequest = ActividadRequest(
                    titulo = actual.titulo,
                    descripcion = actual.descripcion,
                    tipo = actual.tipo,
                    fechaInicio = actual.fechaInicio,
                    fechaFin = actual.fechaFin,
                    usuarioRef = actual.usuarioRef ?: "",
                    estado = nuevoEstado
                )

                val result = repository.updateActividad(id, actividadRequest)

                if (result != null) {
                    _updateState.value = UpdateActividadState.Success(result)
                } else {
                    _updateState.value = UpdateActividadState.Error("Error al actualizar estado")
                }

            } catch (e: Exception) {
                _updateState.value = UpdateActividadState.Error(e.message ?: "Error inesperado")
            }
        }
    }

    fun cambiarEstadoActividad(
        actividadId: String,
        actividad: ActividadModel,
        nuevoEstado: String,
        usuarioRef: String
    ) {
        viewModelScope.launch {

            val actividadRequest = ActividadRequest(
                titulo = actividad.titulo,
                descripcion = actividad.descripcion,
                tipo = actividad.tipo,
                fechaInicio = actividad.fechaInicio,
                fechaFin = actividad.fechaFin,
                usuarioRef = actividad.usuarioRef,
                estado = nuevoEstado
            )

            val result = repository.updateActividad(actividadId, actividadRequest)

            if (result != null) {
                loadPendientesCount(usuarioRef)

                if (_actividadesState.value is ActividadesState.Success) {
                    loadActividadesPendientes(usuarioRef)
                }
            }
        }
    }


    // -----------------------------------------------------------
    // ---------------------- DELETE -----------------------------
    // -----------------------------------------------------------

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


    // -----------------------------------------------------------
    // ------------------ RESET STATES ---------------------------
    // -----------------------------------------------------------

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


// -----------------------------------------------------------
// ---------------------- STATES ------------------------------
// -----------------------------------------------------------

sealed class ActividadesState {
    object Idle : ActividadesState()
    object Loading : ActividadesState()
    data class Success(val actividades: List<ActividadModel>) : ActividadesState()
    data class Error(val message: String) : ActividadesState()
}

sealed class CreateActividadState {
    object Idle : CreateActividadState()
    object Loading : CreateActividadState()
    data class Success(val actividad: ActividadModel) : CreateActividadState()
    data class Error(val message: String) : CreateActividadState()
}

sealed class DeleteActividadState {
    object Idle : DeleteActividadState()
    object Loading : DeleteActividadState()
    object Success : DeleteActividadState()
    data class Error(val message: String) : DeleteActividadState()
}

sealed class UpdateActividadState {
    object Idle : UpdateActividadState()
    object Loading : UpdateActividadState()
    data class Success(val actividad: ActividadModel) : UpdateActividadState()
    data class Error(val message: String) : UpdateActividadState()
}
