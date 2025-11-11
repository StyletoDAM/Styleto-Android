package tn.esprit.labasniandroid.models.repositories

import tn.esprit.labasniandroid.api.AppleAuthRequest
import tn.esprit.labasniandroid.api.ForgotPasswordRequest
import tn.esprit.labasniandroid.api.GoogleAuthRequest
import tn.esprit.labasniandroid.api.ResetPasswordRequest
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.SigninRequest
import tn.esprit.labasniandroid.api.SignupRequest
import tn.esprit.labasniandroid.api.VerifyEmailRequest
import tn.esprit.labasniandroid.api.VerifyOtpRequest
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.Responses
import tn.esprit.labasniandroid.models.entities.User

class AuthRepository {
    private val authApi = RetrofitClient.authApi

    suspend fun signin(email: String, password: String): Result<Responses.SigninResponse> {
        return try {
            val request = SigninRequest(email = email.lowercase().trim(), password = password)
            val response = authApi.signin(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    401 -> "Identifiants invalides."
                    else -> errorBody ?: "Une erreur est survenue."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun signup(
        fullName: String,
        email: String,
        password: String,
        gender: User.Gender,
        phoneNumber: String? = null,
        preferences: List<String>? = null
    ): Result<Responses.SignupResponse> {
        return try {
            val request = SignupRequest(
                fullName = fullName.trim(),
                email = email.lowercase().trim(),
                password = password,
                gender = gender,
                phoneNumber = phoneNumber?.takeIf { it.isNotBlank() }?.trim(),
                preferences = preferences
            )
            val response = authApi.signup(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    409 -> "Email déjà utilisé."
                    else -> errorBody ?: "Une erreur est survenue."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun authenticateGoogle(
        googleId: String,
        fullName: String,
        email: String,
        profilePicture: String? = null,
        gender: String? = null
    ): Result<Responses.SigninResponse> {
        return try {
            val request = GoogleAuthRequest(
                googleId = googleId,
                fullName = fullName,
                email = email,
                profilePicture = profilePicture,
                gender = gender
            )
            val response = authApi.googleAuth(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Échec de l'authentification Google."
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun authenticateApple(
        appleId: String,
        fullName: String,
        email: String,
        profilePicture: String? = null,
        gender: String? = null
    ): Result<Responses.SigninResponse> {
        return try {
            val request = AppleAuthRequest(
                appleId = appleId,
                fullName = fullName,
                email = email,
                profilePicture = profilePicture,
                gender = gender
            )
            val response = authApi.appleAuth(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Échec de l'authentification Apple."
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun forgotPassword(email: String): Result<Responses.ForgotPasswordResponse> {
        return try {
            val request = ForgotPasswordRequest(email = email.lowercase().trim())
            val response = authApi.forgotPassword(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(NetworkError.ServerMessage(errorBody ?: "Une erreur est survenue."))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun verifyOtp(email: String, code: String): Result<Responses.VerifyOtpResponse> {
        return try {
            val request = VerifyOtpRequest(email = email.lowercase().trim(), code = code)
            val response = authApi.verifyOtp(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(NetworkError.ServerMessage(errorBody ?: "Code invalide."))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun resetPassword(resetToken: String, newPassword: String): Result<Responses.ResetPasswordResponse> {
        return try {
            val request = ResetPasswordRequest(resetToken = resetToken, newPassword = newPassword)
            val response = authApi.resetPassword(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(NetworkError.ServerMessage(errorBody ?: "Une erreur est survenue."))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun verifyEmail(tempToken: String, code: String): Result<Responses.VerifyEmailResponse> {
        return try {
            val request = VerifyEmailRequest(tempToken = tempToken, code = code)
            val response = authApi.verifyEmail(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(NetworkError.ServerMessage(errorBody ?: "Code invalide."))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }
}
