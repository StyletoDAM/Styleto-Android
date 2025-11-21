package tn.esprit.labasniandroid.models.entities

data class Conversation(
    val id: String,
    val participants: List<String>,
    val participantNames: Map<String, String> = emptyMap(),
    val participantAvatars: Map<String, String> = emptyMap(),
    val lastMessage: Message? = null,
    val messages: List<Message> = emptyList(),
    val isGroup: Boolean = false,
    val title: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

