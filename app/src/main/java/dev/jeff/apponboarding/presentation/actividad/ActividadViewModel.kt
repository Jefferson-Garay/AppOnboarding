package dev.jeff.apponboarding.presentation.actividad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.ActividadModel
import dev.jeff.apponboarding.data.model.ActividadRequest
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.repository.ActividadRepository
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ActividadesState {
    object Loading : ActividadesState()
    data class Success(val actividades: List<ActividadModel>) : ActividadesState()
    data class Error(val message: String) : ActividadesState()
}

class ActividadViewModel(
    private val repository: ActividadRepository,
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _actividadesState = MutableStateFlow<ActividadesState>(ActividadesState.Loading)
    val actividadesState: StateFlow<ActividadesState> = _actividadesState

    private val _mensajeEmergente = MutableStateFlow<ActividadModel?>(null)
    val mensajeEmergente: StateFlow<ActividadModel?> = _mensajeEmergente.asStateFlow()

    // Estado para la lista de usuarios (para el selector)
    private val _usuariosState = MutableStateFlow<List<UsuarioModel>>(emptyList())
    val usuariosState: StateFlow<List<UsuarioModel>> = _usuariosState

    // Notificaciones (pendientes)
    private val _pendientesCount = MutableStateFlow(0)
    val pendientesCount: StateFlow<Int> = _pendientesCount

    private val _notificacionesState = MutableStateFlow<List<ActividadModel>>(emptyList())
    val notificacionesState: StateFlow<List<ActividadModel>> = _notificacionesState

    // Cargar usuarios para el Dropdown
    fun loadUsuarios() {
        viewModelScope.launch {
            try {
                val usuarios = usuarioRepository.getUsuarios()
                _usuariosState.value = usuarios
            } catch (e: Exception) {
                // Si falla, lista vacía
                _usuariosState.value = emptyList()
            }
        }
    }

    fun loadActividadesByUsuario(usuarioRef: String) {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            try {
                val actividades = repository.getActividadesByUsuario(usuarioRef)
                _actividadesState.value = ActividadesState.Success(actividades)

                // Filtrar pendientes
                val pendientes = actividades.filter {
                    it.estado != "completada" && it.estado != "VISTO"
                }
                _notificacionesState.value = pendientes
                _pendientesCount.value = pendientes.size

                // Buscar mensaje emergente (POPUP) no visto
                val nuevosMensajes = actividades.filter {
                    it.tipo.startsWith("MSG_") && it.estado == "PENDIENTE"
                }
                if (nuevosMensajes.isNotEmpty()) {
                    _mensajeEmergente.value = nuevosMensajes.first()
                }

            } catch (e: Exception) {
                _actividadesState.value = ActividadesState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun loadAllActividades() {
        viewModelScope.launch {
            _actividadesState.value = ActividadesState.Loading
            try {
                // Aquí usamos el método correcto del repositorio: getActividades()
                val actividades = repository.getActividades()
                _actividadesState.value = ActividadesState.Success(actividades)
            } catch (e: Exception) {
                _actividadesState.value = ActividadesState.Error(e.message ?: "Error al cargar actividades")
            }
        }
    }

    fun createActividad(actividad: ActividadRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val result = repository.createActividad(actividad)
                if (result != null) {
                    onSuccess()
                    // Recargar lista si es necesario
                    loadAllActividades()
                }
            } catch (e: Exception) {
                _actividadesState.value = ActividadesState.Error(e.message ?: "Error al crear actividad")
            }
        }
    }

    fun cambiarEstadoActividad(actividadId: String, actividad: ActividadModel, nuevoEstado: String, usuarioRef: String) {
        viewModelScope.launch {
            try {
                val success = repository.updateEstadoActividad(actividadId, nuevoEstado)
                if (success) {
                    // Si es un mensaje emergente, cerrarlo
                    if (_mensajeEmergente.value?.id == actividadId) {
                        _mensajeEmergente.value = null
                    }
                    loadActividadesByUsuario(usuarioRef)
                }
            } catch (e: Exception) {
                // Manejo de error silencioso
            }
        }
    }

    fun marcarMensajeVisto(actividad: ActividadModel) {
        viewModelScope.launch {
            try {
                val usuarioRef = actividad.usuarioRef ?: return@launch
                repository.updateEstadoActividad(actividad.id ?: "", "VISTO")
                _mensajeEmergente.value = null
                // Actualizar lista para que baje el contador
                loadActividadesByUsuario(usuarioRef)
            } catch (e: Exception) {
                _mensajeEmergente.value = null
            }
        }
    }
}