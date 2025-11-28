package tn.esprit.labasniandroid.models.repositories

import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.UpgradePlanResponse
import tn.esprit.labasniandroid.models.NetworkError

class SubscriptionRepository(
    private val subscriptionApi: tn.esprit.labasniandroid.api.SubscriptionApi = RetrofitClient.subscriptionApi,
    private val storeApi: tn.esprit.labasniandroid.api.StoreApi = RetrofitClient.storeApi
) {
    
    /**
     * Récupère l'abonnement actuel de l'utilisateur
     */
    suspend fun getMySubscription(token: String): Result<tn.esprit.labasniandroid.api.SubscriptionResponse> {
        return try {
            val response = subscriptionApi.getMySubscription("Bearer $token")
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Abonnement non trouvé."
                    else -> errorBody ?: "Impossible de récupérer l'abonnement."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
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
     * Crée un PaymentIntent pour un abonnement via l'API Store
     * (Réutilise l'endpoint /store/payment-intent car il accepte n'importe quel montant)
     */
    suspend fun createSubscriptionPaymentIntent(
        token: String,
        amount: Double,
        currency: String = "usd"
    ): Result<String> {
        return try {
            val request = tn.esprit.labasniandroid.api.CreatePaymentIntentRequest(
                amount = amount,
                currency = currency
            )
            val response = storeApi.createPaymentIntent("Bearer $token", request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.clientSecret)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Montant invalide."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de créer le paiement."
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
     * Utilise le nouvel endpoint PATCH /subscriptions/me
     */
    suspend fun upgradeSubscription(
        token: String,
        plan: String // "PREMIUM" ou "PRO_SELLER"
    ): Result<UpgradePlanResponse> {
        return try {
            // Normaliser le plan : enlever les espaces et s'assurer du bon format
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
}

