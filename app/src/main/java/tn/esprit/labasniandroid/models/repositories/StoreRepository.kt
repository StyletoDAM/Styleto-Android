package tn.esprit.labasniandroid.models.repositories

import com.google.gson.JsonElement
import tn.esprit.labasniandroid.api.ClothResponse
import tn.esprit.labasniandroid.api.ConfirmPurchaseRequest
import tn.esprit.labasniandroid.api.CreatePaymentIntentRequest
import tn.esprit.labasniandroid.api.CreateStoreItemRequest
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.StoreApi
import tn.esprit.labasniandroid.api.StoreItemResponse
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.StoreItem
import tn.esprit.labasniandroid.models.entities.User

class StoreRepository(
    private val storeApi: StoreApi = RetrofitClient.storeApi
) {

    suspend fun fetchStoreItems(token: String): Result<List<StoreItem>> {
        return try {
            val response = storeApi.getStoreItems("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val items = response.body()!!.map { it.toEntity() }
                Result.success(items)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer la boutique."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun fetchAllStoreItems(token: String): Result<List<StoreItem>> {
        return try {
            val response = storeApi.getAllStoreItems("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val items = response.body()!!.map { it.toEntity() }
                Result.success(items)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer la boutique."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun addStoreItem(
        token: String,
        clothesId: String,
        price: Double,
        size: String
    ): Result<StoreItem> {
        val request = CreateStoreItemRequest(
            clothesId = clothesId,
            price = price,
            size = size
        )

        return try {
            val response = storeApi.createStoreItem("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Impossible d'ajouter cet article. Vérifiez les informations."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Ajout impossible pour le moment."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun deleteStoreItem(token: String, storeItemId: String): Result<Unit> {
        return try {
            val response = storeApi.deleteStoreItem("Bearer $token", storeItemId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Article introuvable."
                    else -> errorBody ?: "Suppression impossible pour le moment."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun updateStorePrice(
        token: String,
        storeItemId: String,
        price: Double? = null,
        size: String? = null
    ): Result<StoreItem> {
        return try {
            val request = tn.esprit.labasniandroid.api.UpdateStoreItemRequest(
                price = price,
                size = size
            )
            val response = storeApi.updateStoreItem("Bearer $token", storeItemId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Article introuvable."
                    else -> errorBody ?: "Mise à jour impossible pour le moment."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun markAsSold(token: String, storeItemId: String): Result<StoreItem> {
        return try {
            val request = tn.esprit.labasniandroid.api.UpdateStoreItemRequest(status = "sold")
            val response = storeApi.updateStoreItem("Bearer $token", storeItemId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Article introuvable."
                    else -> errorBody ?: "Mise à jour impossible pour le moment."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun createPaymentIntent(token: String, amount: Double, currency: String? = null): Result<String> {
        return try {
            val request = CreatePaymentIntentRequest(amount = amount, currency = currency)
            val response = storeApi.createPaymentIntent("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.clientSecret)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Montant invalide."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de créer le paiement."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun confirmPurchase(token: String, storeItemId: String, paymentIntentId: String): Result<StoreItem> {
        return try {
            val request = ConfirmPurchaseRequest(paymentIntentId = paymentIntentId)
            val response = storeApi.confirmPurchase("Bearer $token", storeItemId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Paiement échoué ou article déjà vendu."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Article introuvable."
                    else -> errorBody ?: "Impossible de confirmer l'achat."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
}

private fun StoreItemResponse.toEntity(): StoreItem {
    // Gérer le cas où clothesId peut être une string (ID) ou un objet (ClothResponse)
    val cloth = when {
        clothesId == null || clothesId.isJsonNull -> null
        clothesId.isJsonPrimitive -> null // C'est juste l'ID, pas l'objet complet
        clothesId.isJsonObject -> {
            try {
                // Essayer de parser comme ClothResponse
                val clothResponse = com.google.gson.Gson().fromJson(clothesId, ClothResponse::class.java)
                clothResponse.toClothEntity()
            } catch (e: Exception) {
                null
            }
        }
        else -> null
    }
    
    // Parser l'owner depuis le champ user (peut être String ou User object)
    val (ownerId, ownerName, ownerAvatar) = try {
        when {
            user == null -> {
                android.util.Log.d("StoreRepository", "user is null")
                Triple(null, null, null)
            }
            user is String -> {
                android.util.Log.d("StoreRepository", "user is String: $user")
                Triple(user, null, null)
            }
            user is JsonElement -> {
                if (user.isJsonObject) {
                    android.util.Log.d("StoreRepository", "user is JsonObject")
                    val userObj = com.google.gson.Gson().fromJson(user, User::class.java)
                    val id = userObj.userId
                    android.util.Log.d("StoreRepository", "Parsed ownerId: $id, name: ${userObj.fullName}")
                    Triple(
                        id,
                        userObj.fullName,
                        userObj.profilePicture
                    )
                } else if (user.isJsonPrimitive) {
                    android.util.Log.d("StoreRepository", "user is JsonPrimitive: ${user.asString}")
                    Triple(user.asString, null, null)
                } else {
                    android.util.Log.d("StoreRepository", "user is JsonElement but not object or primitive")
                    Triple(null, null, null)
                }
            }
            user is com.google.gson.JsonObject -> {
                android.util.Log.d("StoreRepository", "user is JsonObject (direct)")
                val userObj = com.google.gson.Gson().fromJson(user, User::class.java)
                val id = userObj.userId
                android.util.Log.d("StoreRepository", "Parsed ownerId: $id, name: ${userObj.fullName}")
                Triple(
                    id,
                    userObj.fullName,
                    userObj.profilePicture
                )
            }
            else -> {
                android.util.Log.d("StoreRepository", "user is unknown type: ${user?.javaClass?.name}")
                // Essayer de convertir en JsonElement
                try {
                    val userJson = com.google.gson.Gson().toJsonTree(user)
                    if (userJson.isJsonObject) {
                        val userObj = com.google.gson.Gson().fromJson(userJson, User::class.java)
                        Triple(
                            userObj.userId,
                            userObj.fullName,
                            userObj.profilePicture
                        )
                    } else if (userJson.isJsonPrimitive) {
                        Triple(userJson.asString, null, null)
                    } else {
                        Triple(null, null, null)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("StoreRepository", "Error parsing user: ${e.message}", e)
                    Triple(null, null, null)
                }
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("StoreRepository", "Error in owner parsing: ${e.message}", e)
        Triple(null, null, null)
    }
    
    return StoreItem(
        id = id,
        cloth = cloth,
        price = price,
        size = size,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        ownerId = ownerId,
        ownerName = ownerName,
        ownerAvatar = ownerAvatar
    )
}

private fun ClothResponse.toClothEntity(): Cloth {
    val name = extractCategory(category)
    val type = style?.takeIf { it.isNotBlank() } ?: name
    val normalizedColor = normalizeColor(color)
    val image = imageUrl?.takeIf { it.isNotBlank() } ?: buildPlaceholder(normalizedColor)

    return Cloth(
        id = id,
        name = name.ifBlank { "Vêtement" },
        type = type.ifBlank { "Autre" },
        colorHex = normalizedColor,
        imageUrl = image,
        createdAt = createdAt
    )
}

private fun extractCategory(categoryElement: JsonElement?): String {
    if (categoryElement == null || categoryElement.isJsonNull) return ""

    return when {
        categoryElement.isJsonArray -> categoryElement.asJsonArray
            .mapNotNull { element ->
                element.takeIf { !it.isJsonNull }?.asString
            }
            .joinToString(separator = ", ")
        categoryElement.isJsonPrimitive -> categoryElement.asString
        else -> ""
    }.trim()
}

private fun normalizeColor(rawColor: String?): String {
    val defaultColor = "#F6D4E3"
    if (rawColor.isNullOrBlank()) return defaultColor

    val candidate = rawColor.trim()
    val withoutHash = candidate.removePrefix("#")
    val hex = when (withoutHash.length) {
        3 -> withoutHash.flatMap { listOf(it, it) }.joinToString(separator = "")
        6 -> withoutHash
        8 -> withoutHash.substring(2)
        else -> return defaultColor
    }
    return "#${hex.uppercase()}"
}

private fun buildPlaceholder(colorHex: String): String {
    val sanitized = colorHex.removePrefix("#")
    return "https://singlecolorimage.com/get/${sanitized.uppercase()}/400x400"
}

