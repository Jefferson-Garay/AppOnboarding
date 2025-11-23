package dev.jeff.apponboarding.presentation.rol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.RolModel
import dev.jeff.apponboarding.data.model.RolRequest
import dev.jeff.apponboarding.data.repository.RolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RolViewModel(
    private val repository: RolRepository
) : ViewModel() {

    private val _rolesState = MutableStateFlow<RolesState>(RolesState.Idle)
    val rolesState: StateFlow<RolesState> = _rolesState

    private val _createState = MutableStateFlow<CreateRolState>(CreateRolState.Idle)
    val createState: StateFlow<CreateRolState> = _createState

    private val _deleteState = MutableStateFlow<DeleteRolState>(DeleteRolState.Idle)
    val deleteState: StateFlow<DeleteRolState> = _deleteState

    private val _updateState = MutableStateFlow<UpdateRolState>(UpdateRolState.Idle)
    val updateState: StateFlow<UpdateRolState> = _updateState

    // Cargar todos los roles
    fun loadRoles() {
        viewModelScope.launch {
            _rolesState.value = RolesState.Loading
            val roles = repository.getRoles()
            _rolesState.value = RolesState.Success(roles)
        }
    }

    // Crear rol
    fun createRol(rol: RolRequest) {
        viewModelScope.launch {
            _createState.value = CreateRolState.Loading
            val result = repository.createRol(rol)
            if (result != null) {
                _createState.value = CreateRolState.Success(result)
            } else {
                _createState.value = CreateRolState.Error("Error al crear el rol")
            }
        }
    }

    // Actualizar rol
    fun updateRol(id: String, rol: RolRequest) {
        viewModelScope.launch {
            _updateState.value = UpdateRolState.Loading
            val result = repository.updateRol(id, rol)
            if (result != null) {
                _updateState.value = UpdateRolState.Success(result)
            } else {
                _updateState.value = UpdateRolState.Error("Error al actualizar el rol")
            }
        }
    }

    // Eliminar rol
    fun deleteRol(id: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteRolState.Loading
            val success = repository.deleteRol(id)
            if (success) {
                _deleteState.value = DeleteRolState.Success
            } else {
                _deleteState.value = DeleteRolState.Error("Error al eliminar el rol")
            }
        }
    }

    // Resetear estados
    fun resetCreateState() {
        _createState.value = CreateRolState.Idle
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteRolState.Idle
    }

    fun resetUpdateState() {
        _updateState.value = UpdateRolState.Idle
    }
}

// Estados para la lista de roles
sealed class RolesState {
    object Idle : RolesState()
    object Loading : RolesState()
    data class Success(val roles: List<RolModel>) : RolesState()
    data class Error(val message: String) : RolesState()
}

// Estados para crear rol
sealed class CreateRolState {
    object Idle : CreateRolState()
    object Loading : CreateRolState()
    data class Success(val rol: RolModel) : CreateRolState()
    data class Error(val message: String) : CreateRolState()
}

// Estados para eliminar rol
sealed class DeleteRolState {
    object Idle : DeleteRolState()
    object Loading : DeleteRolState()
    object Success : DeleteRolState()
    data class Error(val message: String) : DeleteRolState()
}

// Estados para actualizar rol
sealed class UpdateRolState {
    object Idle : UpdateRolState()
    object Loading : UpdateRolState()
    data class Success(val rol: RolModel) : UpdateRolState()
    data class Error(val message: String) : UpdateRolState()
}