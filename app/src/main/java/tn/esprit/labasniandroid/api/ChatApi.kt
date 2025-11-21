package tn.esprit.labasniandroid.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {
    @POST("/chat/conversations")
    suspend fun createConversation(
        @Header("Authorization") token: String,
        @Body request: CreateConversationRequest
    ): Response<ConversationResponse>

    @GET("/chat/conversations")
    suspend fun getMyConversations(
        @Header("Authorization") token: String
    ): Response<List<ConversationResponse>>

    @GET("/chat/conversations/{id}/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("id") conversationId: String
    ): Response<List<MessageResponse>>

    @POST("/chat/messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body request: SendMessageRequest
    ): Response<MessageResponse>
}

data class CreateConversationRequest(
    @SerializedName("participantId") val participantId: String
)

data class SendMessageRequest(
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("content") val content: String
)

data class ConversationResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("participants") val participants: List<Any>,
    @SerializedName("lastMessage") val lastMessage: Any?,
    @SerializedName("isGroup") val isGroup: Boolean?,
    @SerializedName("title") val title: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("messages") val messages: List<MessageResponse>? = null
)

data class MessageResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("conversationId") val conversationId: Any,
    @SerializedName("senderId") val senderId: Any,
    @SerializedName("content") val content: String,
    @SerializedName("readAt") val readAt: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

