package tn.esprit.labasniandroid.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface OutfitsApi {
    @GET("/outfits/my")
    suspend fun getOutfits(
        @Header("Authorization") token: String
    ): Response<List<OutfitResponse>>

    @POST("/outfits")
    suspend fun createOutfit(
        @Header("Authorization") token: String,
        @Body request: CreateOutfitRequest
    ): Response<Unit>

    @DELETE("/outfits/{id}")
    suspend fun deleteOutfit(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}

data class OutfitResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("userId") val user: Any?,
    @SerializedName("clothesIds") val clothes: List<ClothResponse>?,
    @SerializedName("eventType") val eventType: String?,
    @SerializedName("weatherType") val weatherType: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class CreateOutfitRequest(
    @SerializedName("clothesIds") val clothesIds: List<String>,
    @SerializedName("eventType") val eventType: String?,
    @SerializedName("status") val status: String? = "pending"
)
