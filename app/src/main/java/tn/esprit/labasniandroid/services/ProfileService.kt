package tn.esprit.labasniandroid.services

import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.User

class ProfileService {
    private val authApi = RetrofitClient.authApi

    suspend fun getProfile(token: String): Result<User> {
        return try {
            val response = authApi.getProfile("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer le profil."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }
}

