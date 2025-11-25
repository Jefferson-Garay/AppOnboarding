package dev.jeff.apponboarding.data.remote.chat


import dev.jeff.apponboarding.data.model.ChatRequest
import dev.jeff.apponboarding.data.model.ChatResponse
import dev.jeff.apponboarding.data.model.RenderIpResponse
import retrofit2.http.*

interface InteraccionChatService {

    // Obtener IP de render (servidor)
    @GET("InteraccionChat/render-ip")
    suspend fun getRenderIp(): RenderIpResponse

    // Enviar mensaje al chatbot
    @POST("InteraccionChat/chat")  // FUNCIÓN PRINCIPAL: Envía mensaje y recibe respuesta del chatbot
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}