package tn.esprit.labasniandroid.services

import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.ForgotPasswordResponse
import tn.esprit.labasniandroid.models.VerifyOtpResponse
import tn.esprit.labasniandroid.models.ResetPasswordResponse
import tn.esprit.labasniandroid.models.dto.ForgotPasswordRequest
import tn.esprit.labasniandroid.models.dto.VerifyOtpRequest
import tn.esprit.labasniandroid.models.dto.ResetPasswordRequest

class ForgotPasswordService {
    private val authApi = RetrofitClient.authApi

    suspend fun requestOtp(email: String): Result<ForgotPasswordResponse> {
        return try {
            val request = ForgotPasswordRequest(email = email.lowercase().trim())
            val response = authApi.forgotPassword(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    404 -> "Aucun compte associé à cet email."
                    400 -> errorBody ?: "Numéro de téléphone invalide ou compte non vérifié."
                    else -> errorBody ?: "Une erreur est survenue."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun verifyOtp(email: String, code: String): Result<VerifyOtpResponse> {
        return try {
            val request = VerifyOtpRequest(
                email = email.lowercase().trim(),
                code = code.trim()
            )
            val response = authApi.verifyOtp(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    401 -> errorBody ?: "Code incorrect ou expiré."
                    else -> errorBody ?: "Une erreur est survenue."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun resetPassword(resetToken: String, newPassword: String): Result<ResetPasswordResponse> {
        return try {
            val request = ResetPasswordRequest(
                resetToken = resetToken,
                newPassword = newPassword
            )
            val response = authApi.resetPassword(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    401 -> "Lien de réinitialisation invalide ou expiré."
                    else -> errorBody ?: "Une erreur est survenue."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }
}

