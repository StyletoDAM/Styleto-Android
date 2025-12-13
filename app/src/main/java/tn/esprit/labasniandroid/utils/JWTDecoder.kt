package tn.esprit.labasniandroid.utils

import android.util.Base64
import android.util.Log

/**
 * JWTDecoder pour Android (identique a iOS)
 * Decode les tokens JWT pour extraire l'ID utilisateur depuis le champ 'sub'
 */
object JWTDecoder {
    private const val TAG = "JWTDecoder"
    
    /**
     * Decode un token JWT et retourne le payload
     */
    fun decode(jwtToken: String): Map<String, Any>? {
        return try {
            val parts = jwtToken.split(".")
            if (parts.size != 3) {
                Log.w(TAG, "Token JWT invalide: nombre de parties incorrect")
                return null
            }
            
            // Decoder le payload (partie 2)
            val payloadJson = String(
                Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP),
                Charsets.UTF_8
            )
            
            // Parser le JSON
            val json = org.json.JSONObject(payloadJson)
            val map = mutableMapOf<String, Any>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = json.get(key)
            }
            map
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors du decodage du JWT: ${e.message}", e)
            null
        }
    }
    
    /**
     * CRITIQUE : Extrait l'ID utilisateur depuis le champ 'sub' du JWT
     * C'est exactement ce que le backend utilise pour identifier l'utilisateur
     */
    fun extractUserId(token: String): String? {
        if (token.isBlank()) {
            Log.w(TAG, "Token vide")
            return null
        }
        
        val payload = decode(token) ?: run {
            Log.w(TAG, "Impossible de decoder le token")
            return null
        }
        
        val userId = payload["sub"] as? String
        
        if (userId != null) {
            Log.d(TAG, "User ID extrait du JWT (sub): '$userId'")
            Log.d(TAG, "   Cles disponibles dans le JWT: ${payload.keys.joinToString(", ")}")
            return userId
        } else {
            Log.w(TAG, "Aucun 'sub' trouve dans le JWT")
            Log.w(TAG, "   Cles disponibles: ${payload.keys.joinToString(", ")}")
            Log.w(TAG, "   Payload complet: $payload")
            return null
        }
    }
    
    /**
     * Normalise un ID (trim + lowercase) pour la comparaison
     * UTILISER CETTE METHODE POUR TOUTES LES COMPARAISONS
     */
    fun normalizeId(id: String?): String {
        if (id.isNullOrBlank()) return ""
        return id.trim().lowercase()
    }
}

