package tn.esprit.labasniandroid.api

import com.google.gson.annotations.SerializedName

data class CancelSubscriptionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("success") val success: Boolean? = true,
    @SerializedName("expiresAt") val expiresAt: String? = null,
    @SerializedName("plan") val plan: String? = null,
    @SerializedName("status") val status: String? = null // ✅ AJOUT
)