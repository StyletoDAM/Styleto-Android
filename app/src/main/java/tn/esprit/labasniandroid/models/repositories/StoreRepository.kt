package tn.esprit.labasniandroid.models.repositories

import com.google.gson.JsonElement
import tn.esprit.labasniandroid.api.ClothResponse
import tn.esprit.labasniandroid.api.CreateStoreItemRequest
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.StoreApi
import tn.esprit.labasniandroid.api.StoreItemResponse
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.StoreItem

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
        price: Double
    ): Result<StoreItem> {
        val request = CreateStoreItemRequest(
            clothesId = clothesId,
            price = price
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

    suspend fun updateStorePrice(token: String, storeItemId: String, price: Double): Result<StoreItem> {
        return try {
            val request = tn.esprit.labasniandroid.api.UpdateStoreItemRequest(price = price)
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
    
    return StoreItem(
        id = id,
        cloth = cloth,
        price = price,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
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

