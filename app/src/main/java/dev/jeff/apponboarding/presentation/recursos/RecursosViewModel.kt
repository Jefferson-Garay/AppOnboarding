package dev.jeff.apponboarding.presentation.recursos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.Recurso
import dev.jeff.apponboarding.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecursosViewModel : ViewModel() {

    private val _recursosAgrupados = MutableStateFlow<Map<String, List<Recurso>>>(emptyMap())
    val recursosAgrupados = _recursosAgrupados.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // init { // ELIMINADO: La carga ya no es automática.
    //     cargarRecursos()
    // }

    fun cargarRecursos() {
        viewModelScope.launch {
            try {
                val groupedData = withContext(Dispatchers.IO) {
                    val response = ApiClient.instance.getRecursos()
                    if (response.isSuccessful) {
                        val recursosActivos = response.body()?.filter { it.estado == "Activo" } ?: emptyList()
                        recursosActivos
                            .filter { !it.tipo.isNullOrBlank() }
                            .groupBy { it.tipo!! }
                    } else {
                        null
                    }
                }

                if (groupedData != null && groupedData.isNotEmpty()) {
                    _recursosAgrupados.value = groupedData
                    _error.value = null
                } else {
                     _error.value = "No se pudieron cargar los recursos."
                }

            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            }
        }
    }
}