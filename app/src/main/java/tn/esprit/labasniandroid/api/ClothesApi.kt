package tn.esprit.labasniandroid.api

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ClothesApi {
    @GET("/cloth/my")
    suspend fun getClothes(
        @Header("Authorization") token: String
    ): Response<List<ClothResponse>>

    @POST("/cloth")
    suspend fun createCloth(
        @Header("Authorization") token: String,
        @Body request: CreateClothRequest
    ): Response<ClothResponse>

    @DELETE("/cloth/{id}")
    suspend fun deleteCloth(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @Multipart
    @POST("/detect")
    suspend fun detectCloth(
        @Part photo: MultipartBody.Part
    ): Response<DetectionApiResponse>

    @PATCH("/cloth/{id}/feedback")
    suspend fun updateFeedback(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: UpdateFeedbackRequest
    ): Response<Unit>

    @GET("/cloth/sell-suggestions")
    suspend fun getSellSuggestions(
        @Header("Authorization") token: String
    ): Response<List<ClothResponse>>
}

data class UpdateFeedbackRequest(
    @SerializedName("accepted") val accepted: Boolean
)

data class ClothResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("userId") val user: JsonElement?,
    @SerializedName("imageURL") val imageUrl: String?,
    @SerializedName("category") val category: JsonElement?,
    @SerializedName("season") val season: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("style") val style: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("acceptedCount") val acceptedCount: Int? = null,
    @SerializedName("rejectedCount") val rejectedCount: Int? = null
)

data class CreateClothRequest(
    @SerializedName("imageURL") val imageUrl: String,
    @SerializedName("category") val category: String,
    @SerializedName("color") val color: String,
    @SerializedName("style") val style: String,
    @SerializedName("season") val season: String
)

data class DetectionApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("public_id") val publicId: String?,
    @SerializedName("detection_result") val detectionResult: String
)

