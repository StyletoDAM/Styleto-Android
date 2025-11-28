package tn.esprit.labasniandroid.ui.screen.dressing

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.DetectionResult
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.repositories.DressingRepository
import tn.esprit.labasniandroid.models.repositories.SubscriptionRepository

class DressingViewModel(
    private val dressingRepository: DressingRepository = DressingRepository(),
    private val subscriptionRepository: SubscriptionRepository = SubscriptionRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _clothes = MutableStateFlow<List<Cloth>>(emptyList())
    val clothes: StateFlow<List<Cloth>> = _clothes.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<String>>(emptySet())
    val deletingIds: StateFlow<Set<String>> = _deletingIds.asStateFlow()

    // États pour la détection
    private val _isDetecting = MutableStateFlow(false)
    val isDetecting: StateFlow<Boolean> = _isDetecting.asStateFlow()

    private val _detectionResult = MutableStateFlow<Pair<DetectionResult, String>?>(null)
    val detectionResult: StateFlow<Pair<DetectionResult, String>?> = _detectionResult.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    // État pour afficher ViewPackages si quota dépassé
    private val _showUpgradeDialog = MutableStateFlow(false)
    val showUpgradeDialog: StateFlow<Boolean> = _showUpgradeDialog.asStateFlow()

    // SharedFlow pour notifier le refresh (comme NotificationCenter dans iOS)
    private val _refreshEvent = MutableStateFlow<Unit>(Unit)
    val refreshEvent: SharedFlow<Unit> = _refreshEvent.asSharedFlow()

    fun loadClothes(token: String) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            dressingRepository.fetchClothes(token).fold(
                onSuccess = { items -> _clothes.value = items.sortedByDescending { it.createdAt } },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _isLoading.value = false
        }
    }

    fun deleteCloth(token: String, clothId: String) {
        if (_deletingIds.value.contains(clothId)) return
        viewModelScope.launch {
            _deletingIds.value = _deletingIds.value + clothId
            _errorMessage.value = null

            dressingRepository.deleteCloth(token, clothId).fold(
                onSuccess = {
                    _clothes.value = _clothes.value.filterNot { it.id == clothId }
                    _successMessage.value = "Vêtement supprimé."
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _deletingIds.value = _deletingIds.value - clothId
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    /**
     * Détecte un vêtement à partir d'une image
     */
    fun detectCloth(bitmap: Bitmap) {
        if (_isDetecting.value) return
        
        if (bitmap.isRecycled) {
            _errorMessage.value = "L'image n'est plus disponible"
            return
        }
        
        viewModelScope.launch {
            try {
                _isDetecting.value = true
                _errorMessage.value = null
                _detectionResult.value = null

                dressingRepository.detectCloth(bitmap).fold(
                    onSuccess = { (result, imageUrl) ->
                        _detectionResult.value = Pair(result, imageUrl)
                    },
                    onFailure = { error ->
                        val message = when (error) {
                            is NetworkError.Transport -> 
                                "Erreur réseau: ${error.error.localizedMessage ?: error.error.message}"
                            is NetworkError.ServerMessage -> error.serverMessage
                            else -> error.message ?: "Erreur inconnue"
                        }
                        _errorMessage.value = message
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Erreur inattendue: ${e.localizedMessage ?: e.message}"
            } finally {
                _isDetecting.value = false
            }
        }
    }

    /**
     * Sauvegarde un vêtement détecté
     */
    fun saveDetectedCloth(
        token: String,
        imageURL: String,
        category: String,
        color: String,
        style: String,
        season: String
    ) {
        if (_isSaving.value) return
        
        // Validation des données avant envoi
        if (imageURL.isBlank()) {
            _errorMessage.value = "URL d'image manquante"
            return
        }
        
        if (category.isBlank()) {
            _errorMessage.value = "Catégorie manquante"
            return
        }
        
        viewModelScope.launch {
            try {
                _isSaving.value = true
                _errorMessage.value = null

                dressingRepository.addCloth(
                    token = token,
                    imageURL = imageURL,
                    category = category,
                    color = color,
                    style = style,
                    season = season
                ).fold(
                    onSuccess = { cloth ->
                        // Ajouter à la liste
                        _clothes.value = (listOf(cloth) + _clothes.value)
                            .sortedByDescending { it.createdAt }
                        
                        _successMessage.value = "Vêtement ajouté avec succès."
                        _detectionResult.value = null
                        
                        // Émettre l'événement de refresh (comme NotificationCenter dans iOS)
                        _refreshEvent.emit(Unit)
                    },
                    onFailure = { error ->
                        val message = when (error) {
                            is NetworkError.Transport -> 
                                "Erreur réseau: ${error.error.localizedMessage ?: error.error.message}"
                            is NetworkError.ServerMessage -> error.serverMessage
                            else -> error.message ?: "Erreur inconnue"
                        }
                        
                        // Vérifier si l'erreur est liée au quota
                        val errorMsg = message.lowercase()
                        if (errorMsg.contains("limite") || 
                            errorMsg.contains("quota") || 
                            errorMsg.contains("limit") || 
                            errorMsg.contains("exceeded") ||
                            errorMsg.contains("premium") ||
                            errorMsg.contains("403")) {
                            _showUpgradeDialog.value = true
                        } else {
                            _errorMessage.value = message
                        }
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Erreur inattendue: ${e.localizedMessage ?: e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    /**
     * Réinitialise le résultat de détection
     */
    fun clearDetectionResult() {
        _detectionResult.value = null
    }

    /**
     * Cache le dialog d'upgrade
     */
    fun hideUpgradeDialog() {
        _showUpgradeDialog.value = false
    }

    /**
     * Vérifie le quota de détection avant de permettre la détection
     */
    suspend fun checkDetectionQuota(token: String): Boolean {
        return try {
            subscriptionRepository.getMyStats(token).fold(
                onSuccess = { stats ->
                    val used = stats.clothesDetection.used
                    val limit = stats.clothesDetection.limit
                    
                    // Si limit est "unlimited" (String) ou Int.MAX_VALUE, toujours autorisé
                    val isUnlimited = limit == "unlimited" || (limit is Int && limit == Int.MAX_VALUE)
                    
                    if (isUnlimited) {
                        true
                    } else {
                        val limitInt = if (limit is Int) limit else 0
                        used < limitInt
                    }
                },
                onFailure = { 
                    // En cas d'erreur, autoriser quand même (peut être temporaire)
                    true
                }
            )
        } catch (e: Exception) {
            // En cas d'exception, autoriser quand même
            true
        }
    }
}
