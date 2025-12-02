package tn.esprit.labasniandroid.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RecommendationsApi {
    @POST("/recommendations/outfit")
    suspend fun recommendOutfit(
        @Header("Authorization") token: String,
        @Body request: RecommendOutfitRequest
    ): Response<AIRecommendationResponse>
}

data class RecommendOutfitRequest(
    @SerializedName("preference") val preference: String, // "casual", "formal", "sport"
    @SerializedName("city") val city: String? = null,
    @SerializedName("temperature") val temperature: Double? = null
)

// MARK: - AI Recommendation Response (comme iOS)
data class AIRecommendationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("outfit") val outfit: AIOutfitItems?,
    @SerializedName("metadata") val metadata: AIMetadata?,
    @SerializedName("clothesIds") val clothesIds: List<String>?
)

data class AIOutfitItems(
    @SerializedName("top") val top: RecommendedCloth?,
    @SerializedName("bottom") val bottom: RecommendedCloth?,
    @SerializedName("footwear") val footwear: RecommendedCloth?
)

data class RecommendedCloth(
    @SerializedName("_id") val id: String,
    @SerializedName("imageURL") val imageUrl: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("style") val style: String?,
    @SerializedName("season") val season: String?,
    @SerializedName("userId") val userId: String?,
    @SerializedName("acceptedCount") val acceptedCount: Int?,
    @SerializedName("rejectedCount") val rejectedCount: Int?
)

data class AIMetadata(
    @SerializedName("weather") val weather: WeatherInfo?,
    @SerializedName("season") val season: String?,
    @SerializedName("preference") val preference: String?,
    @SerializedName("explanation") val explanation: Map<String, Any>?
)

data class WeatherInfo(
    @SerializedName("temperature") val temperature: Double?,
    @SerializedName("condition") val condition: String?,
    @SerializedName("city") val city: String?
)

