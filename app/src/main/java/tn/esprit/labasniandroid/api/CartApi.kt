package tn.esprit.labasniandroid.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface CartApi {
    @GET("/cart")
    suspend fun getCart(
        @Header("Authorization") token: String
    ): Response<CartResponse>

    @POST("/cart/add")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): Response<CartResponse>

    @POST("/cart/remove")
    suspend fun removeFromCart(
        @Header("Authorization") token: String,
        @Body request: RemoveFromCartRequest
    ): Response<CartResponse>

    @DELETE("/cart/clear")
    suspend fun clearCart(
        @Header("Authorization") token: String
    ): Response<ClearCartResponse>

    @GET("/cart/check-status")
    suspend fun checkItemsStatus(
        @Header("Authorization") token: String
    ): Response<Map<String, String>> // { storeItemId: "available" | "sold" }
}

data class AddToCartRequest(
    @SerializedName("storeItemId") val storeItemId: String
)

data class RemoveFromCartRequest(
    @SerializedName("storeItemId") val storeItemId: String
)

data class ClearCartResponse(
    @SerializedName("message") val message: String
)

data class CartResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("items") val items: List<CartItemResponse>,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class CartItemResponse(
    @SerializedName("storeItemId") val storeItemId: String,
    @SerializedName("addedAt") val addedAt: String,
    @SerializedName("storeItem") val storeItem: StoreItemInCartResponse?
)

data class StoreItemInCartResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("price") val price: Double,
    @SerializedName("size") val size: String,
    @SerializedName("status") val status: String, // ✨ NOUVEAU : "available" | "sold"
    @SerializedName("clothesId") val clothesId: ClothesInCartResponse?,
    @SerializedName("userId") val userId: UserInCartResponse?
)

data class ClothesInCartResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("imageURL") val imageUrl: String?, // ✨ CORRIGÉ : Backend retourne "imageURL" (majuscules)
    @SerializedName("category") val category: String?,
    @SerializedName("style") val style: String?,
    @SerializedName("color") val color: String?
)

data class UserInCartResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("profilePicture") val profilePicture: String?
)

