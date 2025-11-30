package tn.esprit.labasniandroid.ui.screen.store.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.data.local.entities.CartItem
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.models.repositories.ProfileRepository
import tn.esprit.labasniandroid.models.repositories.StoreRepository
import tn.esprit.labasniandroid.utils.CartManager
import android.util.Log

/**
 * PaymentViewModel Android (équivalent iOS PaymentViewModel)
 * Gère le paiement avec balance ou carte Stripe
 */
class PaymentViewModel(
    private val storeRepository: StoreRepository = StoreRepository(),
    private val profileRepository: ProfileRepository = ProfileRepository()
) : ViewModel() {
    
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _showSuccess = MutableStateFlow(false)
    val showSuccess: StateFlow<Boolean> = _showSuccess.asStateFlow()
    
    private val _userBalance = MutableStateFlow<Double>(0.0)
    val userBalance: StateFlow<Double> = _userBalance.asStateFlow()
    
    private val _useBalance = MutableStateFlow(false)
    val useBalance: StateFlow<Boolean> = _useBalance.asStateFlow()
    
    private val _clientSecret = MutableStateFlow<String?>(null)
    val clientSecret: StateFlow<String?> = _clientSecret.asStateFlow()
    
    /**
     * Vérifie si l'utilisateur peut payer avec son balance
     * Note: Cette vérification est côté client pour UX.
     * Le backend fait aussi une vérification finale avant de déduire le balance.
     */
    fun canPayWithBalance(totalPrice: Double): Boolean {
        val canPay = _userBalance.value >= totalPrice
        Log.d("PaymentViewModel", "💳 Can pay with balance? Balance: ${_userBalance.value} TND, Total: $totalPrice TND, CanPay: $canPay")
        return canPay
    }
    
    /**
     * Rafraîchit le balance de l'utilisateur (comme iOS refreshBalance)
     */
    fun refreshBalance(token: String) {
        viewModelScope.launch {
            try {
                Log.d("PaymentViewModel", "🔄 Refreshing balance...")
                val result = profileRepository.getProfile(token)
                result.onSuccess { user ->
                    _userBalance.value = user.balance ?: 0.0
                    Log.d("PaymentViewModel", "✅ Balance refreshed: ${_userBalance.value} TND")
                }.onFailure { error ->
                    Log.w("PaymentViewModel", "⚠️ Failed to refresh balance: ${error.message}")
                    // Garder l'ancienne valeur en cas d'erreur
                }
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "❌ Exception refreshing balance: ${e.message}", e)
            }
        }
    }
    
    /**
     * Démarre le processus de checkout (comme iOS startCheckout)
     */
    fun startCheckout(
        token: String,
        cartItems: List<CartItem>,
        totalPrice: Double
    ) {
        viewModelScope.launch {
            if (cartItems.isEmpty()) {
                _errorMessage.value = "Your cart is empty"
                return@launch
            }
            
            // Rafraîchir le balance avant de procéder
            refreshBalance(token)
            
            _isProcessing.value = true
            _errorMessage.value = null
            
            if (_useBalance.value) {
                purchaseWithBalance(token, cartItems)
            } else {
                initiateStripePayment(token, totalPrice)
            }
        }
    }
    
    /**
     * Achat avec balance (comme iOS purchaseWithBalance)
     * Le backend gère automatiquement :
     * 1. La déduction du balance de l'acheteur
     * 2. L'ajout du montant au balance du vendeur
     * 3. La vérification du solde avant déduction
     */
    private suspend fun purchaseWithBalance(
        token: String,
        cartItems: List<CartItem>
    ) {
        val totalPrice = cartItems.sumOf { it.price }
        
        // Vérification côté client avant d'appeler le backend
        if (!canPayWithBalance(totalPrice)) {
            _errorMessage.value = "Solde insuffisant. Veuillez recharger votre compte."
            _isProcessing.value = false
            return
        }
        
        Log.d("PaymentViewModel", "💰 Purchasing with balance - Total: $totalPrice TND")
        Log.d("PaymentViewModel", "💰 Current balance: ${_userBalance.value} TND")
        
        try {
            var allSuccess = true
            val purchasedItems = mutableListOf<tn.esprit.labasniandroid.models.entities.StoreItem>()
            
            // Acheter chaque article avec le balance
            // Le backend va :
            // 1. Vérifier le solde (peut changer entre temps)
            // 2. Déduire le montant du balance de l'acheteur
            // 3. Ajouter le montant au balance du vendeur
            var shouldStop = false
            for (item in cartItems) {
                if (shouldStop) break
                
                Log.d("PaymentViewModel", "🛒 Purchasing item: ${item.storeItemID} - Price: ${item.price} TND")
                
                val result = storeRepository.confirmPurchase(
                    token = token,
                    storeItemId = item.storeItemID,
                    paymentMethod = "balance"
                )
                
                result.onSuccess { storeItem ->
                    purchasedItems.add(storeItem)
                    Log.d("PaymentViewModel", "✅ Item purchased successfully: ${item.storeItemID}")
                }.onFailure { error ->
                    allSuccess = false
                    shouldStop = true
                    val errorMsg = when (error) {
                        is NetworkError.ServerMessage -> {
                            // Le backend envoie "Solde insuffisant" si le solde n'est pas suffisant
                            // Même si on a vérifié côté client, le solde peut avoir changé
                            if (error.serverMessage.contains("insuffisant", ignoreCase = true) ||
                                error.serverMessage.contains("insufficient", ignoreCase = true)) {
                                "Solde insuffisant. Veuillez recharger votre compte."
                            } else {
                                error.serverMessage
                            }
                        }
                        is NetworkError.Transport -> "Erreur de connexion: ${error.error.message ?: "Erreur réseau"}"
                        else -> "Erreur inconnue lors du paiement"
                    }
                    _errorMessage.value = errorMsg
                    Log.e("PaymentViewModel", "❌ Balance purchase error for item ${item.storeItemID}: $errorMsg")
                }
            }
            
            if (allSuccess) {
                // ✅ Le backend a déjà :
                // - Déduit le montant du balance de l'acheteur
                // - Ajouté le montant au balance du vendeur
                // On rafraîchit juste pour afficher le nouveau balance
                Log.d("PaymentViewModel", "✅ All items purchased successfully")
                Log.d("PaymentViewModel", "🔄 Refreshing balance to show updated amount...")
                
                refreshBalance(token)
                
                // Vider le panier (sera fait depuis la vue avec le context)
                _showSuccess.value = true
                Log.d("PaymentViewModel", "✅ Purchase completed with balance")
            } else {
                Log.e("PaymentViewModel", "❌ Some items failed to purchase")
            }
        } catch (e: Exception) {
            Log.e("PaymentViewModel", "❌ Exception in purchaseWithBalance: ${e.message}", e)
            _errorMessage.value = "Erreur lors du paiement: ${e.message ?: "Erreur inconnue"}"
        }
        
        _isProcessing.value = false
    }
    
    /**
     * Initie le paiement Stripe (comme iOS initiateStripePayment)
     */
    private suspend fun initiateStripePayment(
        token: String,
        totalAmount: Double
    ) {
        try {
            Log.d("PaymentViewModel", "💳 Initiating Stripe payment for $totalAmount TND")
            
            val result = storeRepository.createPaymentIntent(token, totalAmount, "usd")
            result.onSuccess { secret ->
                _clientSecret.value = secret
                Log.d("PaymentViewModel", "✅ Payment Sheet ready")
            }.onFailure { error ->
                val errorMsg = when (error) {
                    is NetworkError.ServerMessage -> error.serverMessage
                    is NetworkError.Transport -> "Erreur de connexion"
                    else -> "Payment initialization failed"
                }
                _errorMessage.value = errorMsg
                Log.e("PaymentViewModel", "❌ Stripe init error: $errorMsg")
            }
        } catch (e: Exception) {
            Log.e("PaymentViewModel", "❌ Exception in initiateStripePayment: ${e.message}", e)
            _errorMessage.value = "Payment initialization failed"
        }
        
        _isProcessing.value = false
    }
    
    /**
     * Confirme les commandes après paiement Stripe réussi (comme iOS confirmStripeOrders)
     */
    fun confirmStripeOrders(
        token: String,
        cartItems: List<CartItem>
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            
            try {
                val paymentIntentId = _clientSecret.value?.let { secret ->
                    // Extraire le paymentIntentId du clientSecret (format: pi_xxx_secret_yyy)
                    secret.substringBefore("_secret_").substringAfter("pi_").let { id ->
                        "pi_$id"
                    }
                }
                
                if (paymentIntentId == null) {
                    _errorMessage.value = "Payment intent ID not found"
                    _isProcessing.value = false
                    return@launch
                }
                
                var allSuccess = true
                val purchasedItems = mutableListOf<tn.esprit.labasniandroid.models.entities.StoreItem>()
                
                for (item in cartItems) {
                    val result = storeRepository.confirmPurchase(
                        token = token,
                        storeItemId = item.storeItemID,
                        paymentMethod = "card",
                        paymentIntentId = paymentIntentId
                    )
                    
                    result.onSuccess { storeItem ->
                        purchasedItems.add(storeItem)
                    }.onFailure { error ->
                        allSuccess = false
                        val errorMsg = when (error) {
                            is NetworkError.ServerMessage -> error.serverMessage
                            is NetworkError.Transport -> "Erreur de connexion"
                            else -> "Erreur inconnue"
                        }
                        _errorMessage.value = errorMsg
                        Log.e("PaymentViewModel", "❌ Order confirmation error: $errorMsg")
                    }
                }
                
                if (allSuccess) {
                    // Rafraîchir le balance (même si payé par carte, pour sync)
                    refreshBalance(token)
                    
                    // Vider le panier (sera fait depuis la vue)
                    _showSuccess.value = true
                    Log.d("PaymentViewModel", "✅ Orders confirmed")
                } else {
                    _errorMessage.value = "Payment succeeded but confirmation failed"
                }
            } catch (e: Exception) {
                Log.e("PaymentViewModel", "❌ Exception in confirmStripeOrders: ${e.message}", e)
                _errorMessage.value = "Payment succeeded but confirmation failed"
            }
            
            _isProcessing.value = false
        }
    }
    
    /**
     * Réinitialise après succès (comme iOS resetAfterSuccess)
     */
    fun resetAfterSuccess() {
        _showSuccess.value = false
        _clientSecret.value = null
        _errorMessage.value = null
    }
    
    /**
     * Change la méthode de paiement
     */
    fun setUseBalance(useBalance: Boolean) {
        _useBalance.value = useBalance
    }
    
    /**
     * Efface les messages d'erreur
     */
    fun clearMessages() {
        _errorMessage.value = null
    }
}

