package dev.jeff.apponboarding.presentation.mensaje

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.*
import dev.jeff.apponboarding.data.repository.MensajeRepository
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MensajeViewModel : ViewModel() {

    private val repository = MensajeRepository()
    private val usuarioRepository = UsuarioRepository()

    // Lista de mensajes del usuario seleccionado
    private val _mensajesState = MutableStateFlow<List<MensajeProgramadoModel>>(emptyList())
    val mensajesState: StateFlow<List<MensajeProgramadoModel>> = _mensajesState

    // Lista de empleados disponibles para seleccionar
    private val _empleadosState = MutableStateFlow<List<UsuarioModel>>(emptyList())
    val empleadosState: StateFlow<List<UsuarioModel>> = _empleadosState

    // Empleado actualmente seleccionado
    private val _selectedUsuario = MutableStateFlow<UsuarioModel?>(null)
    val selectedUsuario: StateFlow<UsuarioModel?> = _selectedUsuario

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _opSuccess = MutableStateFlow<String?>(null)
    val opSuccess: StateFlow<String?> = _opSuccess

    init {
        loadEmpleados()
    }

    // Cargar la lista de todos los empleados para el Dropdown
    fun loadEmpleados() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usuarios = usuarioRepository.getUsuarios()
                _empleadosState.value = usuarios
            } catch (e: Exception) {
                // Silencioso
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Seleccionar un usuario y cargar sus mensajes
    fun selectUsuario(usuario: UsuarioModel) {
        _selectedUsuario.value = usuario
        val idReal = usuario.obtenerIdReal()

        if (idReal.length == 24) {
            loadMensajes(idReal)
        } else {
            // Feedback inmediato si el ID está corrupto
            _mensajesState.value = emptyList()
            // No mostramos error invasivo aquí, solo en el log o si intenta crear
        }
    }

    fun loadMensajes(usuarioRef: String) {
        if (usuarioRef.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val mensajes = repository.getMensajes(usuarioRef)
                _mensajesState.value = mensajes
            } catch (e: Exception) {
                _mensajesState.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun crearMensaje(
        titulo: String,
        descripcion: String,
        tipo: TipoMensaje,
        fecha: String,
        hora: String,
        canal: CanalEnvio,
        condicion: CondicionActivacion
    ) {
        val usuarioActual = _selectedUsuario.value
        val usuarioRef = usuarioActual?.obtenerIdReal()

        // VALIDACIÓN ESTRICTA DEL ID ANTES DE ENVIAR
        if (usuarioRef.isNullOrBlank() || usuarioRef.length != 24) {
            _opSuccess.value = "Error: ID de usuario inválido (${usuarioRef?.length ?: 0} caracteres). Contacte soporte."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Aseguramos formato ISO con segundos: yyyy-MM-ddTHH:mm:00
                val horaLimpia = if (hora.count { it == ':' } == 1) "$hora:00" else hora
                val fechaIso = "${fecha}T${horaLimpia}"

                val nuevoMensaje = MensajeProgramadoModel(
                    titulo = titulo,
                    descripcion = descripcion,
                    tipo = tipo,
                    fechaProgramada = fechaIso,
                    canal = canal,
                    condicionActivacion = condicion
                )

                val errorMsg = repository.crearMensaje(nuevoMensaje, usuarioRef)

                if (errorMsg == null) {
                    _opSuccess.value = "Mensaje programado para ${usuarioActual.nombre}"
                    loadMensajes(usuarioRef)
                } else {
                    _opSuccess.value = "Fallo: $errorMsg"
                }
            } catch (e: Exception) {
                _opSuccess.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarMensaje(idMensaje: String) {
        val usuarioActual = _selectedUsuario.value
        val usuarioRef = usuarioActual?.obtenerIdReal() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.deleteMensaje(idMensaje)
                if (success) {
                    _opSuccess.value = "Mensaje eliminado"
                    loadMensajes(usuarioRef)
                } else {
                    _opSuccess.value = "No se pudo eliminar el mensaje"
                }
            } catch (e: Exception) {
                _opSuccess.value = "Error al eliminar"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSuccessMessage() {
        _opSuccess.value = null
    }
}