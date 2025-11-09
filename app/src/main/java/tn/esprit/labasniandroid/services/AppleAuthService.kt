package tn.esprit.labasniandroid.services

import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.SigninResponse
import tn.esprit.labasniandroid.models.dto.AppleAuthRequest

class AppleAuthService {
    private val authApi = RetrofitClient.authApi

    suspend fun authenticate(
        appleId: String,
        fullName: String,
        email: String,
        profilePicture: String? = null,
        gender: String? = null
    ): Result<SigninResponse> {
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
}

