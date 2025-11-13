package tn.esprit.labasniandroid.api

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ClothesApi {
    @GET("/cloth")
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
}

data class ClothResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("userId") val user: JsonElement?,
    @SerializedName("imageURL") val imageUrl: String?,
    @SerializedName("category") val category: JsonElement?,
    @SerializedName("season") val season: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("style") val style: String?,
    @SerializedName("createdAt") val createdAt: String?
)

data class CreateClothRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("imageURL") val imageUrl: String,
    @SerializedName("category") val category: String,
    @SerializedName("color") val color: String?,
    @SerializedName("style") val style: String?
)

