package tn.esprit.labasniandroid.models.repositories

import tn.esprit.labasniandroid.api.RecommendationsApi
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.RecommendOutfitRequest
import tn.esprit.labasniandroid.api.AIRecommendationResponse
import tn.esprit.labasniandroid.models.NetworkError

class RecommendationsRepository(
    private val recommendationsApi: RecommendationsApi = RetrofitClient.recommendationsApi
) {
    suspend fun recommendOutfit(
        token: String,
        preference: String,
        city: String? = null,
        temperature: Double? = null
    ): Result<AIRecommendationResponse> {
        val request = RecommendOutfitRequest(
            preference = preference.lowercase(),
            city = city,
            temperature = temperature
        )

        return try {
            val response = recommendationsApi.recommendOutfit("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                // ✨ NOUVEAU: Extraire le message d'erreur du JSON de réponse
                val message = try {
                    if (errorBody != null) {
                        // Essayer de parser le JSON pour extraire le message
                        val jsonObject = org.json.JSONObject(errorBody)
                        jsonObject.optString("message", errorBody)
                    } else {
                        when (response.code()) {
                            400 -> "You don't have enough clothes for this style. Please add more items to your wardrobe."
                            401 -> "Session expired. Please log in again."
                            500 -> "Server error. Please try again later."
                            else -> "Unable to generate a recommendation at this time."
                        }
                    }
                } catch (e: Exception) {
                    // Si le parsing échoue, utiliser le message brut ou un message par défaut
                    errorBody ?: when (response.code()) {
                        400 -> "You don't have enough clothes for this style. Please add more items to your wardrobe."
                        401 -> "Session expired. Please log in again."
                        500 -> "Server error. Please try again later."
                        else -> "Unable to generate a recommendation at this time."
                    }
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: java.net.SocketTimeoutException) {
            Result.failure(NetworkError.ServerMessage(
                "The recommendation is taking longer than expected. " +
                "The AI model is processing. Please try again in a few moments."
            ))
        } catch (exception: java.io.IOException) {
            val errorMessage = when {
                exception.message?.contains("timeout", ignoreCase = true) == true -> 
                    "The recommendation is taking too long. Please try again."
                exception.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                    "Connection problem. Please check your internet connection."
                else -> 
                    "Connection error: ${exception.message ?: "Please try again."}"
            }
            Result.failure(NetworkError.Transport(Exception(errorMessage)))
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
}

