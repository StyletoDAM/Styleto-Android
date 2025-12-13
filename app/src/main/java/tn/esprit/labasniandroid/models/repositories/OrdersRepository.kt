package tn.esprit.labasniandroid.models.repositories

import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.models.NetworkError

class OrdersRepository(
    private val ordersApi: tn.esprit.labasniandroid.api.OrdersApi = RetrofitClient.ordersApi
) {
    
    suspend fun getMyOrders(token: String): Result<List<tn.esprit.labasniandroid.api.OrderResponse>> {
        return try {
            val response = ordersApi.getMyOrders("Bearer $token")
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer les commandes."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
    
    suspend fun createOrder(
        token: String,
        clothesId: String,
        price: Double
    ): Result<tn.esprit.labasniandroid.api.OrderResponse> {
        return try {
            val request = tn.esprit.labasniandroid.api.CreateOrderRequest(
                clothesId = clothesId,
                price = price
            )
            val response = ordersApi.createOrder("Bearer $token", request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "Données invalides."
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de créer la commande."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
    
    // MARK: - Get Unified History (exactement comme iOS)
    suspend fun getUnifiedHistory(token: String): Result<List<tn.esprit.labasniandroid.api.HistoryItemResponse>> {
        return try {
            val response = ordersApi.getUnifiedHistory("Bearer $token")
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer l'historique."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }
}

