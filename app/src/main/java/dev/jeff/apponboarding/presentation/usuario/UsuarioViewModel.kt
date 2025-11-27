package dev.jeff.apponboarding.presentation.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.model.RolModel
import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.model.UsuarioRequest
import dev.jeff.apponboarding.data.repository.RolRepository
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {

    private val repository = UsuarioRepository()
    private val rolRepository = RolRepository()

    private val _usuariosState = MutableStateFlow<List<UsuarioModel>>(emptyList())
    val usuariosState: StateFlow<List<UsuarioModel>> = _usuariosState

    private val _rolesState = MutableStateFlow<List<RolModel>>(emptyList())
    val rolesState: StateFlow<List<RolModel>> = _rolesState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _opStatus = MutableStateFlow<String?>(null)
    val opStatus: StateFlow<String?> = _opStatus

    fun loadUsuarios() {
        viewModelScope.launch {
            _isLoading.value = true
            _usuariosState.value = repository.getUsuarios()
            _isLoading.value = false
        }
    }

    fun loadRoles() {
        viewModelScope.launch {
            _rolesState.value = rolRepository.getRoles()
        }
    }

    fun createUsuario(usuario: UsuarioRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.createUsuario(usuario)
            if (success) {
                _opStatus.value = "Usuario creado exitosamente"
                loadUsuarios()
            } else {
                _opStatus.value = "Error al crear usuario"
            }
            _isLoading.value = false
        }
    }

    fun updateUsuario(id: String, usuario: UsuarioRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.updateUsuario(id, usuario)
            if (success) {
                _opStatus.value = "Usuario actualizado"
                loadUsuarios()
            } else {
                _opStatus.value = "Error al actualizar"
            }
            _isLoading.value = false
        }
    }

    fun deleteUsuario(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.deleteUsuario(id)
            if (success) {
                loadUsuarios()
                _opStatus.value = "Usuario eliminado"
            } else {
                _opStatus.value = "Error al eliminar"
            }
            _isLoading.value = false
        }
    }

    fun clearStatus() {
        _opStatus.value = null
    }

    suspend fun getUsuarioById(id: String): UsuarioModel? {
        return repository.getUsuarioById(id)
    }
}