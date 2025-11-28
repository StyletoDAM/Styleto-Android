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
}

data class UpdateSubscriptionRequest(
    @SerializedName("plan") val plan: String
)

data class QuotaCheckResponse(
    @SerializedName("allowed") val allowed: Boolean,
    @SerializedName("remaining") val remaining: Any?, // Int ou "unlimited"
    @SerializedName("limit") val limit: Any?, // Int ou "unlimited"
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
    @SerializedName("limit") val limit: Any, // Int ou "unlimited"
    @SerializedName("remaining") val remaining: Any // Int ou "unlimited"
)

data class SubscriptionResponse(
    @SerializedName("plan") val plan: String,
    @SerializedName("subscribedAt") val subscribedAt: String?,
    @SerializedName("expiresAt") val expiresAt: String?,
    @SerializedName("isActive") val isActive: Boolean?,
    @SerializedName("currentUsage") val currentUsage: MonthlyUsage?,
    @SerializedName("usageHistory") val usageHistory: List<MonthlyUsage>?,
    @SerializedName("_id") val id: String?
)

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

