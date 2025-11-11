package tn.esprit.labasniandroid.models

import com.google.gson.annotations.SerializedName
import tn.esprit.labasniandroid.models.entities.User

// Auth Responses
object Responses {
    data class SigninResponse(
        @SerializedName("user")
        val user: User,
        @SerializedName("access_token")
        val accessToken: String
    )

    data class SignupResponse(
        @SerializedName("message")
        val message: String,
        @SerializedName("tempToken")
        val tempToken: String? = null,
        @SerializedName("user")
        val user: User? = null
    )

    data class ForgotPasswordResponse(
        @SerializedName("message")
        val message: String,
        @SerializedName("maskedPhoneNumber")
        val maskedPhoneNumber: String,
        @SerializedName("expiresAt")
        val expiresAt: String
    )

    data class VerifyOtpResponse(
        @SerializedName("message")
        val message: String,
        @SerializedName("resetToken")
        val resetToken: String
    )

    data class ResetPasswordResponse(
        @SerializedName("message")
        val message: String
    )

    data class VerifyEmailResponse(
        @SerializedName("message")
        val message: String,
        @SerializedName("user")
        val user: User
    )
}

