package dev.jeff.apponboarding.presentation.actividad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.Actividad
import dev.jeff.apponboarding.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActividadViewModel : ViewModel() {

    private val _actividades = MutableStateFlow<List<Actividad>>(emptyList())
    val actividades = _actividades.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso = _progreso.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun cargarActividades() {
        viewModelScope.launch {
            _error.value = null
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.instance.getActividades()
                }
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    _actividades.value = lista
                    calcularProgreso(lista)
                } else {
                    _error.value = "No se pudo cargar el progreso. Intente nuevamente."
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            }
        }
    }

    private fun calcularProgreso(lista: List<Actividad>) {
        if (lista.isEmpty()) {
            _progreso.value = 0f
            return
        }
        val completadas = lista.count { it.isCompleted() }
        _progreso.value = completadas.toFloat() / lista.size.toFloat()
    }

    // --- FUNCIÓN ACTUALIZADA CON PERSISTENCIA ---
    fun toggleTask(actividad: Actividad) {
        // Guarda de seguridad para IDs nulos.
        if (actividad.id == null) return

        // 1. Lógica de actualización visual (Optimistic Update).
        val nuevaLista = _actividades.value.map {
            if (it.id == actividad.id) {
                val nuevoEstado = if (it.isCompleted()) "Pendiente" else "Completada"
                it.copy(estado = nuevoEstado)
            } else {
                it
            }
        }
        _actividades.value = nuevaLista
        calcularProgreso(nuevaLista)

        // 2. Lanzar corrutina para guardar el cambio en el backend.
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Encontrar el objeto actualizado para enviarlo a la API.
                val actividadActualizada = nuevaLista.find { it.id == actividad.id }!!

                // Llamar al endpoint de Retrofit.
                ApiClient.instance.updateActividad(actividad.id, actividadActualizada)

                // Si llegamos aquí, todo ha ido bien.
                println("Actividad ${actividad.id} guardada en el backend.")

            } catch (e: Exception) {
                // 3. Manejo de error si falla la conexión.
                println("Error al guardar en backend: ${e.message}")
                // Opcional: Podrías revertir el cambio en la UI o mostrar un Snackbar de error.
            }
        }
    }
}