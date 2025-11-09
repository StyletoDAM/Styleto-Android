package tn.esprit.labasniandroid.models

import com.google.gson.annotations.SerializedName

data class ResetPasswordResponse(
    @SerializedName("message")
    val message: String
)

