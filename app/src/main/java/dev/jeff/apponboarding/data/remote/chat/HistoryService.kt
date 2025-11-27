package dev.jeff.apponboarding.data.remote.chat

import dev.jeff.apponboarding.data.model.ConversationHistoryItem
import retrofit2.http.GET
import retrofit2.http.Query

interface HistoryService {

    // Modificado: Cambiamos de InteraccionChat/history a salas/history si es necesario
    // O mantenemos el endpoint pero verificamos si el backend espera un parametro especifico
    // segun la estructura de mongo, parece que estamos buscando interacciones, no salas.
    
    @GET("InteraccionChat/history")
    suspend fun getHistory(
        @Query("usuarioRef") usuarioRef: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): List<ConversationHistoryItem>
}
