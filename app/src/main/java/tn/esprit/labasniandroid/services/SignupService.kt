package tn.esprit.labasniandroid.services

import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.SignupResponse
import tn.esprit.labasniandroid.models.User
import tn.esprit.labasniandroid.models.dto.SignupRequest

class SignupService {
    private val authApi = RetrofitClient.authApi

    suspend fun signup(
        fullName: String,
        email: String,
        password: String,
        gender: User.Gender,
        phoneNumber: String? = null,
        preferences: List<String>? = null
    ): Result<SignupResponse> {
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
}

