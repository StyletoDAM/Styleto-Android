package tn.esprit.labasniandroid.models

import com.google.gson.annotations.SerializedName

data class VerifyEmailResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: User
)

