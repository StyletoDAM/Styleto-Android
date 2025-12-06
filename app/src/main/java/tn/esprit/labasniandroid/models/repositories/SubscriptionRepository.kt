package tn.esprit.labasniandroid.models.repositories

import android.util.Log
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.UpgradePlanResponse
import tn.esprit.labasniandroid.api.CreateCheckoutRequest
import tn.esprit.labasniandroid.api.CheckoutSessionResponse
import tn.esprit.labasniandroid.api.VerifySessionResponse
import tn.esprit.labasniandroid.models.NetworkError

class SubscriptionRepository(
    private val subscriptionApi: tn.esprit.labasniandroid.api.SubscriptionApi = RetrofitClient.subscriptionApi
) {

    /**
     * Récupère l'abonnement actuel de l'utilisateur
     */
    /**
     * Récupère l'abonnement actuel de l'utilisateur
     */
    suspend fun getMySubscription(token: String): Result<tn.esprit.labasniandroid.api.SubscriptionResponse> {
        return try {
            // ✅ LOG 1 : Token utilisé
            Log.d("SubscriptionRepo", "🔑 Token: ${token.take(20)}...")

            // ✅ LOG 2 : URL appelée
            Log.d("SubscriptionRepo", "🌐 Calling: GET /subscriptions/me")

            val response = subscriptionApi.getMySubscription("Bearer $token")

            // ✅ LOG 3 : Code de réponse
            Log.d("SubscriptionRepo", "📡 Response code: ${response.code()}")

            // ✅ LOG 4 : Body de la réponse (même en cas d'erreur)
            val errorBody = response.errorBody()?.string()
            Log.d("SubscriptionRepo", "📦 Response body: ${response.body()}")
            Log.d("SubscriptionRepo", "❌ Error body: $errorBody")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Abonnement non trouvé. (userId manquant ou abonnement non créé)"
                    else -> errorBody ?: "Impossible de récupérer l'abonnement."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            // ✅ LOG 5 : Exception
            Log.e("SubscriptionRepo", "💥 Exception: ${exception.message}", exception)
            Result.failure(NetworkError.Transport(exception))
        }
    }

    /**
     * Récupère les statistiques d'usage de l'utilisateur
     */
    suspend fun getMyStats(token: String): Result<tn.esprit.labasniandroid.api.UsageStatsResponse> {
        return try {
            val response = subscriptionApi.getMyStats("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer les statistiques."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    /**
     * ✨ NOUVEAU: Crée une Stripe Checkout Session et retourne l'URL
     * @param token Token d'authentification
     * @param plan Plan d'abonnement ("PREMIUM" ou "PRO_SELLER")
     * @param interval Intervalle de paiement ("month" ou "year")
     * @return URL de la page de paiement Stripe
     */
    suspend fun createCheckoutSession(
        token: String,
        plan: String,
        interval: String = "month"
    ): Result<CheckoutSessionResponse> {
        return try {
            val request = CreateCheckoutRequest(
                plan = plan,
                interval = interval
            )
            val response = subscriptionApi.createCheckoutSession("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Plan invalide."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de créer la session de paiement."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }


    /**
     * ✨ NOUVEAU: Vérifie le statut d'une session après retour de Stripe
     * @param token Token d'authentification
     * @param sessionId ID de la session Stripe
     */
    suspend fun verifyCheckoutSession(
        token: String,
        sessionId: String
    ): Result<VerifySessionResponse> {
        return try {
            val response = subscriptionApi.verifySession("Bearer $token", sessionId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Session invalide."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de vérifier la session."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    /**
     * Vérifie si l'utilisateur peut vendre des articles (quota)
     */
    suspend fun checkStoreSellingQuota(token: String): Result<tn.esprit.labasniandroid.api.QuotaCheckResponse> {
        return try {
            val response = subscriptionApi.checkStoreSellingQuota("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de vérifier le quota."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    /**
     * Met à jour l'abonnement après un paiement réussi
     * (Normalement géré automatiquement par les webhooks Stripe,
     * mais garde ce endpoint pour les cas edge)
     */
    suspend fun upgradeSubscription(
        token: String,
        plan: String
    ): Result<UpgradePlanResponse> {
        return try {
            val normalizedPlan = plan.trim().uppercase().replace("-", "_")
            val request = tn.esprit.labasniandroid.api.UpdateSubscriptionRequest(plan = normalizedPlan)
            val response = subscriptionApi.updateSubscription("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Plan invalide."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de mettre à jour l'abonnement."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
    /**
     * Annule l'abonnement actuel de l'utilisateur
     */
    suspend fun cancelSubscription(token: String): Result<tn.esprit.labasniandroid.api.CancelSubscriptionResponse> {
        return try {
            val response = subscriptionApi.cancelSubscription("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Aucun abonnement actif à annuler."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Abonnement non trouvé."
                    else -> errorBody ?: "Impossible d'annuler l'abonnement."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
}