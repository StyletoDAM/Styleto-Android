package tn.esprit.labasniandroid.services

import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.VerifyEmailResponse
import tn.esprit.labasniandroid.models.dto.VerifyEmailRequest

class VerifyEmailService {
    private val authApi = RetrofitClient.authApi

    suspend fun verify(tempToken: String, code: String): Result<VerifyEmailResponse> {
        return try {
            val request = VerifyEmailRequest(
                tempToken = tempToken,
                code = code.trim()
            )
            val response = authApi.verifyEmail(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    401 -> "Code invalide ou expiré."
                    409 -> "Compte déjà créé."
                    else -> errorBody ?: "Une erreur est survenue."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }
}

