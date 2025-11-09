package tn.esprit.labasniandroid.models

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("maskedPhoneNumber")
    val maskedPhoneNumber: String,
    @SerializedName("expiresAt")
    val expiresAt: String
)

