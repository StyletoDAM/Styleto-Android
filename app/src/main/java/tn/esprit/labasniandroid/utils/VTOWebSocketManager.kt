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
            // ✅ FIX CRITIQUE 1 : Ajouter le namespace /vto
            val socketUrl = "${APIConstants.BASE_URL}/vto"

            val options = IO.Options().apply {
                forceNew = true
                reconnection = true
                reconnectionAttempts = 3
                reconnectionDelay = 3000
                timeout = 20000
                // ✅ FIX CRITIQUE 2 : Token dans query params (comme iOS)
                query = "token=$token"
            }

            socket = IO.socket(URI.create(socketUrl), options).apply {
                on(Socket.EVENT_CONNECT) {
                    Log.d(tag, "✅ WebSocket VTO connecté")
                    onConnected?.invoke()
                }

                on("frame_processed") { args ->
                    try {
                        val data = args[0] as? JSONObject
                        if (data == null) {
                            Log.e(tag, "❌ frame_processed: data null")
                            return@on
                        }

                        val frame = data.optString("frame", null)
                        if (frame.isNullOrBlank()) {
                            Log.e(tag, "❌ frame_processed: frame vide")
                            return@on
                        }

                        Log.d(tag, "✅ Frame traitée reçue (${frame.length / 1024}KB)")
                        onProcessedFrame?.invoke(frame)
                    } catch (e: Exception) {
                        Log.e(tag, "❌ Erreur parsing frame_processed", e)
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
            Log.d(tag, "🔌 Connexion WS VTO initiée sur: $socketUrl")
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