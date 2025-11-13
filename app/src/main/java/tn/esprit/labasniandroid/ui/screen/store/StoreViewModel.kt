package tn.esprit.labasniandroid.ui.screen.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.StoreItem
import tn.esprit.labasniandroid.models.repositories.DressingRepository
import tn.esprit.labasniandroid.models.repositories.StoreRepository

class StoreViewModel(
    private val storeRepository: StoreRepository = StoreRepository(),
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

    private val _storeItems = MutableStateFlow<List<StoreItem>>(emptyList())
    val storeItems: StateFlow<List<StoreItem>> = _storeItems.asStateFlow()

    private val _availableClothes = MutableStateFlow<List<Cloth>>(emptyList())
    val availableClothes: StateFlow<List<Cloth>> = _availableClothes.asStateFlow()

    private val _isLoadingClothes = MutableStateFlow(false)
    val isLoadingClothes: StateFlow<Boolean> = _isLoadingClothes.asStateFlow()

    private var initialized = false
    private var cachedToken: String? = null
    private var cachedUserId: String? = null

    fun initialize(token: String, userId: String) {
        if (initialized && cachedToken == token && cachedUserId == userId) return
        initialized = true
        cachedToken = token
        cachedUserId = userId
        loadStoreItems(token)
        loadAvailableClothes(token)
    }

    fun refresh(token: String) {
        cachedToken = token
        loadStoreItems(token)
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun loadAvailableClothes(token: String) {
        if (_isLoadingClothes.value) return
        viewModelScope.launch {
            _isLoadingClothes.value = true
            dressingRepository.fetchClothes(token).fold(
                onSuccess = { clothes ->
                    _availableClothes.value = clothes
                    reconcileWithDressing()
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )
            _isLoadingClothes.value = false
        }
    }

    fun addStoreItem(token: String, selectedCloth: Cloth, price: Double) {
        val userId = cachedUserId ?: return
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            storeRepository.addStoreItem(
                token = token,
                userId = userId,
                clothesId = selectedCloth.id,
                price = price
            ).fold(
                onSuccess = { item ->
                    val enriched = mergeWithDressing(listOf(item), _availableClothes.value).firstOrNull() ?: item
                    _storeItems.value = listOf(enriched) + _storeItems.value
                    _successMessage.value = "Article ajouté à la boutique."
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _isSubmitting.value = false
        }
    }

    private fun loadStoreItems(token: String) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            storeRepository.fetchStoreItems(token).fold(
                onSuccess = { items ->
                    _storeItems.value = mergeWithDressing(items, _availableClothes.value)
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _isLoading.value = false
        }
    }

    private fun reconcileWithDressing() {
        if (_storeItems.value.isEmpty() || _availableClothes.value.isEmpty()) return
        _storeItems.value = mergeWithDressing(_storeItems.value, _availableClothes.value)
    }

    private fun mergeWithDressing(storeItems: List<StoreItem>, dressing: List<Cloth>): List<StoreItem> {
        if (storeItems.isEmpty() || dressing.isEmpty()) return storeItems
        val dressingMap = dressing.associateBy { it.id }
        return storeItems.map { item ->
            val cloth = item.cloth
            val enrichedCloth = dressingMap[cloth?.id]?.let { match ->
                match.copy(imageUrl = match.imageUrl, createdAt = cloth?.createdAt ?: match.createdAt)
            } ?: cloth
            item.copy(cloth = enrichedCloth)
        }
    }
}

