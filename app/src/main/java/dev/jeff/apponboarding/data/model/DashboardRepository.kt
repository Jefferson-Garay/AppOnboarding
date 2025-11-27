package dev.jeff.apponboarding.data.model

import dev.jeff.apponboarding.data.remote.actividad.ActividadService

class DashboardRepository(private val service: ActividadService) {
    suspend fun resumenGlobal() = service.getResumenGlobal()
    suspend fun resumenUsuario(user: String) = service.getResumenUsuario(user)
}
