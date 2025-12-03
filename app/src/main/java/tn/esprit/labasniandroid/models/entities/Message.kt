package tn.esprit.labasniandroid.models.entities

data class ExtractedInfo(
    val phoneNumbers: List<String>? = null,
    val addresses: List<String>? = null,
    val emails: List<String>? = null,
    val urls: List<String>? = null
)

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val content: String,
    val readAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val extractedInfo: ExtractedInfo? = null
)

