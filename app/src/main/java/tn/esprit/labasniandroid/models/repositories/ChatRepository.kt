package tn.esprit.labasniandroid.models.repositories

import com.google.gson.Gson
import com.google.gson.JsonElement
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import org.json.JSONArray
import tn.esprit.labasniandroid.api.ChatApi
import tn.esprit.labasniandroid.api.ConversationResponse
import tn.esprit.labasniandroid.api.CreateConversationRequest
import tn.esprit.labasniandroid.api.MessageResponse
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.SendMessageRequest
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.Conversation
import tn.esprit.labasniandroid.models.entities.Message
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.utils.APIConstants
import java.net.URISyntaxException

class ChatRepository(
    private val chatApi: ChatApi = RetrofitClient.chatApi,
    private val authApi: tn.esprit.labasniandroid.api.AuthApi = RetrofitClient.authApi
) {
    private var socket: Socket? = null
    private var currentConversationId: String? = null
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    companion object {
        @Volatile
        private var INSTANCE: ChatRepository? = null
        
        fun getInstance(chatApi: ChatApi = RetrofitClient.chatApi): ChatRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ChatRepository(chatApi).also { INSTANCE = it }
            }
        }
        
        fun reset() {
            INSTANCE?.disconnectSocket()
            INSTANCE = null
        }
    }

    fun connectSocket(token: String, userId: String): Result<Unit> {
        return try {
            // Si le socket est déjà connecté, ne pas créer une nouvelle connexion
            if (socket?.connected() == true) {
                // Si on a un conversationId, s'assurer qu'on est dans la room
                currentConversationId?.let { convId ->
                    socket?.emit("join-conversation", JSONObject().apply {
                        put("conversationId", convId)
                    })
                }
                return Result.success(Unit)
            }
            
            // Si le socket existe mais n'est pas connecté, le déconnecter d'abord
            socket?.disconnect()
            socket?.off()
            
            val socketUrl = "${APIConstants.BASE_URL}/chat"
            
            val options = IO.Options().apply {
                auth = mapOf("token" to token.replace("Bearer ", ""))
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 1000
            }
            
            socket = IO.socket(socketUrl, options)
            
            socket?.on(Socket.EVENT_CONNECT) {
                _isConnected.value = true
                
                // Si on a déjà un conversationId, rejoindre automatiquement
                currentConversationId?.let { convId ->
                    socket?.emit("join-conversation", JSONObject().apply {
                        put("conversationId", convId)
                    })
                } ?: run {
                }
            }
            
            socket?.on(Socket.EVENT_DISCONNECT) {
                _isConnected.value = false
            }
            
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
            }
            
            socket?.on("new-message") { args ->
                try {
                    
                    val messageJson = args[0] as? JSONObject
                    if (messageJson != null) {
                        val message = parseMessage(messageJson)
                        
                        // Normaliser les conversationId pour la comparaison
                        val normalizedMessageConvId = message.conversationId.trim().lowercase()
                        val currentConvId = currentConversationId // Copie locale pour éviter le smart cast
                        val normalizedCurrentConvId = currentConvId?.trim()?.lowercase()
                        
                        
                        // Filtrer par conversationId pour n'afficher que les messages de la conversation active
                        if (normalizedCurrentConvId != null && normalizedMessageConvId != normalizedCurrentConvId) {
                            return@on
                        }
                        
                        // Vérifier si le message existe déjà par ID (évite les doublons)
                        val existingMessageById = _messages.value.find { it.id == message.id }
                        if (existingMessageById != null) {
                            // ✨ NOUVEAU : Mettre à jour le message existant si le contenu a changé (masquage)
                            if (existingMessageById.content != message.content) {
                                val updatedMessages = _messages.value.map { 
                                    if (it.id == message.id) message else it 
                                }
                                _messages.value = updatedMessages.sortedWith(compareBy { 
                                    it.createdAt ?: "0000-00-00T00:00:00.000Z"
                                })
                            }
                            return@on
                        }
                        
                        // ✨ CORRIGÉ : Chercher un message optimiste correspondant (même senderId et conversationId, envoyé récemment)
                        // Ne pas comparer le contenu car il peut être masqué différemment
                            val optimisticMessage = _messages.value.find { 
                                it.id.startsWith("temp_") && 
                            it.senderId == message.senderId &&
                            it.conversationId == message.conversationId &&
                            // Vérifier que le message optimiste a été envoyé récemment (dans les 10 dernières secondes)
                            (System.currentTimeMillis() - (it.id.removePrefix("temp_").toLongOrNull() ?: 0L)) < 10000L
                            }
                            
                            if (optimisticMessage != null) {
                            // ✨ Remplacer le message optimiste par le message réel (masqué)
                            val filteredMessages = _messages.value.filter { it.id != optimisticMessage.id }
                            _messages.value = (filteredMessages + message).sortedWith(compareBy { 
                                it.createdAt ?: "0000-00-00T00:00:00.000Z"
                                })
                        } else {
                            // Nouveau message reçu - l'ajouter et trier par date
                            _messages.value = (_messages.value + message).sortedWith(compareBy { 
                                it.createdAt ?: "0000-00-00T00:00:00.000Z"
                            })
                        }
                    }
                } catch (e: Exception) {
                    // Ignorer les erreurs de parsing
                }
            }
            
            socket?.on("conversation-history") { args ->
                try {
                    val messagesArray = args[0] as? JSONArray
                    if (messagesArray != null) {
                        val parsedMessages = (0 until messagesArray.length()).mapNotNull { i ->
                            try {
                                val msgJson = messagesArray.getJSONObject(i)
                                parseMessage(msgJson)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        // Filtrer par conversationId et trier
                        val currentConvId = currentConversationId // Copie locale pour éviter le smart cast
                        val filteredMessages = if (currentConvId != null) {
                            parsedMessages.filter { 
                                it.conversationId.trim().lowercase() == currentConvId.trim().lowercase() 
                            }
                        } else {
                            parsedMessages
                        }
                        _messages.value = filteredMessages.sortedWith(compareBy { 
                            it.createdAt ?: "0000-00-00T00:00:00.000Z" // Utiliser une date minimale si createdAt est null
                        })
                    }
                } catch (e: Exception) {
                    // Ignorer les erreurs de parsing
                }
            }
            
            socket?.connect()
            Result.success(Unit)
        } catch (e: URISyntaxException) {
            Result.failure(NetworkError.ServerMessage("URL Socket.IO invalide: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    fun disconnectSocket() {
        currentConversationId = null
        socket?.disconnect()
        socket?.off()
        socket = null
        _isConnected.value = false
        _messages.value = emptyList()
    }

    fun joinConversation(conversationId: String) {
        
        currentConversationId = conversationId
        
        // S'assurer que le socket est connecté avant de joindre
        if (socket?.connected() == true) {
            socket?.emit("join-conversation", JSONObject().apply {
                put("conversationId", conversationId)
            })
        } else {
            // Le listener EVENT_CONNECT existant gérera le rejoin automatique
        }
    }

    fun sendMessage(conversationId: String, content: String, senderId: String, senderName: String? = null, senderAvatar: String? = null) {
        // Créer un message optimiste (temporaire) pour l'affichage immédiat
        // Utiliser un timestamp précis pour le tri (format ISO 8601 avec timezone UTC)
        val now = java.util.Date()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val timestamp = sdf.format(now)
        
        val optimisticMessage = Message(
            id = "temp_${System.currentTimeMillis()}", // ID temporaire
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            createdAt = timestamp
        )
        
        
        // Ajouter immédiatement le message à la liste (optimistic update) et trier par date
        _messages.value = (_messages.value + optimisticMessage).sortedWith(compareBy { 
            it.createdAt ?: "0000-00-00T00:00:00.000Z" // Utiliser une date minimale si createdAt est null
        })
        
        // Envoyer via socket
        socket?.emit("send-message", JSONObject().apply {
            put("conversationId", conversationId)
            put("content", content)
        })
    }

    suspend fun createConversation(token: String, participantId: String): Result<Conversation> {
        return try {
            val request = CreateConversationRequest(participantId = participantId)
            val response = chatApi.createConversation("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de créer la conversation."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun getMyConversations(token: String): Result<List<Conversation>> {
        return try {
            val response = chatApi.getMyConversations("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                
                // Log la première conversation pour voir la structure
                if (response.body()!!.isNotEmpty()) {
                    val firstConv = response.body()!![0]
                }
                
                var conversations = response.body()!!.map { it.toEntity() }
                
                // Log après conversion
                if (conversations.isNotEmpty()) {
                    val firstConv = conversations[0]
                }
                
                // Enrichir les participants avec leurs infos complètes (comme iOS)
                conversations = enrichParticipants(conversations, token)
                
                // Log après enrichissement
                if (conversations.isNotEmpty()) {
                    val firstConv = conversations[0]
                }
                
                Result.success(conversations)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer les conversations."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    /**
     * Enrichit les participants avec leurs infos complètes (comme iOS enrichParticipants)
     * Si un participant n'a pas de nom (juste un ID), on fait un appel API pour récupérer ses infos
     */
    private suspend fun enrichParticipants(
        conversations: List<Conversation>,
        token: String
    ): List<Conversation> {
        
        return conversations.map { conversation ->
            val enrichedNames = conversation.participantNames.toMutableMap()
            val enrichedAvatars = conversation.participantAvatars.toMutableMap()
            
            // Pour chaque participant qui n'a pas de nom
            conversation.participants.forEach { participantId ->
                
                val normalizedId = participantId.trim().lowercase()
                val hasName = conversation.participantNames.entries.any { 
                    it.key.trim().lowercase() == normalizedId 
                }
                
                val hasNameExact = conversation.participantNames.containsKey(participantId)
                
                if (!hasName && !hasNameExact) {
                    // Le backend devrait déjà populer les participants
                }
            }
            
            // Créer une nouvelle conversation avec les participants enrichis
            val enriched = conversation.copy(
                participantNames = enrichedNames,
                participantAvatars = enrichedAvatars
            )
            
            enriched
        }.also {
        }
    }

    suspend fun getMessages(token: String, conversationId: String, forceRefresh: Boolean = false): Result<List<Message>> {
        return try {
            currentConversationId = conversationId
            val response = chatApi.getMessages("Bearer $token", conversationId)
            if (response.isSuccessful && response.body() != null) {
                val messages = response.body()!!.map { it.toEntity() }
                // Filtrer pour ne garder que les messages de cette conversation et éviter les doublons
                val existingIds = if (forceRefresh) emptySet() else _messages.value.map { it.id }.toSet()
                val newMessages = messages.filter { 
                    it.conversationId == conversationId && 
                    !it.id.startsWith("temp_") && 
                    it.id !in existingIds 
                }
                
                if (newMessages.isNotEmpty() || forceRefresh) {
                    _messages.value = (_messages.value.filter { it.conversationId == conversationId || it.id.startsWith("temp_") } + newMessages)
                        .sortedWith(compareBy { 
                            it.createdAt ?: "0000-00-00T00:00:00.000Z" // Utiliser une date minimale si createdAt est null
                        })
                } else {
                }
                Result.success(_messages.value)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer les messages."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
    
    /**
     * Vérifie s'il y a de nouveaux messages sans remplacer tous les messages
     * Utilisé pour le polling en temps réel
     */
    suspend fun checkForNewMessages(token: String, conversationId: String): Result<Boolean> {
        return try {
            val response = chatApi.getMessages("Bearer $token", conversationId)
            if (response.isSuccessful && response.body() != null) {
                val messages = response.body()!!.map { it.toEntity() }
                val existingIds = _messages.value.map { it.id }.toSet()
                val newMessages = messages.filter { 
                    it.conversationId == conversationId && 
                    !it.id.startsWith("temp_") && 
                    it.id !in existingIds 
                }
                
                if (newMessages.isNotEmpty()) {
                    _messages.value = (_messages.value.filter { it.conversationId == conversationId || it.id.startsWith("temp_") } + newMessages)
                        .sortedWith(compareBy { 
                            it.createdAt ?: "0000-00-00T00:00:00.000Z"
                        })
                    Result.success(true)
                } else {
                    Result.success(false)
                }
            } else {
                Result.success(false)
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun sendMessageViaRest(token: String, conversationId: String, content: String): Result<Message> {
        return try {
            val request = SendMessageRequest(conversationId = conversationId, content = content)
            val response = chatApi.sendMessage("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible d'envoyer le message."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    private fun parseMessage(json: JSONObject): Message {
        val id = json.optString("_id", "")
        val conversationId = when {
            json.has("conversationId") -> {
                val convId = json.get("conversationId")
                if (convId is String) convId else (convId as? JSONObject)?.optString("_id", "") ?: ""
            }
            else -> ""
        }
        val senderId = when {
            json.has("senderId") -> {
                val sender = json.get("senderId")
                val id = when {
                    sender is String -> {
                        sender
                    }
                    sender is JSONObject -> {
                        
                        // Essayer TOUTES les possibilités pour extraire l'ID
                        var extractedId = ""
                        
                        // 1. Chercher "id" (transformé depuis _id par le backend via toObject)
                        if (extractedId.isBlank() && sender.has("id")) {
                            val idValue = sender.get("id")
                            if (idValue is String) {
                                extractedId = idValue
                            }
                        }
                        
                        // 2. Chercher "_id" directement
                        if (extractedId.isBlank() && sender.has("_id")) {
                            val idValue = sender.get("_id")
                            if (idValue is String) {
                                extractedId = idValue
                            } else if (idValue is org.json.JSONObject) {
                                // Si _id est un objet (ObjectId), chercher $oid
                                if (idValue.has("\$oid")) {
                                    extractedId = idValue.getString("\$oid")
                                }
                            }
                        }
                        
                        if (extractedId.isBlank()) {
                        }
                        extractedId
                    }
                    else -> {
                        if (sender != null) {
                        }
                        ""
                    }
                }
                id
            }
            else -> {
                ""
            }
        }
        val senderName = when {
            json.has("senderId") -> {
                val sender = json.get("senderId")
                if (sender is JSONObject) {
                    sender.optString("fullName", null).takeIf { !it.isNullOrBlank() }
                } else null
            }
            else -> null
        }
        val senderAvatar = when {
            json.has("senderId") -> {
                val sender = json.get("senderId")
                if (sender is JSONObject) {
                    val avatar = sender.optString("profilePicture", "")
                    if (avatar.isNotBlank()) avatar else null
                } else null
            }
            else -> null
        }
        val content = json.optString("content", "")
        val readAt = json.optString("readAt", "").takeIf { it.isNotBlank() }
        val createdAt = json.optString("createdAt", "").takeIf { it.isNotBlank() }
        val updatedAt = json.optString("updatedAt", "").takeIf { it.isNotBlank() }
        
        // ✨ NOUVEAU : Parser extractedInfo
        val extractedInfo = if (json.has("extractedInfo")) {
            val infoJson = json.getJSONObject("extractedInfo")
            tn.esprit.labasniandroid.models.entities.ExtractedInfo(
                phoneNumbers = if (infoJson.has("phoneNumbers")) {
                    val arr = infoJson.getJSONArray("phoneNumbers")
                    (0 until arr.length()).map { arr.getString(it) }
                } else null,
                addresses = if (infoJson.has("addresses")) {
                    val arr = infoJson.getJSONArray("addresses")
                    (0 until arr.length()).map { arr.getString(it) }
                } else null,
                emails = if (infoJson.has("emails")) {
                    val arr = infoJson.getJSONArray("emails")
                    (0 until arr.length()).map { arr.getString(it) }
                } else null,
                urls = if (infoJson.has("urls")) {
                    val arr = infoJson.getJSONArray("urls")
                    (0 until arr.length()).map { arr.getString(it) }
                } else null
            )
        } else null

        return Message(
            id = id,
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            readAt = readAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
            extractedInfo = extractedInfo
        )
    }

    private fun MessageResponse.toEntity(): Message {
        // Log le type exact de senderId AVANT tout traitement
        
        val senderIdValue = when {
            this.senderId is String -> {
                this.senderId as String
            }
            this.senderId is Map<*, *> -> {
                // Gson parse les objets JSON comme LinkedTreeMap<String, Any> quand le type est Any
                @Suppress("UNCHECKED_CAST")
                val map = this.senderId as Map<String, Any>
                
                var extractedId = ""
                
                // 1. Chercher "id" (transformé depuis _id par le backend)
                if (extractedId.isBlank() && map.containsKey("id")) {
                    val idValue = map["id"]
                    if (idValue is String) {
                        extractedId = idValue
                    }
                }
                
                // 2. Chercher "_id"
                if (extractedId.isBlank() && map.containsKey("_id")) {
                    val idValue = map["_id"]
                    if (idValue is String) {
                        extractedId = idValue
                    }
                }
                
                // 3. Parser comme User (fallback)
                if (extractedId.isBlank()) {
                    try {
                        val gson = Gson()
                        val jsonString = gson.toJson(map)
                        val user = gson.fromJson(jsonString, User::class.java)
                        extractedId = user.userId
                    } catch (e: Exception) {
                    }
                }
                
                extractedId
            }
            this.senderId is JsonElement -> {
                val jsonElement = this.senderId as JsonElement
                
                if (jsonElement.isJsonObject) {
                    val jsonObj = jsonElement.asJsonObject
                    
                    // Afficher tout le JSON pour déboguer
                
                // Essayer TOUTES les possibilités pour extraire l'ID
                var extractedId = ""
                
                // 1. Chercher "id" (transformé depuis _id par le backend via toObject)
                if (extractedId.isBlank() && jsonObj.has("id")) {
                    val idElement = jsonObj.get("id")
                    if (idElement.isJsonPrimitive) {
                        extractedId = idElement.asString
                    } else if (idElement.isJsonObject) {
                        // Si id est un objet, chercher _id dedans
                        val idObj = idElement.asJsonObject
                        if (idObj.has("_id") && idObj.get("_id").isJsonPrimitive) {
                            extractedId = idObj.get("_id").asString
                        }
                    }
                }
                
                // 2. Chercher "_id" directement
                if (extractedId.isBlank() && jsonObj.has("_id")) {
                    val idElement = jsonObj.get("_id")
                    if (idElement.isJsonPrimitive) {
                        extractedId = idElement.asString
                    } else if (idElement.isJsonObject) {
                        // Si _id est un objet (ObjectId), chercher $oid ou toString
                        val idObj = idElement.asJsonObject
                        if (idObj.has("\$oid") && idObj.get("\$oid").isJsonPrimitive) {
                            extractedId = idObj.get("\$oid").asString
                        }
                    }
                }
                
                // 3. Parser comme User et utiliser userId (fallback)
                if (extractedId.isBlank()) {
                    try {
                        val user = Gson().fromJson(this.senderId as JsonElement, User::class.java)
                        extractedId = user.userId
                    } catch (e: Exception) {
                    }
                }
                
                    if (extractedId.isBlank()) {
                        // Essayer de parcourir toutes les clés pour trouver quelque chose qui ressemble à un ID
                        jsonObj.keySet().forEach { key ->
                            val value = jsonObj.get(key)
                        }
                    }
                    extractedId
                } else if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isString) {
                    // Si c'est une primitive string
                    val id = jsonElement.asString
                    id
                } else {
                    ""
                }
            }
            else -> {
                if (this.senderId != null) {
                }
                ""
            }
        }
        
        val senderName = when {
            this.senderId is JsonElement && (this.senderId as JsonElement).isJsonObject -> {
                try {
                    val user = Gson().fromJson(this.senderId as JsonElement, User::class.java)
                    user.fullName
                } catch (e: Exception) {
                    null
                }
            }
            else -> null
        }
        val senderAvatar = when {
            this.senderId is JsonElement && (this.senderId as JsonElement).isJsonObject -> {
                try {
                    val user = Gson().fromJson(this.senderId as JsonElement, User::class.java)
                    user.profilePicture
                } catch (e: Exception) {
                    null
                }
            }
            else -> null
        }
        val conversationIdStr = when {
            conversationId is String -> conversationId as String
            conversationId is JsonElement && conversationId.isJsonPrimitive -> conversationId.asString
            conversationId is JsonElement && conversationId.isJsonObject -> {
                conversationId.asJsonObject.get("_id")?.asString ?: ""
            }
            else -> ""
        }
        
        // ✨ NOUVEAU : Parser extractedInfo depuis MessageResponse
        val extractedInfo = this.extractedInfo?.let { info ->
            tn.esprit.labasniandroid.models.entities.ExtractedInfo(
                phoneNumbers = info.phoneNumbers,
                addresses = info.addresses,
                emails = info.emails,
                urls = info.urls
            )
        }

        return Message(
            id = id,
            conversationId = conversationIdStr,
            senderId = senderIdValue,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            readAt = readAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
            extractedInfo = extractedInfo
        )
    }

    private fun ConversationResponse.toEntity(): Conversation {
        val participants = mutableListOf<String>()
        val participantNames = mutableMapOf<String, String>()
        val participantAvatars = mutableMapOf<String, String>()
        
        this.participants.forEachIndexed { index, p ->
            
            when {
                p is String -> {
                    participants.add(p)
                }
                p is JsonElement && p.isJsonPrimitive -> {
                    val str = p.asString
                    participants.add(str)
                }
                p is JsonElement && p.isJsonObject -> {
                    val obj = p.asJsonObject
                    
                    // Afficher TOUT le contenu de l'objet pour déboguer
                    obj.keySet().forEach { key ->
                        val value = obj.get(key)
                    }
                    
                    // Essayer d'extraire l'ID de différentes façons
                    var id: String? = null
                    
                    // 1. Essayer _id (MongoDB ObjectId)
                    if (obj.has("_id")) {
                        val idValue = obj.get("_id")
                        id = when {
                            idValue.isJsonPrimitive -> {
                                val idStr = idValue.asString
                                idStr
                            }
                            idValue.isJsonObject -> {
                                val idObj = idValue.asJsonObject
                                val oid = idObj.get("\$oid")?.asString
                                    ?: idObj.get("_id")?.asString
                                    ?: idObj.get("id")?.asString
                                oid
                            }
                            else -> null
                        }
                    }
                    
                    // 2. Essayer id (fallback)
                    if (id == null && obj.has("id")) {
                        val idValue = obj.get("id")
                        id = if (idValue.isJsonPrimitive) {
                            idValue.asString
                        } else null
                    }
                    
                    if (id != null) {
                        participants.add(id)
                        
                        // Extraire le nom (essayer plusieurs variantes avec logs)
                        val name = obj.get("fullName")?.let {
                            if (it.isJsonPrimitive) {
                                val nameStr = it.asString
                                nameStr
                            } else null
                        } ?: obj.get("name")?.let {
                            if (it.isJsonPrimitive) {
                                val nameStr = it.asString
                                nameStr
                            } else null
                        } ?: obj.get("fullname")?.let {
                            if (it.isJsonPrimitive) it.asString else null
                        } ?: obj.get("FullName")?.let {
                            if (it.isJsonPrimitive) it.asString else null
                        }
                        
                        if (name != null && name.isNotBlank()) {
                            // Stocker avec l'ID exact ET normalisé
                            participantNames[id] = name
                            participantNames[id.trim().lowercase()] = name
                        } else {
                            // Essayer de parser comme User
                            try {
                                val user = Gson().fromJson(obj, User::class.java)
                                if (user.fullName.isNotBlank()) {
                                    participantNames[id] = user.fullName
                                    participantNames[id.trim().lowercase()] = user.fullName
                                }
                            } catch (e: Exception) {
                            }
                        }
                        
                        // Extraire l'avatar (essayer plusieurs variantes)
                        val avatar = obj.get("profilePicture")?.let {
                            if (it.isJsonPrimitive) it.asString else null
                        } ?: obj.get("avatar")?.let {
                            if (it.isJsonPrimitive) it.asString else null
                        } ?: obj.get("profilepicture")?.let {
                            if (it.isJsonPrimitive) it.asString else null
                        } ?: obj.get("ProfilePicture")?.let {
                            if (it.isJsonPrimitive) it.asString else null
                        }
                        
                        if (avatar != null && avatar.isNotBlank()) {
                            participantAvatars[id] = avatar
                            participantAvatars[id.trim().lowercase()] = avatar
                        } else {
                            // Essayer de parser comme User
                            try {
                                val user = Gson().fromJson(obj, User::class.java)
                                if (user.profilePicture != null && user.profilePicture.isNotBlank()) {
                                    participantAvatars[id] = user.profilePicture
                                    participantAvatars[id.trim().lowercase()] = user.profilePicture
                                }
                            } catch (e: Exception) {
                            }
                        }
                    } else {
                    }
                }
                // Gérer les Map (Gson désérialise souvent les objets JSON comme des LinkedTreeMap)
                p is Map<*, *> -> {
                    
                    val map = p as Map<*, *>
                    
                    // Extraire l'ID
                    var id: String? = null
                    when (val idValue = map["_id"]) {
                        is String -> id = idValue
                        is Map<*, *> -> {
                            id = (idValue as Map<*, *>)["\$oid"] as? String
                                ?: (idValue["_id"] as? String)
                                ?: (idValue["id"] as? String)
                        }
                    }
                    
                    if (id == null) {
                        id = map["id"] as? String
                    }
                    
                    if (id != null) {
                        participants.add(id)
                        
                        // Extraire le nom
                        val name = (map["fullName"] as? String)
                            ?: (map["name"] as? String)
                            ?: (map["fullname"] as? String)
                        
                        if (name != null && name.isNotBlank()) {
                            participantNames[id] = name
                            participantNames[id.trim().lowercase()] = name
                        }
                        
                        // Extraire l'avatar
                        val avatar = (map["profilePicture"] as? String)
                            ?: (map["avatar"] as? String)
                            ?: (map["profilepicture"] as? String)
                        
                        if (avatar != null && avatar.isNotBlank()) {
                            participantAvatars[id] = avatar
                            participantAvatars[id.trim().lowercase()] = avatar
                        }
                    }
                }
                else -> {
                    // Type de participant non géré
                }
            }
        }
        
        val messages = this.messages?.map { it.toEntity() } ?: emptyList()
        val lastMsg = messages.lastOrNull()

        return Conversation(
            id = id,
            participants = participants,
            participantNames = participantNames,
            participantAvatars = participantAvatars,
            lastMessage = lastMsg,
            messages = messages,
            isGroup = isGroup ?: false,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

