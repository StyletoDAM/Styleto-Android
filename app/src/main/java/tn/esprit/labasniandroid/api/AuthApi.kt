package tn.esprit.labasniandroid.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import tn.esprit.labasniandroid.models.SigninResponse
import tn.esprit.labasniandroid.models.SignupResponse
import tn.esprit.labasniandroid.models.User
import tn.esprit.labasniandroid.models.VerifyEmailResponse
import tn.esprit.labasniandroid.models.ForgotPasswordResponse
import tn.esprit.labasniandroid.models.VerifyOtpResponse
import tn.esprit.labasniandroid.models.ResetPasswordResponse
import tn.esprit.labasniandroid.models.dto.SigninRequest
import tn.esprit.labasniandroid.models.dto.SignupRequest
import tn.esprit.labasniandroid.models.dto.GoogleAuthRequest
import tn.esprit.labasniandroid.models.dto.AppleAuthRequest
import tn.esprit.labasniandroid.models.dto.VerifyEmailRequest
import tn.esprit.labasniandroid.models.dto.ForgotPasswordRequest
import tn.esprit.labasniandroid.models.dto.VerifyOtpRequest
import tn.esprit.labasniandroid.models.dto.ResetPasswordRequest

interface AuthApi {
    @POST("/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("/auth/signin")
    suspend fun signin(@Body request: SigninRequest): Response<SigninResponse>

    @POST("/auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): Response<SigninResponse>

    @POST("/auth/apple")
    suspend fun appleAuth(@Body request: AppleAuthRequest): Response<SigninResponse>

    @GET("/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<User>

    @POST("/auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<VerifyEmailResponse>

    @POST("/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>

    @POST("/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>
}

