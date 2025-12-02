package tn.esprit.labasniandroid.ui.screen.tenues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.Outfit
import tn.esprit.labasniandroid.models.repositories.DressingRepository
import tn.esprit.labasniandroid.models.repositories.TenuesRepository
import tn.esprit.labasniandroid.models.repositories.RecommendationsRepository
import tn.esprit.labasniandroid.api.AIRecommendationResponse
import kotlinx.coroutines.flow.update

class TenuesViewModel(
    private val tenuesRepository: TenuesRepository = TenuesRepository(),
    private val dressingRepository: DressingRepository = DressingRepository(),
    private val recommendationsRepository: RecommendationsRepository = RecommendationsRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _availableClothes = MutableStateFlow<List<Cloth>>(emptyList())
    val availableClothes: StateFlow<List<Cloth>> = _availableClothes.asStateFlow()

    private val _isLoadingClothes = MutableStateFlow(false)
    val isLoadingClothes: StateFlow<Boolean> = _isLoadingClothes.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<String>>(emptySet())
    val deletingIds: StateFlow<Set<String>> = _deletingIds.asStateFlow()

    // État pour la recommandation AI (comme iOS)
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _aiSuggestion = MutableStateFlow<AIRecommendationResponse?>(null)
    val aiSuggestion: StateFlow<AIRecommendationResponse?> = _aiSuggestion.asStateFlow()

    private val _isAccepting = MutableStateFlow(false)
    val isAccepting: StateFlow<Boolean> = _isAccepting.asStateFlow()

    private var initialized = false
    private var cachedToken: String? = null
    private var cachedUserId: String? = null
    
    // Stocker les paramètres de la dernière suggestion pour régénération automatique après rejet
    private var lastSuggestionStyle: String? = null
    private var lastSuggestionCity: String? = null
    private var lastSuggestionTemperature: Double? = null

    fun initialize(token: String, userId: String) {
        if (initialized && cachedToken == token && cachedUserId == userId) return
        initialized = true
        cachedToken = token
        cachedUserId = userId
        loadData(token)
        loadAvailableClothes(token)
    }

    fun refresh(token: String) {
        cachedToken = token
        loadData(token)
    }

    fun loadAvailableClothes(token: String) {
        if (_isLoadingClothes.value) return
        if (_availableClothes.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoadingClothes.value = true
            dressingRepository.fetchClothes(token).fold(
                onSuccess = { clothes ->
                    _availableClothes.value = clothes
                    reconcileOutfitsAndDressing()
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )
            _isLoadingClothes.value = false
        }
    }

    fun addOutfit(token: String, selectedClothes: List<Cloth>, eventType: String?) {
        val userId = cachedUserId ?: return
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            tenuesRepository.createOutfit(
                token = token,
                userId = userId,
                clothesIds = selectedClothes.map { it.id },
                eventType = eventType
            ).fold(
                onSuccess = {
                    _successMessage.value = "Tenue créée avec succès !"
                    loadData(token)
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _isSubmitting.value = false
        }
    }

    fun deleteOutfit(token: String, outfitId: String) {
        if (_deletingIds.value.contains(outfitId)) return
        viewModelScope.launch {
            _deletingIds.value = _deletingIds.value + outfitId

            tenuesRepository.deleteOutfit(token, outfitId).fold(
                onSuccess = {
                    _outfits.value = _outfits.value.filterNot { it.id == outfitId }
                    _successMessage.value = "Tenue supprimée."
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _deletingIds.value = _deletingIds.value - outfitId
        }
    }

    fun toggleFavorite(outfitId: String) {
        _outfits.value = _outfits.value.map { outfit ->
            if (outfit.id == outfitId) outfit.copy(isFavorite = !outfit.isFavorite) else outfit
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // MARK: - AI Recommendation Functions (comme iOS)
    
    /**
     * Génère une suggestion AI (ne crée PAS l'outfit)
     * Comme iOS: generateAISuggestion(style: String)
     * Stocke les paramètres pour régénération automatique après rejet
     */
    fun generateAISuggestion(token: String, style: String, city: String? = null, temperature: Double? = null) {
        if (_isGenerating.value) return
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            _aiSuggestion.value = null
            
            // Stocker les paramètres pour régénération après rejet
            lastSuggestionStyle = style
            lastSuggestionCity = city
            lastSuggestionTemperature = temperature

            recommendationsRepository.recommendOutfit(token, style, city, temperature).fold(
                onSuccess = { response ->
                    _aiSuggestion.value = response
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isGenerating.value = false
        }
    }

    /**
     * ACCEPTER = Créer l'outfit + Mettre à jour feedback + Recharger la liste
     * Comme iOS: acceptAISuggestion()
     * ✨ Vérifie qu'un outfit avec les mêmes clothesIds n'existe pas déjà
     */
    fun acceptAISuggestion(token: String) {
        val suggestion = _aiSuggestion.value ?: return
        val clothesIds = suggestion.clothesIds ?: return
        if (clothesIds.isEmpty()) return

        if (_isAccepting.value) return
        viewModelScope.launch {
            _isAccepting.value = true
            _errorMessage.value = null

            // ✨ Vérifier qu'un outfit avec les mêmes clothesIds n'existe pas déjà
            val existingOutfit = _outfits.value.firstOrNull { outfit ->
                // Comparer les IDs des vêtements (triés pour éviter les problèmes d'ordre)
                val existingIds = outfit.clothes.map { it.id }.sorted()
                val suggestionIds = clothesIds.sorted()
                existingIds == suggestionIds
            }

            if (existingOutfit != null) {
                // Un outfit similaire existe déjà
                _errorMessage.value = "Cette tenue existe déjà dans votre collection"
                _isAccepting.value = false
                return@launch
            }

            val style = suggestion.metadata?.preference?.replaceFirstChar { char -> char.uppercase() } ?: "Recommended"

            // 1️⃣ Créer l'outfit
            tenuesRepository.createOutfit(
                token = token,
                userId = cachedUserId ?: return@launch,
                clothesIds = clothesIds,
                eventType = style,
                status = "accepted"
            ).fold(
                onSuccess = {
                    // 2️⃣ Mettre à jour les compteurs acceptedCount pour tous les vêtements
                    updateOutfitFeedback(token, clothesIds, accepted = true)
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    _isAccepting.value = false
                }
            )
        }
    }

    /**
     * REJETER = Mettre à jour rejectedCount + Supprimer la suggestion (comme iOS)
     * Ne génère PAS automatiquement une nouvelle suggestion - l'utilisateur doit en demander une nouvelle
     */
    fun rejectAISuggestion(token: String) {
        val suggestion = _aiSuggestion.value ?: return
        val clothesIds = suggestion.clothesIds ?: return

        viewModelScope.launch {
            // Mettre à jour les compteurs rejectedCount
            updateOutfitFeedback(token, clothesIds, accepted = false)
            
            // Supprimer la suggestion actuelle (comme iOS)
            _aiSuggestion.value = null
        }
    }

    /**
     * Met à jour le feedback pour tous les vêtements (comme iOS updateOutfitFeedback)
     */
    private suspend fun updateOutfitFeedback(token: String, clothesIds: List<String>, accepted: Boolean) {
        // Mettre à jour tous les vêtements en parallèle
        val results = clothesIds.map { clotheId ->
            dressingRepository.updateFeedback(token, clotheId, accepted)
        }

        // Vérifier si toutes les mises à jour ont réussi
        val allSuccess = results.all { it.isSuccess }
        
        if (allSuccess) {
            if (accepted) {
                // Si accepté, recharger la liste des outfits
                _successMessage.value = "Outfit approuvé et ajouté avec succès !"
                _aiSuggestion.value = null
                loadData(token)
            }
            // Si rejeté, la suggestion est déjà supprimée dans rejectAISuggestion()
        } else {
            _errorMessage.value = "Erreur lors de la mise à jour du feedback"
        }
        
        _isAccepting.value = false
    }

    val favorites: List<Outfit>
        get() = _outfits.value.filter { it.isFavorite }

    private fun loadData(token: String) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            tenuesRepository.fetchOutfits(token).fold(
                onSuccess = { outfits ->
                    val previousFavorites = _outfits.value.filter { it.isFavorite }.map { it.id }.toSet()
                    val merged = mergeWithDressing(outfits, _availableClothes.value).map { outfit ->
                        if (previousFavorites.contains(outfit.id)) outfit.copy(isFavorite = true) else outfit
                    }
                    _outfits.value = merged.sortedByDescending { it.createdAt }
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _isLoading.value = false
        }
    }

    private fun reconcileOutfitsAndDressing() {
        if (_outfits.value.isEmpty() || _availableClothes.value.isEmpty()) return
        val favorites = _outfits.value.filter { it.isFavorite }.map { it.id }.toSet()
        val merged = mergeWithDressing(_outfits.value, _availableClothes.value).map { outfit ->
            if (favorites.contains(outfit.id)) outfit.copy(isFavorite = true) else outfit
        }
        _outfits.value = merged.sortedByDescending { it.createdAt }
    }

    private fun mergeWithDressing(outfits: List<Outfit>, dressing: List<Cloth>): List<Outfit> {
        if (outfits.isEmpty() || dressing.isEmpty()) return outfits
        val dressingMap = dressing.associateBy { it.id }
        return outfits.map { outfit ->
            val enriched = outfit.clothes.map { cloth ->
                dressingMap[cloth.id]?.let { match ->
                    cloth.copy(
                        name = match.name,
                        type = match.type,
                        colorHex = match.colorHex,
                        imageUrl = match.imageUrl,
                        createdAt = cloth.createdAt ?: match.createdAt
                    )
                } ?: cloth
            }
            outfit.copy(clothes = enriched)
        }
    }
}

