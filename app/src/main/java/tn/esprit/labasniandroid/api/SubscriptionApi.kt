package tn.esprit.labasniandroid.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface SubscriptionApi {
    @GET("/subscriptions/me")
    suspend fun getMySubscription(
        @Header("Authorization") token: String
    ): Response<SubscriptionResponse>

    @GET("/subscriptions/me/stats")
    suspend fun getMyStats(
        @Header("Authorization") token: String
    ): Response<UsageStatsResponse>

    @GET("/subscriptions/quota/store-selling")
    suspend fun checkStoreSellingQuota(
        @Header("Authorization") token: String
    ): Response<QuotaCheckResponse>

    @PATCH("/subscriptions/me")
    suspend fun updateSubscription(
        @Header("Authorization") token: String,
        @Body request: UpdateSubscriptionRequest
    ): Response<UpgradePlanResponse>

    // ✨ NOUVEAU: Créer une Stripe Checkout Session
    @POST("/subscriptions/create-checkout-session")
    suspend fun createCheckoutSession(
        @Header("Authorization") token: String,
        @Body request: CreateCheckoutRequest
    ): Response<CheckoutSessionResponse>

    // ✨ NOUVEAU: Vérifier le statut d'une session après redirection
    @GET("/subscriptions/verify-session")
    suspend fun verifySession(
        @Header("Authorization") token: String,
        @Query("sessionId") sessionId: String
    ): Response<VerifySessionResponse>
    @DELETE("/subscriptions/cancel")
    suspend fun cancelSubscription(
        @Header("Authorization") token: String
    ): Response<CancelSubscriptionResponse>
}

// ✨ NOUVEAU: Request pour créer une checkout session
data class CreateCheckoutRequest(
    @SerializedName("plan") val plan: String, // "PREMIUM" ou "PRO_SELLER"
    @SerializedName("interval") val interval: String = "month" // "month" ou "year"
)

// ✨ NOUVEAU: Response avec l'URL de la page Stripe
data class CheckoutSessionResponse(
    @SerializedName("checkoutUrl") val checkoutUrl: String,
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("displayPrice") val displayPrice: String,
    @SerializedName("plan") val plan: String,
    @SerializedName("interval") val interval: String
)

// ✨ NOUVEAU: Response de vérification après paiement
data class VerifySessionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("plan") val plan: String?,
    @SerializedName("subscriptionId") val subscriptionId: String?
)

data class UpdateSubscriptionRequest(
    @SerializedName("plan") val plan: String
)

data class QuotaCheckResponse(
    @SerializedName("allowed") val allowed: Boolean,
    @SerializedName("remaining") val remaining: Any?,
    @SerializedName("limit") val limit: Any?,
    @SerializedName("plan") val plan: String,
    @SerializedName("message") val message: String?
)

data class UsageStatsResponse(
    @SerializedName("plan") val plan: String,
    @SerializedName("currentMonth") val currentMonth: String,
    @SerializedName("clothesDetection") val clothesDetection: QuotaInfo,
    @SerializedName("outfitSuggestions") val outfitSuggestions: QuotaInfo,
    @SerializedName("storeSelling") val storeSelling: QuotaInfo,
    @SerializedName("subscribedAt") val subscribedAt: String?,
    @SerializedName("expiresAt") val expiresAt: String?,
    @SerializedName("isActive") val isActive: Boolean?
)

data class QuotaInfo(
    @SerializedName("used") val used: Int,
    @SerializedName("limit") val limit: Any,
    @SerializedName("remaining") val remaining: Any
)

data class SubscriptionResponse(
    @SerializedName("plan") val plan: String,
    @SerializedName("subscribedAt") val subscribedAt: String?,
    @SerializedName("expiresAt") val expiresAt: String?,
    @SerializedName("isActive") val isActive: Boolean?,
    @SerializedName("status") val status: String? = "active", // ✅ AJOUT : Le backend retourne déjà ce champ
    @SerializedName("currentUsage") val currentUsage: MonthlyUsage?,
    @SerializedName("usageHistory") val usageHistory: List<MonthlyUsage>?,
    @SerializedName("_id") val id: String?
) {
    // ✅ Propriété calculée comme iOS
    val isCanceled: Boolean
        get() = status == "canceled"

    // ✅ Message d'expiration calculé
    val expirationMessage: String?
        get() {
            val expiresAt = this.expiresAt ?: return null
            return if (isCanceled) {
                "Access expires on $expiresAt"
            } else {
                "Renews on $expiresAt"
            }
        }
}

data class MonthlyUsage(
    @SerializedName("month") val month: String,
    @SerializedName("clothesDetectionUsed") val clothesDetectionUsed: Int,
    @SerializedName("outfitSuggestionsUsed") val outfitSuggestionsUsed: Int,
    @SerializedName("itemsSoldCount") val itemsSoldCount: Int,
    @SerializedName("lastReset") val lastReset: String?
)

data class UpgradePlanResponse(
    @SerializedName("message") val message: String,
    @SerializedName("subscription") val subscription: SubscriptionResponse
)