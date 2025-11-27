package dev.jeff.apponboarding.presentation.mensaje

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.*
import dev.jeff.apponboarding.data.repository.MensajeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MensajeViewModel : ViewModel() {

    private val repository = MensajeRepository()

    private val _mensajesState = MutableStateFlow<List<MensajeProgramadoModel>>(emptyList())
    val mensajesState: StateFlow<List<MensajeProgramadoModel>> = _mensajesState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _opSuccess = MutableStateFlow<String?>(null)
    val opSuccess: StateFlow<String?> = _opSuccess

    fun loadMensajes(usuarioRef: String) {
        if (usuarioRef.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _mensajesState.value = repository.getMensajes(usuarioRef)
            _isLoading.value = false
        }
    }

    fun crearMensaje(
        titulo: String,
        descripcion: String,
        tipo: TipoMensaje,
        fecha: String,
        hora: String,
        canal: CanalEnvio,
        condicion: CondicionActivacion,
        usuarioRef: String
    ) {
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
                    _opSuccess.value = "Mensaje guardado correctamente"
                    loadMensajes(usuarioRef)
                }
            } catch (e: Exception) {
                // Error silencioso por ahora
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarMensaje(id: String, usuarioRef: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.deleteMensaje(id)
            if (success) loadMensajes(usuarioRef)
            _isLoading.value = false
        }
    }

    fun clearSuccessMessage() {
        _opSuccess.value = null
    }
}