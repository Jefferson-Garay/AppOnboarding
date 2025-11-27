package dev.jeff.apponboarding.data.repository

import android.util.Log
import dev.jeff.apponboarding.data.model.ConversationHistoryItem
import dev.jeff.apponboarding.data.remote.RetrofitInstance
import retrofit2.HttpException

class HistoryRepository {

    private val historyApi = RetrofitInstance.historyApi

    suspend fun getHistory(
        usuarioRef: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): List<ConversationHistoryItem> {
        return try {
            historyApi.getHistory(usuarioRef, startDate, endDate)
        } catch (e: HttpException) {
            Log.e("HISTORY", "Error HTTP fetching history: ${e.code()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("HISTORY", "Error fetching history: ${e.message}")
            emptyList()
        }
    }
}
