package tn.esprit.labasniandroid.models.entities

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val content: String,
    val readAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

