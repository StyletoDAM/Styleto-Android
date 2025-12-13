package tn.esprit.labasniandroid.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface OrdersApi {
    @GET("/orders")
    suspend fun getMyOrders(
        @Header("Authorization") token: String
    ): Response<List<OrderResponse>>

    @GET("/orders/history")
    suspend fun getUnifiedHistory(
        @Header("Authorization") token: String
    ): Response<List<HistoryItemResponse>>

    @POST("/orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): Response<OrderResponse>
}

data class OrderResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("clothesId") val clothesId: Any, // String ou ClothInfo
    @SerializedName("userId") val userId: Any, // String ou UserInfo
    @SerializedName("price") val price: Double,
    @SerializedName("orderDate") val orderDate: String,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class ClothInfo(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("type") val type: String?, // Fallback pour compatibilité
    @SerializedName("imageURL") val imageURL: String?, // Champ réel du backend
    @SerializedName("imageUrl") val imageUrl: String? // Fallback pour compatibilité
) {
    // Extension pour obtenir l'URL de l'image (priorité à imageURL)
    val displayImageUrl: String?
        get() = imageURL ?: imageUrl
    
    // Extension pour obtenir le type/category
    val displayType: String?
        get() = category ?: type
}

data class UserInfo(
    @SerializedName("_id") val id: String,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?
)

data class CreateOrderRequest(
    @SerializedName("clothesId") val clothesId: String,
    @SerializedName("price") val price: Double
)

// MARK: - History Models (exactement comme iOS)
data class HistoryItemResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("type") val type: String, // "Purchased" ou "Sold"
    @SerializedName("clothesId") val clothesId: ClothInfo,
    @SerializedName("price") val price: Double,
    @SerializedName("date") val date: String, // ISO8601 date string
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("size") val size: String?
) {
    val isPurchased: Boolean
        get() = type == "Purchased"
    
    val isSold: Boolean
        get() = type == "Sold"
}

