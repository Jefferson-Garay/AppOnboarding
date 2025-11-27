package dev.jeff.apponboarding.presentation.Dash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.jeff.apponboarding.data.model.ResumenGlobalResponse
import dev.jeff.apponboarding.data.model.ResumenUsuarioResponse
import dev.jeff.apponboarding.data.repository.ActividadRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: ActividadRepository // ✅ Cambiado: Usa Repository, no Service
) : ViewModel() {

    // Estados de datos
    private val _globalResumen = MutableStateFlow<ResumenGlobalResponse?>(null)
    val globalResumen = _globalResumen.asStateFlow()

    private val _usuarioResumen = MutableStateFlow<ResumenUsuarioResponse?>(null)
    val usuarioResumen = _usuarioResumen.asStateFlow()

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun cargarDashboard(usuarioRef: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Lanza ambas peticiones en paralelo para que sea más rápido
                coroutineScope {
                    val globalDeferred = async { repository.getResumenGlobal() }
                    val usuarioDeferred = async { repository.getResumenUsuario(usuarioRef) }

                    _globalResumen.value = globalDeferred.await()
                    _usuarioResumen.value = usuarioDeferred.await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al cargar datos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Factory actualizada para aceptar Repository
    companion object {
        fun provideFactory(repository: ActividadRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DashboardViewModel(repository)
            }
        }
    }
}