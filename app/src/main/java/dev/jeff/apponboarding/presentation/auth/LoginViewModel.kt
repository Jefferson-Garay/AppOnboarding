package dev.jeff.apponboarding.presentation.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jeff.apponboarding.data.repository.UsuarioRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(   //Maneja toda la lógica de la interfaz del chat:
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(correo: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val user = repository.login(correo, password)

            if (user != null) {
                _loginState.value = LoginState.Success(user)
            } else {
                _loginState.value = LoginState.Error("Credenciales inválidas")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: Any) : LoginState()
    data class Error(val message: String) : LoginState()
}
