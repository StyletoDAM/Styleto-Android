package tn.esprit.labasniandroid.ui.screen.store.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.Message
import tn.esprit.labasniandroid.models.repositories.ChatRepository

class ChatDetailViewModel(
    private val chatRepository: ChatRepository = ChatRepository.getInstance()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _conversationId = MutableStateFlow<String?>(null)
    val conversationId: StateFlow<String?> = _conversationId.asStateFlow()
    
    private var pollingJob: Job? = null
    private var currentToken: String? = null

    init {
        // Observer les messages du repository
        viewModelScope.launch {
            chatRepository.messages.collect { msgs ->
                _messages.value = msgs
            }
        }
        // Observer la connexion socket
        viewModelScope.launch {
            chatRepository.isConnected.collect { connected ->
                _isConnected.value = connected
            }
        }
    }

    fun initializeChat(token: String, userId: String, ownerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            currentToken = token

            // Connecter le socket
            chatRepository.connectSocket(token, userId).fold(
                onSuccess = {
                    // Créer ou récupérer la conversation
                    chatRepository.createConversation(token, ownerId).fold(
                        onSuccess = { conversation ->
                            _conversationId.value = conversation.id
                            // Joindre la conversation via socket
                            chatRepository.joinConversation(conversation.id)
                            // Charger les messages historiques
                            loadMessages(token, conversation.id)
                            // Démarrer le polling pour les nouveaux messages
                            startPolling(token, conversation.id)
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
                },
                onFailure = { error ->
                    _errorMessage.value = when (error) {
                        is NetworkError.ServerMessage -> error.serverMessage
                        is NetworkError.Transport -> "Erreur de connexion socket: ${error.error.message}"
                        else -> error.message ?: "Erreur inconnue"
                    }
                    _isLoading.value = false
                }
            )
        }
    }
    
    /**
     * Démarrer le polling périodique pour vérifier les nouveaux messages
     * (nécessaire car le backend n'émet pas via Socket.IO quand un message est créé via REST)
     */
    private fun startPolling(token: String, conversationId: String) {
        stopPolling() // Arrêter le polling précédent si existant
        
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(2000) // Poll toutes les 2 secondes
                chatRepository.checkForNewMessages(token, conversationId).fold(
                    onSuccess = { hasNewMessages ->
                        if (hasNewMessages) {
                        }
                    },
                    onFailure = { error ->
                        // Ne pas afficher d'erreur pour le polling, juste logger
                    }
                )
            }
        }
    }
    
    /**
     * Arrêter le polling
     */
    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun loadMessages(token: String, conversationId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(token, conversationId).fold(
                onSuccess = { msgs ->
                    _messages.value = msgs
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _errorMessage.value = when (error) {
                        is NetworkError.ServerMessage -> error.serverMessage
                        is NetworkError.Transport -> "Erreur lors du chargement des messages: ${error.error.message}"
                        else -> error.message ?: "Erreur inconnue"
                    }
                    _isLoading.value = false
                }
            )
        }
    }

    fun sendMessage(token: String, userId: String, content: String) {
        // ⭐ CORRECTION : Envoyer soit via socket, soit via REST (comme iOS), mais pas les deux !
        val convId = _conversationId.value
        if (convId == null) {
            _errorMessage.value = "Aucune conversation active"
            return
        }

        if (content.isBlank()) {
            return
        }

        viewModelScope.launch {
            // Récupérer les infos de l'utilisateur pour l'optimistic update
            val senderName: String? = null // Peut être récupéré depuis le profil si disponible
            val senderAvatar: String? = null // Peut être récupéré depuis le profil si disponible
            
            // ⭐ CORRECTION : Envoyer soit via socket, soit via REST (comme iOS ligne 139-145), mais pas les deux !
            if (_isConnected.value) {
                // Socket connecté → envoyer via socket uniquement
                chatRepository.sendMessage(convId, content, userId, senderName, senderAvatar)
            } else {
                // Socket déconnecté → envoyer via REST uniquement (fallback)
                chatRepository.sendMessageViaRest(token, convId, content).fold(
                    onSuccess = { message ->
                    },
                    onFailure = { error ->
                        _errorMessage.value = when (error) {
                            is NetworkError.ServerMessage -> error.serverMessage
                            is NetworkError.Transport -> "Erreur lors de l'envoi: ${error.error.message}"
                            else -> error.message ?: "Erreur inconnue"
                        }
                    }
                )
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
        // Ne pas déconnecter le socket car il peut être utilisé par d'autres ViewModels
        // chatRepository.disconnectSocket()
    }
}

