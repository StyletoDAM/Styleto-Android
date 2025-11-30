package tn.esprit.labasniandroid.api

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Path

interface StoreApi {
    @GET("/store/my")
    suspend fun getStoreItems(
        @Header("Authorization") token: String
    ): Response<List<StoreItemResponse>>

    @GET("/store")
    suspend fun getAllStoreItems(
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

    @PATCH("/store/{id}")
    suspend fun updateStoreItem(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: UpdateStoreItemRequest
    ): Response<StoreItemResponse>

    @POST("/store/payment-intent")
    suspend fun createPaymentIntent(
        @Header("Authorization") token: String,
        @Body request: CreatePaymentIntentRequest
    ): Response<PaymentIntentResponse>

    @POST("/store/purchase/{id}")
    suspend fun confirmPurchase(
        @Header("Authorization") token: String,
        @Path("id") storeItemId: String,
        @Body request: ConfirmPurchaseRequest
    ): Response<StoreItemResponse>
}

data class StoreItemResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("userId") val user: Any?,
    @SerializedName("clothesId") val clothesId: JsonElement?, // Accepte string ou objet
    @SerializedName("price") val price: Double,
    @SerializedName("size") val size: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class CreateStoreItemRequest(
    @SerializedName("clothesId") val clothesId: String,
    @SerializedName("price") val price: Double,
    @SerializedName("size") val size: String
)

data class CreatePaymentIntentRequest(
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String? = null
)

data class PaymentIntentResponse(
    @SerializedName("clientSecret") val clientSecret: String
)

data class ConfirmPurchaseRequest(
    @SerializedName("paymentMethod") val paymentMethod: String, // "balance" ou "card"
    @SerializedName("paymentIntentId") val paymentIntentId: String? = null // Requis seulement pour "card"
)

data class UpdateStoreItemRequest(
    @SerializedName("price") val price: Double? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("size") val size: String? = null
)

