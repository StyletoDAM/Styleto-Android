package tn.esprit.labasniandroid.ui.screen.store.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.Conversation
import tn.esprit.labasniandroid.models.repositories.ChatRepository

class MessagingViewModel(
    private val chatRepository: ChatRepository = ChatRepository.getInstance()
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private var currentUserId: String? = null
    
    // Track des conversations marquées comme lues localement
    private val readConversationIds = mutableSetOf<String>()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Observer les conversations pour mettre à jour le compteur automatiquement
        viewModelScope.launch {
            _conversations.collect { conversations ->
                currentUserId?.let { userId ->
                    updateUnreadCount(conversations, userId)
                }
            }
        }
    }

    fun initialize(token: String, userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Connecter le socket
            chatRepository.connectSocket(token, userId).fold(
                onSuccess = {
                    // Charger les conversations
                    loadConversations(token, userId)
                },
                onFailure = { error ->
                    _errorMessage.value = when (error) {
                        is NetworkError.ServerMessage -> error.serverMessage
                        is NetworkError.Transport -> "Erreur de connexion: ${error.error.message}"
                        else -> error.message ?: "Erreur inconnue"
                    }
                    _isLoading.value = false
                }
            )
        }
    }

    private fun loadConversations(token: String, userId: String) {
        viewModelScope.launch {
            chatRepository.getMyConversations(token).fold(
                onSuccess = { convs ->
                    _conversations.value = convs
                    // Calculer le nombre de messages non lus
                    // Note: readConversationIds persiste même après rechargement
                    updateUnreadCount(convs, userId)
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _errorMessage.value = when (error) {
                        is NetworkError.ServerMessage -> error.serverMessage
                        is NetworkError.Transport -> "Erreur lors du chargement: ${error.error.message}"
                        else -> error.message ?: "Erreur inconnue"
                    }
                    _isLoading.value = false
                }
            )
        }
    }

    private fun updateUnreadCount(conversations: List<Conversation>, userId: String) {
        // Compter les conversations avec des messages non lus
        // Une conversation a des messages non lus si :
        // 1. Elle a un dernier message qui n'est pas de l'utilisateur
        // 2. Elle n'est pas dans la liste des conversations marquées comme lues
        var count = 0
        val userIdNormalized = userId.trim().lowercase()
        
        conversations.forEach { conversation ->
            // Ignorer les conversations marquées comme lues
            if (conversation.id in readConversationIds) {
                return@forEach
            }
            
            conversation.lastMessage?.let { lastMessage ->
                val senderIdNormalized = lastMessage.senderId.trim().lowercase()
                // Si le dernier message n'est pas de l'utilisateur, on considère qu'il y a au moins 1 message non lu
                if (senderIdNormalized != userIdNormalized && senderIdNormalized.isNotEmpty()) {
                    count++
                }
            }
        }
        _unreadCount.value = count
    }

    /**
     * Marque une conversation comme lue localement
     * Cela fait que la conversation ne sera plus comptée dans les messages non lus
     */
    fun markConversationAsRead(conversationId: String, userId: String) {
        viewModelScope.launch {
            // Ajouter la conversation à la liste des conversations lues
            readConversationIds.add(conversationId)
            // Recalculer le compteur (qui exclura maintenant cette conversation)
            updateUnreadCount(_conversations.value, userId)
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.disconnectSocket()
    }
}
