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
                // Filtramos para no mostrarnos a nosotros mismos si somos admin, o mostrar todos
                _empleadosState.value = usuarios
            } catch (e: Exception) {
                // Manejo de error silencioso al estilo "no pasó nada"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Seleccionar un usuario y cargar sus mensajes
    fun selectUsuario(usuario: UsuarioModel) {
        _selectedUsuario.value = usuario
        // Usamos obtenerIdReal() por si el ID viene raro de Mongo
        val idReal = usuario.obtenerIdReal()
        if (idReal.isNotBlank()) {
            loadMensajes(idReal)
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
        val usuarioRef = usuarioActual?.obtenerIdReal() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fechaIso = "${fecha}T${hora}:00"

                val nuevoMensaje = MensajeProgramadoModel(
                    titulo = titulo,
                    descripcion = descripcion,
                    tipo = tipo,
                    fechaProgramada = fechaIso,
                    canal = canal,
                    condicionActivacion = condicion
                )

                val success = repository.crearMensaje(nuevoMensaje, usuarioRef)
                if (success) {
                    _opSuccess.value = "Mensaje programado para ${usuarioActual.nombre}"
                    loadMensajes(usuarioRef)
                } else {
                    _opSuccess.value = "Error al guardar el mensaje"
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