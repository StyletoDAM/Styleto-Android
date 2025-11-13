package tn.esprit.labasniandroid.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.POST
import tn.esprit.labasniandroid.models.Responses
import tn.esprit.labasniandroid.models.entities.User
import okhttp3.MultipartBody

// DTOs intégrés directement dans l'API (même structure que dans AuthRepository)
data class SigninRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SignupRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("gender") val gender: User.Gender,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("preferences") val preferences: List<String>? = null
)

data class GoogleAuthRequest(
    @SerializedName("googleId") val googleId: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("profilePicture") val profilePicture: String? = null,
    @SerializedName("gender") val gender: String? = null
)

data class AppleAuthRequest(
    @SerializedName("appleId") val appleId: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("profilePicture") val profilePicture: String? = null,
    @SerializedName("gender") val gender: String? = null
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class VerifyEmailRequest(
    @SerializedName("tempToken") val tempToken: String,
    @SerializedName("code") val code: String
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String
)

data class ResetPasswordRequest(
    @SerializedName("resetToken") val resetToken: String,
    @SerializedName("newPassword") val newPassword: String
)

data class UpdateProfileRequest(
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("preferences") val preferences: List<String>? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("profilePicture") val profilePicture: String? = null
)

interface AuthApi {
    @POST("/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Responses.SignupResponse>

    @POST("/auth/signin")
    suspend fun signin(@Body request: SigninRequest): Response<Responses.SigninResponse>

    @POST("/auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): Response<Responses.SigninResponse>

    @POST("/auth/apple")
    suspend fun appleAuth(@Body request: AppleAuthRequest): Response<Responses.SigninResponse>

    @GET("/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<User>

    @PATCH("/auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<User>

    @Multipart
    @PATCH("/auth/profile/photo")
    suspend fun updateProfilePhoto(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<User>

    @POST("/auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<Responses.VerifyEmailResponse>

    @POST("/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Responses.ForgotPasswordResponse>

    @POST("/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<Responses.VerifyOtpResponse>

    @POST("/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Responses.ResetPasswordResponse>

    @DELETE("/auth/profile")
    suspend fun deleteProfile(@Header("Authorization") token: String): Response<Responses.MessageResponse>
}
