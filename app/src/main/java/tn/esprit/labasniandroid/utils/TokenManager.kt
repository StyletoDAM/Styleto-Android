package tn.esprit.labasniandroid.utils

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "labasni_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token" // ✨ NOUVEAU
    private const val KEY_USER_ID = "user_id"
    private const val KEY_GENDER = "gender"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .apply()
        
        // ✨ CRITIQUE : Extraire et sauvegarder le userId du JWT automatiquement (comme iOS)
        val userId = JWTDecoder.extractUserId(token)
        if (userId != null) {
            saveUserId(context, userId)
            android.util.Log.d("TokenManager", "✅ Token et userId sauvegardés: '$userId'")
        } else {
            android.util.Log.w("TokenManager", "⚠️ Impossible d'extraire userId du token")
        }
    }

    fun getToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, null)
    }

    fun saveRefreshToken(context: Context, token: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_REFRESH_TOKEN, token)
            .apply()
    }

    fun getRefreshToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_REFRESH_TOKEN, null)
    }

    fun clearToken(context: Context) {
        getSharedPreferences(context).edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN) // ✨ AJOUT
            .remove(KEY_USER_ID)
            .remove(KEY_GENDER)
            .apply()
    }

    fun saveUserId(context: Context, userId: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_USER_ID, userId)
            .apply()
    }

    fun getUserId(context: Context): String? {
        val storedId = getSharedPreferences(context).getString(KEY_USER_ID, null)
        
        // Fallback : si pas stocké, essayer d'extraire du token actuel (comme iOS)
        if (storedId == null) {
            val token = getToken(context)
            if (token != null) {
                val extractedId = JWTDecoder.extractUserId(token)
                if (extractedId != null) {
                    saveUserId(context, extractedId)
                    android.util.Log.d("TokenManager", "✅ UserId récupéré du token actuel: '$extractedId'")
                    return extractedId
                }
            }
        }
        
        return storedId
    }
    
    /**
     * Récupère l'ID utilisateur de manière normalisée (trim + lowercase)
     * ⚠️ UTILISER CETTE MÉTHODE POUR TOUTES LES COMPARAISONS
     */
    fun getNormalizedUserId(context: Context): String? {
        val userId = getUserId(context) ?: return null
        return JWTDecoder.normalizeId(userId)
    }

    fun saveGender(context: Context, gender: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_GENDER, gender)
            .apply()
    }

    fun getGender(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_GENDER, null)
    }
}

