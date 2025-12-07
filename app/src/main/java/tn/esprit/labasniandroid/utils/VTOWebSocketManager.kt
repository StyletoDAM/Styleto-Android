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
            val options = IO.Options().apply {
                forceNew = true
                reconnection = true
                reconnectionAttempts = 3
                reconnectionDelay = 3000
                timeout = 20000
                query = "token=$token"
            }

            socket = IO.socket(URI.create(APIConstants.BASE_URL), options).apply {
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
            }

            socket?.connect()
            Log.d(tag, "🔌 Connexion WS VTO initiée...")
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

        // ✅ FIX CRITIQUE : Vérification null-safe
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
            val clothesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("imageURL", selectedCloth.imageUrl)
                    put("processedImageURL", selectedCloth.processedImageUrl ?: selectedCloth.imageUrl)
                    put("category", selectedCloth.type)
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

    // ✅ AJOUT : Méthode pour vérifier l'état de connexion
    fun isConnected(): Boolean {
        return socket?.connected() == true
    }
}
