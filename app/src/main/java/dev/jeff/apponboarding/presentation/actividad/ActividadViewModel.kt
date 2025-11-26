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

    private val _notificacionesState = MutableStateFlow<List<ActividadModel>>(emptyList())
    val notificacionesState: StateFlow<List<ActividadModel>> = _notificacionesState

    private val _pendientesCount = MutableStateFlow(0)
    val pendientesCount: StateFlow<Int> = _pendientesCount

    private val _createState = MutableStateFlow<CreateActividadState>(CreateActividadState.Idle)
    val createState: StateFlow<CreateActividadState> = _createState

    private val _deleteState = MutableStateFlow<DeleteActividadState>(DeleteActividadState.Idle)
    val deleteState: StateFlow<DeleteActividadState> = _deleteState

    private val _updateState = MutableStateFlow<UpdateActividadState>(UpdateActividadState.Idle)
    val updateState: StateFlow<UpdateActividadState> = _updateState

    private val _mensajeEmergente = MutableStateFlow<ActividadModel?>(null)
    val mensajeEmergente: StateFlow<ActividadModel?> = _mensajeEmergente

    private fun List<ActividadModel>.filtrarTareasReales(): List<ActividadModel> {
        return this.filter { !it.tipo.startsWith("MSG_") }
    }

    fun loadActividadesByUsuario(usuarioRef: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            try {
                val actividadesRaw = repository.getActividadesByUsuario(usuarioRef)

                // 1. Filtramos para obtener solo tareas reales
                val tareasReales = actividadesRaw.filtrarTareasReales()

                // Lista principal (ordenada)
                _actividadesState.value = ActividadesState.Success(tareasReales.sortedBy { it.fechaInicio })

                // 2. CORRECCIÓN: Notificaciones solo de tareas reales (para no inflar el contador)
                val notificacionesReales = tareasReales.sortedByDescending { it.fechaInicio }
                _notificacionesState.value = notificacionesReales

                // 3. CORRECCIÓN: Contador de pendientes usando solo la lista filtrada
                val pendientes = tareasReales.count { !it.estado.equals("Completada", ignoreCase = true) }
                _pendientesCount.value = pendientes

                // 4. Buscar mensajes para POP-UP (Aquí sí buscamos MSG_)
                val mensajeNuevo = actividadesRaw.firstOrNull {
                    it.tipo.startsWith("MSG_") &&
                            !it.estado.equals("VISTO", ignoreCase = true) &&
                            !it.estado.equals("COMPLETADA", ignoreCase = true)
                }
                _mensajeEmergente.value = mensajeNuevo

            } catch (e: Exception) {
                _actividadesState.value = ActividadesState.Error("Error al cargar actividades")
            }
        }
    }

    // Método corregido para respuesta instantánea
    fun marcarMensajeVisto(actividad: ActividadModel) {
        // 1. UI Optimista: Ocultar inmediatamente el popup
        _mensajeEmergente.value = null

        // 2. Actualizar en background
        viewModelScope.launch {
            val nuevoEstado = "VISTO"
            repository.updateEstadoActividad(actividad.id ?: "", "\"$nuevoEstado\"")
            // No recargamos toda la lista para evitar parpadeos innecesarios,
            // ya que el popup ya se cerró visualmente.
        }
    }

    fun loadPendientesCount(usuarioRef: String) {
        viewModelScope.launch {
            try {
                val actividadesRaw = repository.getActividadesPendientes(usuarioRef)
                val actividadesReales = actividadesRaw.filtrarTareasReales() // Filtrar también aquí
                _pendientesCount.value = actividadesReales.size
                _notificacionesState.value = actividadesReales.sortedByDescending { it.fechaInicio }
            } catch (e: Exception) {
                _pendientesCount.value = 0
            }
        }
    }

    // ... (Resto de métodos create, update, delete igual que antes) ...

    fun createActividad(actividad: ActividadRequest) {
        viewModelScope.launch {
            _createState.value = CreateActividadState.Loading
            val result = repository.createActividad(actividad)
            if (result != null) {
                _createState.value = CreateActividadState.Success(result)
            } else {
                _createState.value = CreateActividadState.Error("Error al crear")
            }
        }
    }

    fun cambiarEstadoActividad(actividadId: String, actividad: ActividadModel, nuevoEstado: String, usuarioRef: String) {
        viewModelScope.launch {
            val success = repository.updateEstadoActividad(actividadId, "\"$nuevoEstado\"")
            if (success) {
                loadActividadesByUsuario(usuarioRef)
            } else {
                val request = ActividadRequest(
                    titulo = actividad.titulo,
                    descripcion = actividad.descripcion,
                    tipo = actividad.tipo,
                    fechaInicio = actividad.fechaInicio,
                    fechaFin = actividad.fechaFin,
                    usuarioRef = actividad.usuarioRef,
                    estado = nuevoEstado
                )
                if (repository.updateActividad(actividadId, request) != null) {
                    loadActividadesByUsuario(usuarioRef)
                }
            }
        }
    }

    fun updateActividad(id: String, actividad: ActividadRequest) {
        viewModelScope.launch {
            _updateState.value = UpdateActividadState.Loading
            if (repository.updateActividad(id, actividad) != null) {
                _updateState.value = UpdateActividadState.Success(repository.getActividadById(id)!!)
            } else {
                _updateState.value = UpdateActividadState.Error("Fallo update")
            }
        }
    }

    fun deleteActividad(id: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteActividadState.Loading
            if (repository.deleteActividad(id)) {
                _deleteState.value = DeleteActividadState.Success
            } else {
                _deleteState.value = DeleteActividadState.Error("Error al eliminar")
            }
        }
    }

    fun resetCreateState() { _createState.value = CreateActividadState.Idle }
    fun resetDeleteState() { _deleteState.value = DeleteActividadState.Idle }
    fun resetUpdateState() { _updateState.value = UpdateActividadState.Idle }
}

// Sealed classes (iguales)
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