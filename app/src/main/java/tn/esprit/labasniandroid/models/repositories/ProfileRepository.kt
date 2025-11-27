package tn.esprit.labasniandroid.models.repositories

import android.util.Log
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.TopUpBalanceRequest
import tn.esprit.labasniandroid.api.UpdateProfileRequest
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.Responses
import tn.esprit.labasniandroid.models.entities.User
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileRepository {
    private val authApi = RetrofitClient.authApi

    suspend fun getProfile(token: String): Result<User> {
        return try {
            val response = authApi.getProfile("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    401 -> "Token invalide ou expiré."
                    404 -> "Profil non trouvé."
                    else -> errorBody ?: "Une erreur est survenue."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun updateProfile(
        token: String,
        fullName: String? = null,
        email: String? = null,
        gender: String? = null,
        phoneNumber: String? = null,
        preferences: List<String>? = null,
        password: String? = null,
        profilePictureUrl: String? = null
    ): Result<User> {
        return try {
            // Construire la requête - ne pas inclure les champs null
            val request = UpdateProfileRequest(
                fullName = fullName?.takeIf { it.isNotBlank() },
                email = email?.takeIf { it.isNotBlank() },
                gender = gender?.takeIf { it.isNotBlank() },
                phoneNumber = phoneNumber?.takeIf { it.isNotBlank() },
                preferences = preferences?.takeIf { it.isNotEmpty() },
                password = password?.takeIf { it.isNotBlank() },
                // IMPORTANT: ne pas filtrer la chaîne vide pour permettre la suppression côté backend
                profilePicture = profilePictureUrl
            )
            // Log pour débogage détaillé
            Log.d("ProfileRepository", "=== UPDATE PROFILE REQUEST ===")
            Log.d("ProfileRepository", "fullName: $fullName (filtered: ${request.fullName})")
            Log.d("ProfileRepository", "email: $email (filtered: ${request.email})")
            Log.d("ProfileRepository", "gender: $gender (filtered: ${request.gender})")
            Log.d("ProfileRepository", "phoneNumber: $phoneNumber (filtered: ${request.phoneNumber})")
            Log.d("ProfileRepository", "preferences: $preferences (filtered: ${request.preferences})")
            Log.d("ProfileRepository", "profilePicture: $profilePictureUrl (filtered: ${request.profilePicture})")
            Log.d("ProfileRepository", "Request object: $request")
            
            val response = authApi.updateProfile("Bearer $token", request)
            
            Log.d("ProfileRepository", "Response code: ${response.code()}")
            Log.d("ProfileRepository", "Response isSuccessful: ${response.isSuccessful}")
            Log.d("ProfileRepository", "Response headers: ${response.headers()}")

            if (response.isSuccessful) {
                val updatedUser = response.body()
                if (updatedUser != null) {
                    Log.d("ProfileRepository", "✅ Profile updated successfully!")
                    Log.d("ProfileRepository", "New fullName: ${updatedUser.fullName}")
                    Log.d("ProfileRepository", "New email: ${updatedUser.email}")
                    Log.d("ProfileRepository", "New gender: ${updatedUser.gender}")
                    Log.d("ProfileRepository", "New profilePicture: ${updatedUser.profilePicture}")
                    Result.success(updatedUser)
                } else {
                    Log.e("ProfileRepository", "❌ Response body is null!")
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProfileRepository", "Error body: $errorBody")
                    Result.failure(NetworkError.ServerMessage("Réponse vide du serveur."))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ProfileRepository", "❌ Update failed!")
                Log.e("ProfileRepository", "Status code: ${response.code()}")
                Log.e("ProfileRepository", "Error body: $errorBody")
                val errorMessage = when (response.code()) {
                    401 -> "Token invalide ou expiré."
                    409 -> "Email déjà utilisé."
                    400 -> "Données invalides: $errorBody"
                    else -> errorBody ?: "Une erreur est survenue lors de la mise à jour (code: ${response.code()})."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun uploadProfilePhoto(
        token: String,
        imageData: ByteArray,
        fileName: String = "profile.jpg"
    ): Result<User> {
        return try {
            val mediaType = "image/jpeg".toMediaType()
            val requestBody = imageData.toRequestBody(mediaType)
            val multipartBody = MultipartBody.Part.createFormData(
                name = "image",
                filename = fileName,
                body = requestBody
            )

            val response = authApi.updateProfilePhoto("Bearer $token", multipartBody)

            if (response.isSuccessful) {
                val updatedUser = response.body()
                if (updatedUser != null) {
                    Result.success(updatedUser)
                } else {
                    Result.failure(NetworkError.ServerMessage("Réponse vide du serveur."))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    401 -> "Token invalide ou expiré."
                    413 -> "Image trop lourde."
                    else -> errorBody ?: "Une erreur est survenue lors de l'upload (code: ${response.code()})."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun deleteAccount(token: String): Result<Responses.MessageResponse> {
        return try {
            val response = authApi.deleteProfile("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    401 -> "Token invalide ou expiré."
                    else -> errorBody ?: "Une erreur est survenue lors de la suppression."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(NetworkError.Transport(e))
        }
    }

    suspend fun topUpBalance(token: String, amount: Double): Result<User> {
        return try {
            // Convertir le montant en centimes (comme dans iOS)
            val amountInCents = (amount * 100).toInt()
            val request = TopUpBalanceRequest(amount = amountInCents)
            
            Log.d("ProfileRepository", "=== TOP UP BALANCE REQUEST ===")
            Log.d("ProfileRepository", "Amount TND: $amount")
            Log.d("ProfileRepository", "Amount in cents: $amountInCents")
            
            val response = authApi.topUpBalance("Bearer $token", request)
            
            Log.d("ProfileRepository", "Response code: ${response.code()}")
            Log.d("ProfileRepository", "Response isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val topUpResponse = response.body()
                if (topUpResponse != null) {
                    Log.d("ProfileRepository", "✅ Balance topped up successfully!")
                    Log.d("ProfileRepository", "Message: ${topUpResponse.message}")
                    Log.d("ProfileRepository", "New balance: ${topUpResponse.newBalance}")
                    Log.d("ProfileRepository", "Updated user balance: ${topUpResponse.user.balance}")
                    Result.success(topUpResponse.user)
                } else {
                    Log.e("ProfileRepository", "❌ Response body is null!")
                    Result.failure(NetworkError.ServerMessage("Réponse vide du serveur."))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ProfileRepository", "❌ Top up failed!")
                Log.e("ProfileRepository", "Status code: ${response.code()}")
                Log.e("ProfileRepository", "Error body: $errorBody")
                val errorMessage = when (response.code()) {
                    401 -> "Token invalide ou expiré."
                    400 -> "Montant invalide."
                    else -> errorBody ?: "Une erreur est survenue lors de la recharge (code: ${response.code()})."
                }
                Result.failure(NetworkError.ServerMessage(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Exception in topUpBalance: ${e.message}", e)
            Result.failure(NetworkError.Transport(e))
        }
    }
}

