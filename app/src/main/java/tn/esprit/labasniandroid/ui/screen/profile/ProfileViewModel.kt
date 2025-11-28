package tn.esprit.labasniandroid.ui.screen.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.models.repositories.ProfileRepository
import tn.esprit.labasniandroid.models.repositories.StoreRepository
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.utils.TokenManager

class ProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository(),
    private val storeRepository: StoreRepository = StoreRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _accountDeleted = MutableStateFlow(false)
    val accountDeleted: StateFlow<Boolean> = _accountDeleted.asStateFlow()

    private val _isPhotoUpdating = MutableStateFlow(false)
    val isPhotoUpdating: StateFlow<Boolean> = _isPhotoUpdating.asStateFlow()

    // Stripe Payment states
    private val _clientSecret = MutableStateFlow<String?>(null)
    val clientSecret: StateFlow<String?> = _clientSecret.asStateFlow()

    private val _pendingTopUpAmount = MutableStateFlow<Double?>(null)
    val pendingTopUpAmount: StateFlow<Double?> = _pendingTopUpAmount.asStateFlow()

    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

    fun loadProfile(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            profileRepository.getProfile(token).fold(
                onSuccess = { user ->
                    _user.value = user
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isLoading.value = false
        }
    }

    fun updateProfile(
        token: String,
        fullName: String? = null,
        email: String? = null,
        gender: String? = null,
        phoneNumber: String? = null,
        preferences: List<String>? = null,
        password: String? = null
    ) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "=== START UPDATE PROFILE ===")
                Log.d("ProfileViewModel", "fullName: $fullName, email: $email, gender: $gender")
                
                _isLoading.value = true
                _errorMessage.value = null
                _successMessage.value = null

                val result = profileRepository.updateProfile(
                    token = token,
                    fullName = fullName,
                    email = email,
                    gender = gender,
                    phoneNumber = phoneNumber,
                    preferences = preferences,
                    password = password
                )
                
                result.fold(
                    onSuccess = { updatedUser ->
                        Log.d("ProfileViewModel", "Update SUCCESS - New fullName: ${updatedUser.fullName}")
                        _user.value = updatedUser
                        _successMessage.value = "Profil mis à jour avec succès !"
                        Log.d("ProfileViewModel", "Success message set: ${_successMessage.value}")
                    },
                    onFailure = { error ->
                        Log.e("ProfileViewModel", "Update FAILED: ${error.message}")
                        _errorMessage.value = error.message
                    }
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception in updateProfile: ${e.message}", e)
                _errorMessage.value = "Erreur inattendue: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("ProfileViewModel", "=== END UPDATE PROFILE ===")
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun acknowledgeAccountDeleted() {
        _accountDeleted.value = false
    }

    fun setInitialUser(initialUser: User?) {
        if (initialUser != null) {
            _user.value = initialUser
        }
    }

    fun deleteAccount(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            profileRepository.deleteAccount(token).fold(
                onSuccess = { response ->
                    _user.value = null
                    _accountDeleted.value = true
                    _successMessage.value = response.message
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isLoading.value = false
        }
    }

    fun uploadProfilePhoto(token: String, imageData: ByteArray) {
        viewModelScope.launch {
            _isPhotoUpdating.value = true
            _errorMessage.value = null
            _successMessage.value = null

            profileRepository.uploadProfilePhoto(token, imageData).fold(
                onSuccess = { updatedUser ->
                    _user.value = updatedUser
                    _successMessage.value = "Photo de profil mise à jour avec succès !"
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isPhotoUpdating.value = false
        }
    }

    fun setProfilePictureFromUrl(token: String, imageUrl: String) {
        viewModelScope.launch {
            _isPhotoUpdating.value = true
            _errorMessage.value = null
            _successMessage.value = null

            profileRepository.updateProfile(
                token = token,
                profilePictureUrl = imageUrl
            ).fold(
                onSuccess = { updatedUser ->
                    _user.value = updatedUser
                    _successMessage.value = "Photo de profil mise à jour !"
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isPhotoUpdating.value = false
        }
    }

    fun topUpBalance(token: String, amount: Double) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "=== START TOP UP BALANCE ===")
                Log.d("ProfileViewModel", "Amount: $amount TND")
                
                _isLoading.value = true
                _errorMessage.value = null
                _successMessage.value = null

                val result = profileRepository.topUpBalance(token = token, amount = amount)
                
                result.fold(
                    onSuccess = { updatedUser ->
                        Log.d("ProfileViewModel", "Top up SUCCESS - New balance: ${updatedUser.balance}")
                        _user.value = updatedUser
                        _successMessage.value = "Solde rechargé avec succès ! +${String.format("%.2f", amount)} TND"
                        Log.d("ProfileViewModel", "Success message set: ${_successMessage.value}")
                    },
                    onFailure = { error ->
                        Log.e("ProfileViewModel", "Top up FAILED: ${error.message}")
                        _errorMessage.value = "Échec de la recharge. Veuillez réessayer."
                    }
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception in topUpBalance: ${e.message}", e)
                _errorMessage.value = "Erreur inattendue: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("ProfileViewModel", "=== END TOP UP BALANCE ===")
            }
        }
    }

    // MARK: - Stripe Payment for Top-up

    /**
     * Initie le processus de top-up avec Stripe
     * 1. Crée un PaymentIntent via StoreRepository
     * 2. Retourne le clientSecret pour présenter le PaymentSheet
     */
    suspend fun initiateTopUp(token: String, amount: Double): Result<String> {
        return try {
            Log.d("ProfileViewModel", "💰 Initiating top-up for $amount TND")
            
            _isProcessingPayment.value = true
            _errorMessage.value = null
            _pendingTopUpAmount.value = amount

            // Créer le PaymentIntent via StoreRepository (utilise l'API /store/payment-intent)
            val result = storeRepository.createPaymentIntent(
                token = token,
                amount = amount,
                currency = "usd" // ou "tnd" si supporté
            )

            result.fold(
                onSuccess = { clientSecret ->
                    Log.d("ProfileViewModel", "✅ Payment Intent created successfully")
                    _clientSecret.value = clientSecret
                    _isProcessingPayment.value = false
                    Result.success(clientSecret)
                },
                onFailure = { error ->
                    Log.e("ProfileViewModel", "❌ Failed to create Payment Intent: ${error.message}")
                    _errorMessage.value = when (error) {
                        is tn.esprit.labasniandroid.models.NetworkError.ServerMessage -> error.serverMessage
                        is tn.esprit.labasniandroid.models.NetworkError.Transport -> "Erreur de connexion: ${error.error.message ?: "Erreur réseau"}"
                        else -> "Impossible d'initialiser le paiement"
                    }
                    _isProcessingPayment.value = false
                    _pendingTopUpAmount.value = null
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Exception in initiateTopUp: ${e.message}", e)
            _errorMessage.value = "Erreur: ${e.message}"
            _isProcessingPayment.value = false
            _pendingTopUpAmount.value = null
            Result.failure(e)
        }
    }

    /**
     * Confirme le top-up après un paiement Stripe réussi
     * Appelle l'API /auth/balance/topup pour mettre à jour le solde
     */
    suspend fun confirmTopUpWithBackend(token: String) {
        val amount = _pendingTopUpAmount.value
        if (amount == null) {
            _errorMessage.value = "Montant de recharge manquant"
            _isProcessingPayment.value = false
            return
        }

        try {
            Log.d("ProfileViewModel", "✅ Payment completed! Confirming top-up with backend...")
            
            _isProcessingPayment.value = true
            _errorMessage.value = null

            // Appeler l'API backend pour mettre à jour le solde
            val result = profileRepository.topUpBalance(token = token, amount = amount)

            result.fold(
                onSuccess = { updatedUser ->
                    Log.d("ProfileViewModel", "✅ Top-up confirmed! New balance: ${updatedUser.balance}")
                    _user.value = updatedUser
                    _successMessage.value = "Solde rechargé avec succès ! +${String.format("%.2f", amount)} TND"
                    
                    // Cleanup
                    _isProcessingPayment.value = false
                    _pendingTopUpAmount.value = null
                    _clientSecret.value = null
                },
                onFailure = { error ->
                    Log.e("ProfileViewModel", "❌ Backend confirmation failed: ${error.message}")
                    _errorMessage.value = "Paiement réussi mais confirmation échouée. Veuillez contacter le support."
                    _isProcessingPayment.value = false
                }
            )
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Exception in confirmTopUpWithBackend: ${e.message}", e)
            _errorMessage.value = "Erreur lors de la confirmation: ${e.message}"
            _isProcessingPayment.value = false
        }
    }

    /**
     * Réinitialise l'état du paiement (appelé quand le paiement est annulé ou échoue)
     */
    fun resetPaymentState() {
        _clientSecret.value = null
        _pendingTopUpAmount.value = null
        _isProcessingPayment.value = false
    }
}

