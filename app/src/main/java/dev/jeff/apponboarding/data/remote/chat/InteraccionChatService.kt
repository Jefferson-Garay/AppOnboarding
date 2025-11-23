package dev.jeff.apponboarding.data.remote.chat


import dev.jeff.apponboarding.data.model.ChatRequest
import dev.jeff.apponboarding.data.model.ChatResponse
import dev.jeff.apponboarding.data.model.RenderIpResponse
import retrofit2.http.*

interface InteraccionChatService {

    // Obtener IP de render
    @GET("InteraccionChat/render-ip")
    suspend fun getRenderIp(): RenderIpResponse

    // Enviar mensaje al chatbot
    @POST("InteraccionChat/chat")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}