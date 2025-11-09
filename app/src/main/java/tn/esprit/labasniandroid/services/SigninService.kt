package tn.esprit.labasniandroid.services

import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.SigninResponse
import tn.esprit.labasniandroid.models.dto.SigninRequest

class SigninService {
    private val authApi = RetrofitClient.authApi

    suspend fun signin(email: String, password: String): Result<SigninResponse> {
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
}

