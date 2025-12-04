package tn.esprit.labasniandroid.models.repositories

import android.util.Log
import tn.esprit.labasniandroid.api.CartApi
import tn.esprit.labasniandroid.api.AddToCartRequest
import tn.esprit.labasniandroid.api.RemoveFromCartRequest
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.CartItemResponse
import tn.esprit.labasniandroid.data.local.entities.CartItem
import tn.esprit.labasniandroid.utils.TokenManager
import android.content.Context

/**
 * Repository pour gérer les appels API du panier
 */
object CartRepository {
    private val cartApi: CartApi = RetrofitClient.cartApi

    /**
     * Récupérer le panier depuis l'API
     */
    suspend fun getCart(context: Context): Result<List<CartItem>> {
        return try {
            val token = TokenManager.getToken(context)
                ?: return Result.failure(Exception("Token non disponible"))

            val response = cartApi.getCart("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val cartResponse = response.body()!!
                val cartItems = cartResponse.items.mapNotNull { item ->
                    convertToCartItem(item)
                }
                Result.success(cartItems)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Erreur inconnue"
                Log.e("CartRepository", "Erreur getCart: ${response.code()} - $errorBody")
                Result.failure(Exception("Erreur ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception getCart: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Ajouter un article au panier
     */
    suspend fun addToCart(context: Context, storeItemId: String): Result<Unit> {
        return try {
            val token = TokenManager.getToken(context)
                ?: return Result.failure(Exception("Token non disponible"))

            val response = cartApi.addToCart("Bearer $token", AddToCartRequest(storeItemId))

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Erreur inconnue"
                Log.e("CartRepository", "Erreur addToCart: ${response.code()} - $errorBody")
                Result.failure(Exception("Erreur ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception addToCart: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Retirer un article du panier
     */
    suspend fun removeFromCart(context: Context, storeItemId: String): Result<Unit> {
        return try {
            val token = TokenManager.getToken(context)
                ?: return Result.failure(Exception("Token non disponible"))

            val response = cartApi.removeFromCart("Bearer $token", RemoveFromCartRequest(storeItemId))

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Erreur inconnue"
                Log.e("CartRepository", "Erreur removeFromCart: ${response.code()} - $errorBody")
                Result.failure(Exception("Erreur ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception removeFromCart: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Vider le panier
     */
    suspend fun clearCart(context: Context): Result<Unit> {
        return try {
            val token = TokenManager.getToken(context)
                ?: return Result.failure(Exception("Token non disponible"))

            val response = cartApi.clearCart("Bearer $token")

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Erreur inconnue"
                Log.e("CartRepository", "Erreur clearCart: ${response.code()} - $errorBody")
                Result.failure(Exception("Erreur ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception clearCart: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Vérifier le statut des articles dans le panier
     */
    suspend fun checkItemsStatus(context: Context): Result<Map<String, String>> {
        return try {
            val token = TokenManager.getToken(context)
                ?: return Result.failure(Exception("Token non disponible"))

            val response = cartApi.checkItemsStatus("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Erreur inconnue"
                Log.e("CartRepository", "Erreur checkItemsStatus: ${response.code()} - $errorBody")
                Result.failure(Exception("Erreur ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Exception checkItemsStatus: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Convertir CartItemResponse en CartItem local
     */
    private fun convertToCartItem(item: CartItemResponse): CartItem? {
        val storeItem = item.storeItem ?: return null
        val clothes = storeItem.clothesId

        // ✨ CORRIGÉ : Extraire le titre depuis category (comme dans StoreRepository)
        val categoryName = extractCategory(clothes?.category)
        val title = (clothes?.style?.takeIf { it.isNotBlank() } ?: categoryName)
            .replaceFirstChar { it.uppercaseChar() }
            .takeIf { it.isNotBlank() } ?: "Item"
        
        val status = storeItem.status // ✨ NOUVEAU : "available" | "sold"

        // ✨ MODIFIÉ : Si l'article est vendu et supprimé, certaines données peuvent être null
        // On utilise des valeurs par défaut pour éviter les erreurs
        return CartItem(
            id = item.storeItemId, // Utiliser storeItemId comme ID unique
            userId = "", // Sera rempli par CartManager
            storeItemID = item.storeItemId,
            title = title.ifBlank { "Item" }, // Valeur par défaut si vide
            size = storeItem.size?.takeIf { it.isNotBlank() } ?: "N/A", // Valeur par défaut si vide
            price = storeItem.price.takeIf { it > 0 } ?: 0.0, // Prix 0 si article supprimé
            imageURL = clothes?.imageUrl?.takeIf { it.isNotBlank() }, // Peut être null si article supprimé
            addedAt = parseDate(item.addedAt) ?: System.currentTimeMillis(),
            status = status // ✨ NOUVEAU : Inclure le statut
        )
    }

    /**
     * ✨ NOUVEAU : Extraire la catégorie (comme dans StoreRepository)
     */
    private fun extractCategory(category: String?): String {
        if (category.isNullOrBlank()) return ""
        return category.trim()
    }

    /**
     * Parser la date ISO 8601 (utilise SimpleDateFormat pour compatibilité avec le reste du code)
     */
    private fun parseDate(dateString: String?): Long? {
        if (dateString == null) return null
        return try {
            // Utiliser SimpleDateFormat comme dans le reste du projet
            val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            format.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = format.parse(dateString)
            date?.time
        } catch (e: Exception) {
            // Essayer un format sans millisecondes
            try {
                val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
                format.timeZone = java.util.TimeZone.getTimeZone("UTC")
                val date = format.parse(dateString)
                date?.time
            } catch (e2: Exception) {
                null
            }
        }
    }
}

