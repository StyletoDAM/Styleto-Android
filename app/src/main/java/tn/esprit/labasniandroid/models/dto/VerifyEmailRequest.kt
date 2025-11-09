package tn.esprit.labasniandroid.models.dto

import com.google.gson.annotations.SerializedName

data class VerifyEmailRequest(
    @SerializedName("tempToken")
    val tempToken: String,
    @SerializedName("code")
    val code: String
)

