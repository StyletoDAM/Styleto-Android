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

class TenuesViewModel(
    private val tenuesRepository: TenuesRepository = TenuesRepository(),
    private val dressingRepository: DressingRepository = DressingRepository()
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

    // Variables de génération supprimées - pas de génération d'outfits

    private var initialized = false
    private var cachedToken: String? = null
    private var cachedUserId: String? = null

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

    // Fonctions de génération supprimées - pas de génération d'outfits

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

