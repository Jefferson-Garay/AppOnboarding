package dev.jeff.apponboarding.presentation.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConfiguracionViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState

    fun changePassword(userId: String, newPassword: String) {
        if (newPassword.isBlank()) {
            _changePasswordState.value = ChangePasswordState.Error("La contraseña no puede estar vacía")
            return
        }
        
        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordState.Loading
            val success = repository.changePassword(userId, newPassword)
            if (success) {
                _changePasswordState.value = ChangePasswordState.Success
            } else {
                _changePasswordState.value = ChangePasswordState.Error("Error al cambiar la contraseña")
            }
        }
    }

    fun resetState() {
        _changePasswordState.value = ChangePasswordState.Idle
    }
}

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}
