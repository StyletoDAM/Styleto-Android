package tn.esprit.labasniandroid.models.dto

import com.google.gson.annotations.SerializedName

data class VerifyOtpRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("code")
    val code: String
)

