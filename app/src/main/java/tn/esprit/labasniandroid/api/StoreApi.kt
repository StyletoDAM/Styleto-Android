package tn.esprit.labasniandroid.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path

interface StoreApi {
    @GET("/store/my")
    suspend fun getStoreItems(
        @Header("Authorization") token: String
    ): Response<List<StoreItemResponse>>

    @POST("/store")
    suspend fun createStoreItem(
        @Header("Authorization") token: String,
        @Body request: CreateStoreItemRequest
    ): Response<StoreItemResponse>

    @DELETE("/store/{id}")
    suspend fun deleteStoreItem(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}

data class StoreItemResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("userId") val user: Any?,
    @SerializedName("clothesId") val clothes: ClothResponse?,
    @SerializedName("price") val price: Double,
    @SerializedName("status") val status: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class CreateStoreItemRequest(
    @SerializedName("clothesId") val clothesId: String,
    @SerializedName("price") val price: Double
)

