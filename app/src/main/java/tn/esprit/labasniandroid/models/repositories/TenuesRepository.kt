package tn.esprit.labasniandroid.models.repositories

import tn.esprit.labasniandroid.api.ClothResponse
import tn.esprit.labasniandroid.api.OutfitResponse
import tn.esprit.labasniandroid.api.OutfitsApi
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.CreateOutfitRequest
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.Outfit

class TenuesRepository(
    private val outfitsApi: OutfitsApi = RetrofitClient.outfitsApi
) {

    suspend fun fetchOutfits(token: String): Result<List<Outfit>> {
        return try {
            val response = outfitsApi.getOutfits("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val outfits = response.body()!!.map { it.toEntity() }
                Result.success(outfits)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer les tenues."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun createOutfit(
        token: String,
        userId: String,
        clothesIds: List<String>,
        eventType: String?,
        status: String? = "pending"
    ): Result<Unit> {
        val request = CreateOutfitRequest(
            clothesIds = clothesIds,
            eventType = eventType,
            status = status
        )

        return try {
            val response = outfitsApi.createOutfit("Bearer $token", request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Les informations de la tenue sont invalides."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Création impossible pour le moment."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun deleteOutfit(token: String, outfitId: String): Result<Unit> {
        return try {
            val response = outfitsApi.deleteOutfit("Bearer $token", outfitId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Tenue introuvable."
                    else -> errorBody ?: "Suppression impossible pour le moment."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
}

private fun OutfitResponse.toEntity(): Outfit {
    val clothesEntities = this.clothes?.map { it.toEntity() } ?: emptyList()
    return Outfit(
        id = id,
        clothes = clothesEntities,
        eventType = eventType,
        weatherType = weatherType,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isFavorite = false
    )
}

private fun ClothResponse.toEntity(): Cloth {
    val name = extractCategory(category)
    val type = style ?: name
    val normalizedColor = normalizeColor(color)
    val image = imageUrl?.takeIf { it.isNotBlank() } ?: buildPlaceholder(normalizedColor)

    return Cloth(
        id = id,
        name = name.ifBlank { "Vêtement" },
        type = type.ifBlank { "Autre" },
        colorHex = normalizedColor,
        imageUrl = image,
        createdAt = null
    )
}

private fun extractCategory(category: Any?): String {
    if (category == null) return ""
    return when (category) {
        is List<*> -> category.filterIsInstance<String>().joinToString(", ")
        is String -> category
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

