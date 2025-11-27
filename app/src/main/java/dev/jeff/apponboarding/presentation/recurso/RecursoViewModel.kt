package dev.jeff.apponboarding.presentation.recurso


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.RecursoModel
import dev.jeff.apponboarding.data.model.RecursoRequest
import dev.jeff.apponboarding.data.repository.RecursoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecursoViewModel(
    private val repository: RecursoRepository
) : ViewModel() {

    private val _recursosState = MutableStateFlow<RecursosState>(RecursosState.Idle)
    val recursosState: StateFlow<RecursosState> = _recursosState

    private val _createState = MutableStateFlow<CreateRecursoState>(CreateRecursoState.Idle)
    val createState: StateFlow<CreateRecursoState> = _createState

    private val _deleteState = MutableStateFlow<DeleteRecursoState>(DeleteRecursoState.Idle)
    val deleteState: StateFlow<DeleteRecursoState> = _deleteState

    private val _updateState = MutableStateFlow<UpdateRecursoState>(UpdateRecursoState.Idle)
    val updateState: StateFlow<UpdateRecursoState> = _updateState

    private val _estadoState = MutableStateFlow<UpdateEstadoState>(UpdateEstadoState.Idle)
    val estadoState: StateFlow<UpdateEstadoState> = _estadoState

    // Cargar todos los recursos
    fun loadRecursos() {
        viewModelScope.launch {
            _recursosState.value = RecursosState.Loading
            val recursos = repository.getRecursos()
            _recursosState.value = RecursosState.Success(recursos)
        }
    }

    // Cargar recursos por administrador
    fun loadRecursosByAdmin(adminId: String) {
        viewModelScope.launch {
            _recursosState.value = RecursosState.Loading
            val recursos = repository.getRecursosByAdmin(adminId)
            _recursosState.value = RecursosState.Success(recursos)
        }
    }

    // Cargar recursos por rango de fechas
    fun loadRecursosByFecha(desde: String, hasta: String) {
        viewModelScope.launch {
            _recursosState.value = RecursosState.Loading
            val recursos = repository.getRecursosByFecha(desde, hasta)
            _recursosState.value = RecursosState.Success(recursos)
        }
    }

    // Crear recurso
    fun createRecurso(recurso: RecursoRequest) {
        viewModelScope.launch {
            _createState.value = CreateRecursoState.Loading
            val result = repository.createRecurso(recurso)
            if (result != null) {
                _createState.value = CreateRecursoState.Success(result)
            } else {
                _createState.value = CreateRecursoState.Error("Error al crear el recurso")
            }
        }
    }

    // Actualizar recurso
    fun updateRecurso(id: String, recurso: RecursoRequest) {
        viewModelScope.launch {
            _updateState.value = UpdateRecursoState.Loading
            val result = repository.updateRecurso(id, recurso)
            if (result != null) {
                _updateState.value = UpdateRecursoState.Success(result)
            } else {
                _updateState.value = UpdateRecursoState.Error("Error al actualizar el recurso")
            }
        }
    }

    // Eliminar recurso
    fun deleteRecurso(id: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteRecursoState.Loading
            val success = repository.deleteRecurso(id)
            if (success) {
                _deleteState.value = DeleteRecursoState.Success
            } else {
                _deleteState.value = DeleteRecursoState.Error("Error al eliminar el recurso")
            }
        }
    }

    // Actualizar estado del recurso
    fun updateEstadoRecurso(id: String, estado: String) {
        viewModelScope.launch {
            _estadoState.value = UpdateEstadoState.Loading
            val result = repository.updateEstadoRecurso(id, estado)
            if (result != null) {
                _estadoState.value = UpdateEstadoState.Success(result)
            } else {
                _estadoState.value = UpdateEstadoState.Error("Error al actualizar el estado")
            }
        }
    }

    // Resetear estados
    fun resetCreateState() {
        _createState.value = CreateRecursoState.Idle
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteRecursoState.Idle
    }

    fun resetUpdateState() {
        _updateState.value = UpdateRecursoState.Idle
    }

    fun resetEstadoState() {
        _estadoState.value = UpdateEstadoState.Idle
    }
}

// Estados para la lista de recursos
sealed class RecursosState {
    object Idle : RecursosState()
    object Loading : RecursosState()
    data class Success(val recursos: List<RecursoModel>) : RecursosState()
    data class Error(val message: String) : RecursosState()
}

// Estados para crear recurso
sealed class CreateRecursoState {
    object Idle : CreateRecursoState()
    object Loading : CreateRecursoState()
    data class Success(val message: String) : CreateRecursoState()
    data class Error(val message: String) : CreateRecursoState()
}

// Estados para eliminar recurso
sealed class DeleteRecursoState {
    object Idle : DeleteRecursoState()
    object Loading : DeleteRecursoState()
    object Success : DeleteRecursoState()
    data class Error(val message: String) : DeleteRecursoState()
}

// Estados para actualizar recurso
sealed class UpdateRecursoState {
    object Idle : UpdateRecursoState()
    object Loading : UpdateRecursoState()
    data class Success(val message: String) : UpdateRecursoState()
    data class Error(val message: String) : UpdateRecursoState()
}

// Estados para actualizar estado
sealed class UpdateEstadoState {
    object Idle : UpdateEstadoState()
    object Loading : UpdateEstadoState()
    data class Success(val message: String) : UpdateEstadoState()
    data class Error(val message: String) : UpdateEstadoState()
}