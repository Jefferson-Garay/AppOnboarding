package dev.jeff.apponboarding.data.remote.chat

import dev.jeff.apponboarding.data.model.ConversationHistoryItem
import retrofit2.http.GET
import retrofit2.http.Query

interface HistoryService {

    @GET("InteraccionChat/history")
    suspend fun getHistory(
        @Query("usuarioRef") usuarioRef: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): List<ConversationHistoryItem>
}
