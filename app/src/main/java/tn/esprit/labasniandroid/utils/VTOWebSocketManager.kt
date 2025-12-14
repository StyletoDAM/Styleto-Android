package tn.esprit.labasniandroid.utils

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONObject
import tn.esprit.labasniandroid.models.entities.Cloth
import java.net.URI

class VTOWebSocketManager(private val context: Context) {
    private var socket: Socket? = null
    private val tag = "VTOWebSocket"

    // Callbacks pour UI
    var onConnected: (() -> Unit)? = null
    var onProcessedFrame: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    fun connect() {
        val token = TokenManager.getToken(context) ?: run {
            Log.e(tag, "❌ Pas de token")
            onError?.invoke("Pas de token")
            return
        }

        try {
            // ✅ FIX : URL de base SANS /vto (comme iOS utilise baseURL puis socket(forNamespace: "/vto"))
            // Socket.IO Android nécessite l'URL complète avec namespace
            val baseUrl = APIConstants.BASE_URL
            val socketUrl = "$baseUrl/vto"
            
            Log.d(tag, "🔌 Connexion WS VTO sur: $socketUrl")

            val options = IO.Options().apply {
                forceNew = true
                reconnection = true
                reconnectionAttempts = 5  // ✅ Comme iOS reconnectAttempts(5)
                reconnectionDelay = 2000  // ✅ Comme iOS reconnectWait(2)
                timeout = 20000
                // ✅ FIX : Token dans query params (comme iOS connectParams(["token": token]))
                query = "token=$token"
            }

            socket = IO.socket(URI.create(socketUrl), options).apply {
                on(Socket.EVENT_CONNECT) {
                    Log.d(tag, "✅ WebSocket VTO connecté")
                    onConnected?.invoke()
                }

                on("frame_processed") { args ->
                    try {
                        Log.d(tag, "📥 ========== frame_processed EVENT RECEIVED ==========")
                        Log.d(tag, "📥 args count: ${args.size}")
                        
                        if (args.isEmpty()) {
                            Log.e(tag, "❌ frame_processed: args vide")
                            return@on
                        }
                        
                        // ✅ Parser EXACTEMENT comme iOS: data[0] as? [String: Any]
                        // Dans iOS: guard let dict = data[0] as? [String: Any]
                        val rawData = args[0]
                        
                        if (rawData == null) {
                            Log.e(tag, "❌ frame_processed: args[0] est null")
                            return@on
                        }
                        
                        Log.d(tag, "📥 args[0] type: ${rawData.javaClass.name}")
                        
                        // ✅ Extraire directement depuis Map ou JSONObject (comme iOS dict["frame"])
                        val frame = when {
                            rawData is Map<*, *> -> {
                                // ✅ C'est un Map, extraire directement comme iOS dict["frame"]
                                @Suppress("UNCHECKED_CAST")
                                val map = rawData as Map<String, Any>
                                map["frame"] as? String
                            }
                            rawData is JSONObject -> {
                                // ✅ C'est un JSONObject, utiliser optString
                                rawData.optString("frame", null)
                            }
                            else -> {
                                // ✅ Dernière tentative: convertir en JSONObject
                                try {
                                    val jsonString = rawData.toString()
                                    JSONObject(jsonString).optString("frame", null)
                                } catch (e: Exception) {
                                    Log.e(tag, "❌ Type inattendu et échec conversion: ${rawData.javaClass.name}")
                                    null
                                }
                            }
                        }
                        
                        if (frame.isNullOrBlank()) {
                            Log.e(tag, "❌ frame_processed: frame vide ou null")
                            Log.e(tag, "❌ rawData: ${rawData.toString().take(200)}")
                            return@on
                        }
                        
                        // ✅ Extraire processingTime et fps (comme iOS)
                        val processingTime = when {
                            rawData is Map<*, *> -> {
                                @Suppress("UNCHECKED_CAST")
                                val map = rawData as Map<String, Any>
                                (map["processingTime"] as? Number)?.toLong() ?: 0L
                            }
                            rawData is JSONObject -> {
                                rawData.optLong("processingTime", 0)
                            }
                            else -> 0L
                        }
                        
                        val fps = when {
                            rawData is Map<*, *> -> {
                                @Suppress("UNCHECKED_CAST")
                                val map = rawData as Map<String, Any>
                                (map["fps"] as? Number)?.toInt() ?: 0
                            }
                            rawData is JSONObject -> {
                                rawData.optInt("fps", 0)
                            }
                            else -> 0
                        }
                        
                        Log.d(tag, "✅ Frame traitée reçue (${frame.length / 1024}KB, ${processingTime}ms, ${fps}fps)")
                        Log.d(tag, "✅ Appel onProcessedFrame callback")
                        
                        // ✅ FIX : Capturer la référence pour éviter le smart cast error
                        val callback = onProcessedFrame
                        if (callback == null) {
                            Log.e(tag, "❌ CRITIQUE: onProcessedFrame callback est NULL!")
                        } else {
                            callback.invoke(frame)
                            Log.d(tag, "✅ Callback onProcessedFrame exécuté avec succès")
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "❌ Erreur parsing frame_processed", e)
                        e.printStackTrace()
                        onError?.invoke("Erreur parsing: ${e.message}")
                    }
                }

                on("frame_error") { args ->
                    try {
                        val data = args[0] as? JSONObject
                        val error = data?.optString("error", "Erreur inconnue") ?: "Erreur inconnue"
                        Log.e(tag, "❌ Erreur serveur: $error")
                        onError?.invoke(error)
                    } catch (e: Exception) {
                        Log.e(tag, "❌ Erreur parsing frame_error", e)
                    }
                }

                on(Socket.EVENT_CONNECT_ERROR) { args ->
                    val error = args.getOrNull(0)?.toString() ?: "Erreur connexion"
                    Log.e(tag, "❌ Erreur connexion: $error")
                    onError?.invoke(error)
                }

                on(Socket.EVENT_DISCONNECT) { args ->
                    val reason = args.getOrNull(0)?.toString() ?: "Déconnexion"
                    Log.w(tag, "⚠️ Déconnecté: $reason")
                }

                on("error") { args ->
                    val error = args.getOrNull(0)?.toString() ?: "Erreur socket"
                    Log.e(tag, "❌ Socket error: $error")
                    onError?.invoke(error)
                }

                // ✅ AJOUT : Écouter l'événement "connected"
                on("connected") { args ->
                    try {
                        val data = args[0] as? JSONObject
                        val message = data?.optString("message", "Connexion établie")
                        Log.d(tag, "✅ Backend confirmé: $message")
                    } catch (e: Exception) {
                        Log.e(tag, "Erreur parsing connected event", e)
                    }
                }
            }

            socket?.connect()
            Log.d(tag, "🔌 Connexion WS VTO initiée sur: $socketUrl (comme iOS)")
        } catch (e: Exception) {
            Log.e(tag, "❌ Erreur init WS: ${e.message}", e)
            onError?.invoke("Erreur connexion: ${e.message}")
        }
    }

    fun sendFrame(frameBase64: String, selectedCloth: Cloth?) {
        if (selectedCloth == null) {
            Log.w(tag, "⚠️ Aucun vêtement sélectionné")
            return
        }

        // ✅ FIX CRITIQUE 3 : Vérification null-safe AVANT d'utiliser socket
        val currentSocket = socket
        if (currentSocket == null) {
            Log.e(tag, "❌ Socket est null - connexion non établie")
            onError?.invoke("Socket non initialisé")
            return
        }

        if (!currentSocket.connected()) {
            Log.w(tag, "⚠️ Socket non connecté")
            onError?.invoke("Socket non connecté")
            return
        }

        try {
            // ✅ FIX CRITIQUE 4 : Structure EXACTE comme iOS
            val clothesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("imageURL", selectedCloth.imageUrl)
                    // ✅ Priorité à processedImageURL (comme iOS)
                    put("processedImageURL", selectedCloth.processedImageUrl ?: selectedCloth.imageUrl)
                    // ✅ FIX : Normaliser la catégorie en minuscules
                    put("category", selectedCloth.type.lowercase())
                })
            }

            val payload = JSONObject().apply {
                put("frame", frameBase64)
                put("clothes", clothesArray)
            }

            currentSocket.emit("process_frame", payload)
            Log.d(tag, "📤 Frame envoyée: ${frameBase64.length / 1024}KB - Vêtement: ${selectedCloth.type}")
        } catch (e: Exception) {
            Log.e(tag, "❌ Erreur envoi frame", e)
            onError?.invoke("Erreur envoi: ${e.message}")
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
        Log.d(tag, "🔌 WS VTO déconnecté")
    }

    fun isConnected(): Boolean {
        return socket?.connected() == true
    }
}