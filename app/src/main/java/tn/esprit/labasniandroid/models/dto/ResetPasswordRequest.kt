package tn.esprit.labasniandroid.models.dto

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("resetToken")
    val resetToken: String,
    @SerializedName("newPassword")
    val newPassword: String
)

