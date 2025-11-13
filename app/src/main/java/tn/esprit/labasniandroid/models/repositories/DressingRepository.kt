package tn.esprit.labasniandroid.models.repositories

import com.google.gson.JsonElement
import tn.esprit.labasniandroid.api.ClothResponse
import tn.esprit.labasniandroid.api.ClothesApi
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.Cloth

class DressingRepository(
    private val clothesApi: ClothesApi = RetrofitClient.clothesApi
) {

    suspend fun fetchClothes(token: String): Result<List<Cloth>> {
        return try {
            val response = clothesApi.getClothes("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val clothes = response.body()!!.map { it.toEntity() }
                Result.success(clothes)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer le dressing."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun deleteCloth(token: String, clothId: String): Result<Unit> {
        return try {
            val response = clothesApi.deleteCloth("Bearer $token", clothId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Vêtement introuvable."
                    else -> errorBody ?: "Suppression impossible pour le moment."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
}

private fun ClothResponse.toEntity(): Cloth {
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
        8 -> withoutHash.substring(2) // ignore alpha if present
        else -> return defaultColor
    }
    return "#${hex.uppercase()}"
}

private fun buildPlaceholder(colorHex: String): String {
    val sanitized = colorHex.removePrefix("#")
    return "https://singlecolorimage.com/get/${sanitized.uppercase()}/400x400"
}

