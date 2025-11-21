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
    private val chatApi: ChatApi = RetrofitClient.chatApi
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
                android.util.Log.d("ChatRepository", "Socket already connected, skipping reconnection")
                // Si on a un conversationId, s'assurer qu'on est dans la room
                currentConversationId?.let { convId ->
                    socket?.emit("join-conversation", JSONObject().apply {
                        put("conversationId", convId)
                    })
                    android.util.Log.d("ChatRepository", "Re-joined conversation: $convId")
                }
                return Result.success(Unit)
            }
            
            // Si le socket existe mais n'est pas connecté, le déconnecter d'abord
            socket?.disconnect()
            socket?.off()
            
            val socketUrl = "${APIConstants.BASE_URL}/chat"
            android.util.Log.d("ChatRepository", "Connecting to socket: $socketUrl")
            
            val options = IO.Options().apply {
                auth = mapOf("token" to token.replace("Bearer ", ""))
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 1000
            }
            
            socket = IO.socket(socketUrl, options)
            
            socket?.on(Socket.EVENT_CONNECT) {
                _isConnected.value = true
                android.util.Log.d("ChatRepository", "✅ Socket connected successfully (socket.id: ${socket?.id()})")
                
                // Si on a déjà un conversationId, rejoindre automatiquement
                currentConversationId?.let { convId ->
                    android.util.Log.d("ChatRepository", "Auto-joining conversation after connection: $convId")
                    socket?.emit("join-conversation", JSONObject().apply {
                        put("conversationId", convId)
                    })
                    android.util.Log.d("ChatRepository", "✅ Auto-joined conversation after connection: $convId")
                } ?: run {
                    android.util.Log.d("ChatRepository", "No currentConversationId to auto-join")
                }
            }
            
            socket?.on(Socket.EVENT_DISCONNECT) {
                _isConnected.value = false
                android.util.Log.d("ChatRepository", "Socket disconnected")
            }
            
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                android.util.Log.e("ChatRepository", "Socket connection error: ${args?.getOrNull(0)?.toString() ?: "unknown"}")
            }
            
            socket?.on("new-message") { args ->
                try {
                    android.util.Log.d("ChatRepository", "=== Received new-message event from socket ===")
                    android.util.Log.d("ChatRepository", "Args count: ${args.size}, Args: ${args.contentToString()}")
                    
                    val messageJson = args[0] as? JSONObject
                    if (messageJson != null) {
                        android.util.Log.d("ChatRepository", "Message JSON: ${messageJson.toString()}")
                        val message = parseMessage(messageJson)
                        android.util.Log.d("ChatRepository", "Parsed message: id=${message.id}, conversationId=${message.conversationId}, senderId=${message.senderId}, content=${message.content.take(30)}, createdAt=${message.createdAt}")
                        
                        // Normaliser les conversationId pour la comparaison
                        val normalizedMessageConvId = message.conversationId.trim().lowercase()
                        val currentConvId = currentConversationId // Copie locale pour éviter le smart cast
                        val normalizedCurrentConvId = currentConvId?.trim()?.lowercase()
                        
                        android.util.Log.d("ChatRepository", "Comparing conversationIds: message='$normalizedMessageConvId', current='$normalizedCurrentConvId'")
                        
                        // Filtrer par conversationId pour n'afficher que les messages de la conversation active
                        if (normalizedCurrentConvId != null && normalizedMessageConvId != normalizedCurrentConvId) {
                            android.util.Log.d("ChatRepository", "Ignoring message from different conversation: ${message.conversationId} (current: $currentConvId)")
                            return@on
                        }
                        
                        // Vérifier si le message existe déjà (pour éviter les doublons avec l'optimistic update)
                        val existingMessage = _messages.value.find { it.id == message.id }
                        if (existingMessage == null) {
                            // Chercher un message optimiste correspondant (même contenu et senderId)
                            val optimisticMessage = _messages.value.find { 
                                it.id.startsWith("temp_") && 
                                it.content == message.content && 
                                it.senderId == message.senderId
                            }
                            
                            if (optimisticMessage != null) {
                                // Remplacer le message optimiste par la version du serveur
                                _messages.value = _messages.value.map { 
                                    if (it.id == optimisticMessage.id) message else it
                                }.sortedWith(compareBy { 
                                    it.createdAt ?: "0000-00-00T00:00:00.000Z" // Utiliser une date minimale si createdAt est null
                                })
                                android.util.Log.d("ChatRepository", "✅ Replaced optimistic message with server version: ${message.id}")
                            } else {
                                // Nouveau message reçu - l'ajouter et trier par date
                                val currentMessages = _messages.value
                                _messages.value = (currentMessages + message).sortedWith(compareBy { 
                                    it.createdAt ?: "0000-00-00T00:00:00.000Z" // Utiliser une date minimale si createdAt est null
                                })
                                android.util.Log.d("ChatRepository", "✅ Added new message via socket: ${message.content.take(30)} (from ${message.senderId}, createdAt: ${message.createdAt})")
                                android.util.Log.d("ChatRepository", "Total messages now: ${_messages.value.size}")
                            }
                        } else {
                            // Message existe déjà avec le même ID - mettre à jour si nécessaire
                            android.util.Log.d("ChatRepository", "Message already exists: ${message.id}")
                        }
                    } else {
                        android.util.Log.e("ChatRepository", "❌ new-message event: messageJson is null")
                        android.util.Log.e("ChatRepository", "Args[0] type: ${args.getOrNull(0)?.javaClass?.name}")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ChatRepository", "❌ Error handling new-message event", e)
                    e.printStackTrace()
                }
            }
            
            socket?.on("conversation-history") { args ->
                try {
                    android.util.Log.d("ChatRepository", "Received conversation-history event from socket")
                    val messagesArray = args[0] as? JSONArray
                    if (messagesArray != null) {
                        android.util.Log.d("ChatRepository", "conversation-history: ${messagesArray.length()} messages")
                        val parsedMessages = (0 until messagesArray.length()).mapNotNull { i ->
                            try {
                                val msgJson = messagesArray.getJSONObject(i)
                                parseMessage(msgJson)
                            } catch (e: Exception) {
                                android.util.Log.e("ChatRepository", "Error parsing message in conversation-history", e)
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
                        android.util.Log.d("ChatRepository", "Loaded ${_messages.value.size} messages from conversation-history")
                    } else {
                        android.util.Log.e("ChatRepository", "conversation-history event: messagesArray is null")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ChatRepository", "Error handling conversation-history event", e)
                    e.printStackTrace()
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
        android.util.Log.d("ChatRepository", "=== joinConversation called: $conversationId ===")
        android.util.Log.d("ChatRepository", "Socket is null: ${socket == null}")
        android.util.Log.d("ChatRepository", "Socket connected: ${socket?.connected()}")
        
        currentConversationId = conversationId
        
        // S'assurer que le socket est connecté avant de joindre
        if (socket?.connected() == true) {
            socket?.emit("join-conversation", JSONObject().apply {
                put("conversationId", conversationId)
            })
            android.util.Log.d("ChatRepository", "✅ Joined conversation: $conversationId (socket connected)")
        } else {
            android.util.Log.w("ChatRepository", "⚠️ Cannot join conversation: socket not connected. Will auto-join when connected.")
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
        
        android.util.Log.d("ChatRepository", "Sending message optimistically: $content (timestamp: $timestamp)")
        
        // Ajouter immédiatement le message à la liste (optimistic update) et trier par date
        _messages.value = (_messages.value + optimisticMessage).sortedWith(compareBy { 
            it.createdAt ?: "0000-00-00T00:00:00.000Z" // Utiliser une date minimale si createdAt est null
        })
        
        // Envoyer via socket
        socket?.emit("send-message", JSONObject().apply {
            put("conversationId", conversationId)
            put("content", content)
        })
        android.util.Log.d("ChatRepository", "Message sent via socket to conversation: $conversationId")
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
                val conversations = response.body()!!.map { it.toEntity() }
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
                    android.util.Log.d("ChatRepository", "Found ${newMessages.size} new messages (forceRefresh=$forceRefresh)")
                    _messages.value = (_messages.value.filter { it.conversationId == conversationId || it.id.startsWith("temp_") } + newMessages)
                        .sortedWith(compareBy { 
                            it.createdAt ?: "0000-00-00T00:00:00.000Z" // Utiliser une date minimale si createdAt est null
                        })
                    android.util.Log.d("ChatRepository", "Total messages now: ${_messages.value.size}")
                } else {
                    android.util.Log.d("ChatRepository", "No new messages found")
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
                    android.util.Log.d("ChatRepository", "🆕 Found ${newMessages.size} new messages via polling")
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
                        android.util.Log.d("ChatRepository", "parseMessage: senderId is String = '$sender'")
                        sender
                    }
                    sender is JSONObject -> {
                        android.util.Log.d("ChatRepository", "parseMessage: senderId is JSONObject")
                        android.util.Log.d("ChatRepository", "parseMessage: JSON keys = ${sender.keys().asSequence().toList()}")
                        android.util.Log.e("ChatRepository", "parseMessage: Full JSON = ${sender.toString()}")
                        
                        // Essayer TOUTES les possibilités pour extraire l'ID
                        var extractedId = ""
                        
                        // 1. Chercher "id" (transformé depuis _id par le backend via toObject)
                        if (extractedId.isBlank() && sender.has("id")) {
                            val idValue = sender.get("id")
                            if (idValue is String) {
                                extractedId = idValue
                                android.util.Log.d("ChatRepository", "parseMessage: Found 'id' = '$extractedId'")
                            }
                        }
                        
                        // 2. Chercher "_id" directement
                        if (extractedId.isBlank() && sender.has("_id")) {
                            val idValue = sender.get("_id")
                            if (idValue is String) {
                                extractedId = idValue
                                android.util.Log.d("ChatRepository", "parseMessage: Found '_id' = '$extractedId'")
                            } else if (idValue is org.json.JSONObject) {
                                // Si _id est un objet (ObjectId), chercher $oid
                                if (idValue.has("\$oid")) {
                                    extractedId = idValue.getString("\$oid")
                                    android.util.Log.d("ChatRepository", "parseMessage: Found '_id.\$oid' = '$extractedId'")
                                }
                            }
                        }
                        
                        android.util.Log.e("ChatRepository", "parseMessage: Final extracted id = '$extractedId'")
                        if (extractedId.isBlank()) {
                            android.util.Log.e("ChatRepository", "parseMessage: ERROR - senderId is EMPTY after all attempts!")
                            android.util.Log.e("ChatRepository", "parseMessage: Available keys: ${sender.keys().asSequence().toList()}")
                        }
                        extractedId
                    }
                    else -> {
                        android.util.Log.w("ChatRepository", "parseMessage: senderId is unknown type: ${sender?.javaClass?.simpleName}")
                        if (sender != null) {
                            android.util.Log.w("ChatRepository", "parseMessage: sender toString = ${sender.toString()}")
                        }
                        ""
                    }
                }
                id
            }
            else -> {
                android.util.Log.w("ChatRepository", "parseMessage: No senderId found in message JSON")
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

        return Message(
            id = id,
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            readAt = readAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun MessageResponse.toEntity(): Message {
        // Log le type exact de senderId AVANT tout traitement
        android.util.Log.e("ChatRepository", "=== MessageResponse.toEntity() START ===")
        android.util.Log.e("ChatRepository", "senderId type: ${this.senderId?.javaClass?.canonicalName}")
        android.util.Log.e("ChatRepository", "senderId toString: ${this.senderId?.toString()}")
        android.util.Log.e("ChatRepository", "senderId is String: ${this.senderId is String}")
        android.util.Log.e("ChatRepository", "senderId is Map: ${this.senderId is Map<*, *>}")
        android.util.Log.e("ChatRepository", "senderId is JsonElement: ${this.senderId is JsonElement}")
        
        val senderIdValue = when {
            this.senderId is String -> {
                android.util.Log.e("ChatRepository", "MessageResponse: senderId is String = '${this.senderId}'")
                this.senderId as String
            }
            this.senderId is Map<*, *> -> {
                // Gson parse les objets JSON comme LinkedTreeMap<String, Any> quand le type est Any
                @Suppress("UNCHECKED_CAST")
                val map = this.senderId as Map<String, Any>
                android.util.Log.e("ChatRepository", "MessageResponse: senderId is Map")
                android.util.Log.e("ChatRepository", "MessageResponse: Map keys = ${map.keys}")
                android.util.Log.e("ChatRepository", "MessageResponse: Map toString = ${map.toString()}")
                
                var extractedId = ""
                
                // 1. Chercher "id" (transformé depuis _id par le backend)
                if (extractedId.isBlank() && map.containsKey("id")) {
                    val idValue = map["id"]
                    if (idValue is String) {
                        extractedId = idValue
                        android.util.Log.e("ChatRepository", "MessageResponse: Found 'id' in Map = '$extractedId'")
                    }
                }
                
                // 2. Chercher "_id"
                if (extractedId.isBlank() && map.containsKey("_id")) {
                    val idValue = map["_id"]
                    if (idValue is String) {
                        extractedId = idValue
                        android.util.Log.e("ChatRepository", "MessageResponse: Found '_id' in Map = '$extractedId'")
                    }
                }
                
                // 3. Parser comme User (fallback)
                if (extractedId.isBlank()) {
                    try {
                        val gson = Gson()
                        val jsonString = gson.toJson(map)
                        val user = gson.fromJson(jsonString, User::class.java)
                        extractedId = user.userId
                        android.util.Log.e("ChatRepository", "MessageResponse: Parsed Map as User, userId = '$extractedId'")
                    } catch (e: Exception) {
                        android.util.Log.e("ChatRepository", "MessageResponse: Error parsing Map as User", e)
                    }
                }
                
                android.util.Log.e("ChatRepository", "MessageResponse: Final extracted from Map = '$extractedId'")
                extractedId
            }
            this.senderId is JsonElement -> {
                val jsonElement = this.senderId as JsonElement
                android.util.Log.e("ChatRepository", "MessageResponse: senderId is JsonElement")
                android.util.Log.e("ChatRepository", "MessageResponse: isJsonObject = ${jsonElement.isJsonObject}")
                android.util.Log.e("ChatRepository", "MessageResponse: isJsonPrimitive = ${jsonElement.isJsonPrimitive}")
                android.util.Log.e("ChatRepository", "MessageResponse: isJsonArray = ${jsonElement.isJsonArray}")
                android.util.Log.e("ChatRepository", "MessageResponse: isJsonNull = ${jsonElement.isJsonNull}")
                
                if (jsonElement.isJsonObject) {
                    val jsonObj = jsonElement.asJsonObject
                    android.util.Log.e("ChatRepository", "MessageResponse: senderId is JsonObject")
                    android.util.Log.e("ChatRepository", "MessageResponse: JSON keys = ${jsonObj.keySet()}")
                    
                    // Afficher tout le JSON pour déboguer
                    android.util.Log.e("ChatRepository", "MessageResponse: Full JSON = ${jsonObj.toString()}")
                
                // Essayer TOUTES les possibilités pour extraire l'ID
                var extractedId = ""
                
                // 1. Chercher "id" (transformé depuis _id par le backend via toObject)
                if (extractedId.isBlank() && jsonObj.has("id")) {
                    val idElement = jsonObj.get("id")
                    if (idElement.isJsonPrimitive) {
                        extractedId = idElement.asString
                        android.util.Log.d("ChatRepository", "MessageResponse: Found 'id' = '$extractedId'")
                    } else if (idElement.isJsonObject) {
                        // Si id est un objet, chercher _id dedans
                        val idObj = idElement.asJsonObject
                        if (idObj.has("_id") && idObj.get("_id").isJsonPrimitive) {
                            extractedId = idObj.get("_id").asString
                            android.util.Log.d("ChatRepository", "MessageResponse: Found 'id._id' = '$extractedId'")
                        }
                    }
                }
                
                // 2. Chercher "_id" directement
                if (extractedId.isBlank() && jsonObj.has("_id")) {
                    val idElement = jsonObj.get("_id")
                    if (idElement.isJsonPrimitive) {
                        extractedId = idElement.asString
                        android.util.Log.d("ChatRepository", "MessageResponse: Found '_id' = '$extractedId'")
                    } else if (idElement.isJsonObject) {
                        // Si _id est un objet (ObjectId), chercher $oid ou toString
                        val idObj = idElement.asJsonObject
                        if (idObj.has("\$oid") && idObj.get("\$oid").isJsonPrimitive) {
                            extractedId = idObj.get("\$oid").asString
                            android.util.Log.d("ChatRepository", "MessageResponse: Found '_id.\$oid' = '$extractedId'")
                        }
                    }
                }
                
                // 3. Parser comme User et utiliser userId (fallback)
                if (extractedId.isBlank()) {
                    try {
                        val user = Gson().fromJson(this.senderId as JsonElement, User::class.java)
                        extractedId = user.userId
                        android.util.Log.d("ChatRepository", "MessageResponse: Parsed as User, userId = '$extractedId' (id='${user.id}', mongoId='${user.mongoId}')")
                    } catch (e: Exception) {
                        android.util.Log.e("ChatRepository", "MessageResponse: Error parsing as User", e)
                        android.util.Log.e("ChatRepository", "MessageResponse: Exception message = ${e.message}")
                    }
                }
                
                    android.util.Log.e("ChatRepository", "MessageResponse: Final extracted senderId = '$extractedId'")
                    if (extractedId.isBlank()) {
                        android.util.Log.e("ChatRepository", "MessageResponse: ERROR - senderId is EMPTY after all attempts!")
                        android.util.Log.e("ChatRepository", "MessageResponse: Available keys: ${jsonObj.keySet()}")
                        // Essayer de parcourir toutes les clés pour trouver quelque chose qui ressemble à un ID
                        jsonObj.keySet().forEach { key ->
                            val value = jsonObj.get(key)
                            android.util.Log.e("ChatRepository", "MessageResponse: Key '$key' = $value (type: ${value.javaClass.simpleName})")
                        }
                    }
                    extractedId
                } else if (jsonElement.isJsonPrimitive && jsonElement.asJsonPrimitive.isString) {
                    // Si c'est une primitive string
                    val id = jsonElement.asString
                    android.util.Log.e("ChatRepository", "MessageResponse: senderId is JsonPrimitive String = '$id'")
                    id
                } else {
                    android.util.Log.e("ChatRepository", "MessageResponse: senderId JsonElement is not an object or string")
                    ""
                }
            }
            else -> {
                android.util.Log.e("ChatRepository", "MessageResponse: senderId is unknown type: ${this.senderId?.javaClass?.canonicalName}")
                if (this.senderId != null) {
                    android.util.Log.e("ChatRepository", "MessageResponse: senderId toString = ${this.senderId.toString()}")
                }
                ""
            }
        }
        
        android.util.Log.e("ChatRepository", "MessageResponse: Final senderIdValue = '$senderIdValue'")
        android.util.Log.e("ChatRepository", "=== MessageResponse.toEntity() END ===")
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

        return Message(
            id = id,
            conversationId = conversationIdStr,
            senderId = senderIdValue,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            readAt = readAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun ConversationResponse.toEntity(): Conversation {
        android.util.Log.d("ChatRepository", "=== Converting ConversationResponse to Entity ===")
        android.util.Log.d("ChatRepository", "Conversation ID: $id")
        android.util.Log.d("ChatRepository", "Participants count: ${this.participants.size}")
        android.util.Log.d("ChatRepository", "Participants raw: ${this.participants}")
        
        val participants = mutableListOf<String>()
        val participantNames = mutableMapOf<String, String>()
        val participantAvatars = mutableMapOf<String, String>()
        
        this.participants.forEachIndexed { index, p ->
            android.util.Log.d("ChatRepository", "Processing participant[$index]: type=${p?.javaClass?.simpleName}, value=$p")
            
            when {
                p is String -> {
                    android.util.Log.d("ChatRepository", "Participant[$index] is String: $p")
                    participants.add(p)
                }
                p is JsonElement && p.isJsonPrimitive -> {
                    val str = p.asString
                    android.util.Log.d("ChatRepository", "Participant[$index] is JsonPrimitive: $str")
                    participants.add(str)
                }
                p is JsonElement && p.isJsonObject -> {
                    val obj = p.asJsonObject
                    android.util.Log.d("ChatRepository", "Participant[$index] is JsonObject, keys: ${obj.keySet()}")
                    
                    // Essayer d'extraire l'ID de différentes façons
                    var id: String? = null
                    if (obj.has("_id")) {
                        val idValue = obj.get("_id")
                        id = when {
                            idValue.isJsonPrimitive -> idValue.asString
                            idValue.isJsonObject -> idValue.asJsonObject.get("_id")?.asString ?: idValue.asJsonObject.get("\$oid")?.asString
                            else -> null
                        }
                    } else if (obj.has("id")) {
                        id = obj.get("id")?.asString
                    }
                    
                    if (id != null) {
                        android.util.Log.d("ChatRepository", "Participant[$index] extracted ID: $id")
                        participants.add(id)
                        
                        // Extraire le nom (essayer plusieurs variantes)
                        val name = obj.get("fullName")?.asString
                            ?: obj.get("name")?.asString
                            ?: obj.get("fullname")?.asString
                            ?: obj.get("FullName")?.asString
                        if (name != null && name.isNotBlank()) {
                            android.util.Log.d("ChatRepository", "✅ Participant[$index] name: $id -> $name")
                            participantNames[id] = name
                        } else {
                            android.util.Log.w("ChatRepository", "⚠️ Participant[$index] missing fullName/name for ID: $id")
                            android.util.Log.w("ChatRepository", "Available keys in participant object: ${obj.keySet()}")
                            // Afficher toutes les clés pour déboguer
                            obj.keySet().forEach { key ->
                                val value = obj.get(key)
                                android.util.Log.w("ChatRepository", "  Key '$key' = ${if (value.isJsonPrimitive) value.asString else value.javaClass.simpleName}")
                            }
                        }
                        
                        // Extraire l'avatar (essayer plusieurs variantes)
                        val avatar = obj.get("profilePicture")?.asString
                            ?: obj.get("avatar")?.asString
                            ?: obj.get("profilepicture")?.asString
                            ?: obj.get("ProfilePicture")?.asString
                        if (avatar != null && avatar.isNotBlank()) {
                            android.util.Log.d("ChatRepository", "✅ Participant[$index] avatar: $id -> $avatar")
                            participantAvatars[id] = avatar
                        } else {
                            android.util.Log.w("ChatRepository", "⚠️ Participant[$index] missing or empty profilePicture/avatar for ID: $id")
                        }
                    } else {
                        android.util.Log.e("ChatRepository", "❌ Participant[$index] could not extract ID from: ${obj.toString()}")
                        android.util.Log.e("ChatRepository", "Available keys: ${obj.keySet()}")
                    }
                }
                else -> {
                    android.util.Log.w("ChatRepository", "Participant[$index] is unknown type: ${p?.javaClass?.simpleName}")
                }
            }
        }
        
        android.util.Log.d("ChatRepository", "Final participants: $participants")
        android.util.Log.d("ChatRepository", "Final participant names: $participantNames")
        android.util.Log.d("ChatRepository", "Final participant avatars: $participantAvatars")
        
        val messages = this.messages?.map { it.toEntity() } ?: emptyList()
        val lastMsg = messages.lastOrNull()
        android.util.Log.d("ChatRepository", "Last message: ${lastMsg?.content?.take(30)}")

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

