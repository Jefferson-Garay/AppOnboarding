package dev.jeff.apponboarding.data.repository

import dev.jeff.apponboarding.data.model.UsuarioModel
import dev.jeff.apponboarding.data.remote.RetrofitInstance

class UsuarioRepository {

    suspend fun login(correo: String, password: String): UsuarioModel? {
        val usuarios = RetrofitInstance.api.getUsuarios()

        return usuarios.find { it.correo == correo && it.passwordHash == password }
    }
}
