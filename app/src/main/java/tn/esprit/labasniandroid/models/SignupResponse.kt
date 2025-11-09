package tn.esprit.labasniandroid.models

import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("tempToken")
    val tempToken: String? = null,
    @SerializedName("user")
    val user: User? = null
)

