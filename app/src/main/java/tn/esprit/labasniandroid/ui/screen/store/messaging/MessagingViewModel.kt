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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun initialize(token: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Connecter le socket
            chatRepository.connectSocket(token, userId).fold(
                onSuccess = {
                    // Charger les conversations
                    loadConversations(token)
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

    private fun loadConversations(token: String) {
        viewModelScope.launch {
            chatRepository.getMyConversations(token).fold(
                onSuccess = { convs ->
                    _conversations.value = convs
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

    fun clearMessages() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.disconnectSocket()
    }
}
