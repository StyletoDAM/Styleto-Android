package tn.esprit.labasniandroid.models

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("resetToken")
    val resetToken: String
)

