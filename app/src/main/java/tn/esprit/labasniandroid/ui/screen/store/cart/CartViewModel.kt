package tn.esprit.labasniandroid.ui.screen.store.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.repositories.StoreRepository
import tn.esprit.labasniandroid.models.repositories.OrdersRepository
import tn.esprit.labasniandroid.utils.CartManager
import tn.esprit.labasniandroid.data.local.entities.CartItem

class CartViewModel(
    private val storeRepository: StoreRepository = StoreRepository(),
    private val ordersRepository: OrdersRepository = OrdersRepository()
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _paymentSuccess = MutableStateFlow<Boolean>(false)
    val paymentSuccess: StateFlow<Boolean> = _paymentSuccess.asStateFlow()

    private val _clientSecret = MutableStateFlow<String?>(null)
    val clientSecret: StateFlow<String?> = _clientSecret.asStateFlow()

    fun initialize(token: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Le panier est géré par CartManager
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors du chargement du panier: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun createPaymentIntent(token: String, totalAmount: Double): Result<String> {
        _isLoading.value = true
        return try {
            val result = storeRepository.createPaymentIntent(token, totalAmount)
            result.onSuccess { clientSecret ->
                _clientSecret.value = clientSecret
            }.onFailure { error ->
                _errorMessage.value = when (error) {
                    is tn.esprit.labasniandroid.models.NetworkError.ServerMessage -> error.serverMessage
                    is tn.esprit.labasniandroid.models.NetworkError.Transport -> "Erreur de connexion: ${error.error.message ?: "Erreur réseau"}"
                    else -> "Erreur inconnue"
                }
            }
            result
        } catch (e: Exception) {
            _errorMessage.value = "Erreur: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun confirmPurchase(
        token: String,
        cartItems: List<CartItem>
    ): Result<Unit> {
        _isLoading.value = true
        return try {
            var allSuccess = true
            var lastError: Exception? = null
            val purchasedItems = mutableListOf<tn.esprit.labasniandroid.models.entities.StoreItem>()

            // Confirmer l'achat pour chaque article du panier
            for (item in cartItems) {
                val clientSecret = _clientSecret.value
                if (clientSecret != null) {
                    // Extraire le paymentIntentId du clientSecret (format: pi_xxx_secret_yyy)
                    val paymentIntentId = clientSecret.substringBefore("_secret_").substringAfter("pi_")
                    val fullPaymentIntentId = "pi_$paymentIntentId"

                    val result = storeRepository.confirmPurchase(
                        token = token,
                        storeItemId = item.storeItemID,
                        paymentMethod = "card",
                        paymentIntentId = fullPaymentIntentId
                    )

                    result.onSuccess { storeItem ->
                        // Stocker l'article acheté pour créer la commande après
                        purchasedItems.add(storeItem)
                    }.onFailure { error ->
                        allSuccess = false
                        lastError = when (error) {
                            is tn.esprit.labasniandroid.models.NetworkError.ServerMessage -> 
                                Exception(error.serverMessage)
                            is tn.esprit.labasniandroid.models.NetworkError.Transport -> 
                                error.error as? Exception ?: Exception(error.error.message)
                            else -> Exception("Erreur inconnue")
                        }
                    }
                }
            }

            if (allSuccess) {
                // Créer une commande dans l'historique pour chaque article acheté
                for (storeItem in purchasedItems) {
                    val clothId = storeItem.cloth?.id
                    if (clothId != null && storeItem.price > 0) {
                        // Créer la commande dans l'historique
                        ordersRepository.createOrder(
                            token = token,
                            clothesId = clothId,
                            price = storeItem.price
                        ).onFailure { error ->
                            // Log l'erreur mais ne bloque pas le processus
                            android.util.Log.e(
                                "CartViewModel",
                                "Erreur lors de la création de la commande pour $clothId: ${error.message}"
                            )
                        }
                    }
                }
                
                // Vider le panier après paiement réussi
                // Note: clearCart nécessite un context, sera appelé depuis la vue
                _paymentSuccess.value = true
                return Result.success(Unit)
            } else {
                _errorMessage.value = lastError?.message ?: "Erreur lors de la confirmation du paiement"
                Result.failure(lastError ?: Exception("Erreur inconnue"))
            }
        } catch (e: Exception) {
            _errorMessage.value = "Erreur: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _paymentSuccess.value = false
    }

    fun resetPaymentState() {
        _clientSecret.value = null
        _paymentSuccess.value = false
    }
}

