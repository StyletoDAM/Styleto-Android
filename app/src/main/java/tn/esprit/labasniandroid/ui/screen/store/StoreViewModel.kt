package tn.esprit.labasniandroid.ui.screen.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.StoreItem
import tn.esprit.labasniandroid.models.repositories.DressingRepository
import tn.esprit.labasniandroid.models.repositories.StoreRepository
import tn.esprit.labasniandroid.models.repositories.SubscriptionRepository

class StoreViewModel(
    private val storeRepository: StoreRepository = StoreRepository(),
    private val dressingRepository: DressingRepository = DressingRepository(),
    private val subscriptionRepository: SubscriptionRepository = SubscriptionRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // My Items (mes articles)
    private val _rawStoreItems = MutableStateFlow<List<StoreItem>>(emptyList())
    val rawStoreItems: StateFlow<List<StoreItem>> = _rawStoreItems.asStateFlow()
    
    private val _storeItems = MutableStateFlow<List<StoreItem>>(emptyList())
    val storeItems: StateFlow<List<StoreItem>> = _storeItems.asStateFlow()

    // Discover Items (articles des autres)
    private val _rawDiscoverItems = MutableStateFlow<List<StoreItem>>(emptyList())
    val rawDiscoverItems: StateFlow<List<StoreItem>> = _rawDiscoverItems.asStateFlow()
    
    private val _discoverItems = MutableStateFlow<List<StoreItem>>(emptyList())
    val discoverItems: StateFlow<List<StoreItem>> = _discoverItems.asStateFlow()

    private val _deletingIds = MutableStateFlow<Set<String>>(emptySet())
    val deletingIds: StateFlow<Set<String>> = _deletingIds.asStateFlow()

    private val _availableClothes = MutableStateFlow<List<Cloth>>(emptyList())
    val availableClothes: StateFlow<List<Cloth>> = _availableClothes.asStateFlow()

    private val _isLoadingClothes = MutableStateFlow(false)
    val isLoadingClothes: StateFlow<Boolean> = _isLoadingClothes.asStateFlow()

    private val _showAddToStore = MutableStateFlow(false)
    val showAddToStore: StateFlow<Boolean> = _showAddToStore.asStateFlow()

    private val _showToast = MutableStateFlow(false)
    val showToast: StateFlow<Boolean> = _showToast.asStateFlow()

    private val _showUpgradeToPro = MutableStateFlow(false)
    val showUpgradeToPro: StateFlow<Boolean> = _showUpgradeToPro.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private var searchJob: Job? = null
    private var initialized = false
    private var cachedToken: String? = null

    init {
        // Debounce pour la recherche (300ms comme iOS)
        viewModelScope.launch {
            _searchText.collect { query ->
                searchJob?.cancel()
                searchJob = launch {
                    delay(300)
                    filterItems()
                }
            }
        }
    }

    fun initialize(token: String, userId: String) {
        if (initialized && cachedToken == token) return
        initialized = true
        cachedToken = token
        loadMyStore(token)
    }

    fun loadMyStore(token: String) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            storeRepository.fetchStoreItems(token).fold(
                onSuccess = { items ->
                    _rawStoreItems.value = items
                    _storeItems.value = items
                    loadDiscoverItems(token)
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _isLoading.value = false
        }
    }

    private fun loadDiscoverItems(token: String) {
        viewModelScope.launch {
            storeRepository.fetchAllStoreItems(token).fold(
                onSuccess = { allItems ->
                    val myIds = _rawStoreItems.value.map { it.id }.toSet()
                    val filtered = allItems.filter { !myIds.contains(it.id) }
                    _rawDiscoverItems.value = filtered
                    _discoverItems.value = filtered
                    filterItems() // Applique le filtre actuel
                },
                onFailure = { error -> 
                    // Ne pas afficher d'erreur pour Discover, c'est optionnel
                }
            )
        }
    }

    fun setSearchText(query: String) {
        _searchText.value = query
    }

    private fun filterItems() {
        val query = _searchText.value.lowercase().trim()

        if (query.isEmpty()) {
            _storeItems.value = _rawStoreItems.value
            _discoverItems.value = _rawDiscoverItems.value
            return
        }

        val filteredMy = _rawStoreItems.value.filter { matchesSearch(it, query) }
        val filteredDiscover = _rawDiscoverItems.value.filter { matchesSearch(it, query) }

        _storeItems.value = filteredMy
        _discoverItems.value = filteredDiscover
    }

    private fun matchesSearch(item: StoreItem, query: String): Boolean {
        val category = item.cloth?.type?.lowercase() ?: ""
        val price = "${item.price}"
        val status = item.status?.lowercase() ?: ""
        val name = item.cloth?.name?.lowercase() ?: ""
        val size = item.size?.lowercase() ?: ""

        return category.contains(query) ||
               price.contains(query) ||
               status.contains(query) ||
               name.contains(query) ||
               size.contains(query)
    }

    fun deleteStoreItem(token: String, storeItemId: String) {
        if (_deletingIds.value.contains(storeItemId)) return
        viewModelScope.launch {
            _deletingIds.value = _deletingIds.value + storeItemId
            storeRepository.deleteStoreItem(token, storeItemId).fold(
                onSuccess = {
                    _rawStoreItems.value = _rawStoreItems.value.filterNot { it.id == storeItemId }
                    _rawDiscoverItems.value = _rawDiscoverItems.value.filterNot { it.id == storeItemId }
                    filterItems()
                    _showToast.value = true
                    viewModelScope.launch {
                        delay(2000)
                        _showToast.value = false
                    }
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )
            _deletingIds.value = _deletingIds.value - storeItemId
        }
    }

    fun loadAvailableClothes(token: String) {
        if (_isLoadingClothes.value) return
        viewModelScope.launch {
            _isLoadingClothes.value = true
            dressingRepository.fetchClothes(token).fold(
                onSuccess = { clothes ->
                    // Exclure les vêtements déjà en vente
                    val storeClothIds = _rawStoreItems.value.mapNotNull { it.cloth?.id }.toSet()
                    _availableClothes.value = clothes.filter { !storeClothIds.contains(it.id) }
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )
            _isLoadingClothes.value = false
        }
    }

    fun addStoreItem(token: String, selectedCloth: Cloth, price: Double, size: String) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            // Vérifier le quota avant d'ajouter l'article
            subscriptionRepository.checkStoreSellingQuota(token).fold(
                onSuccess = { quota ->
                    if (!quota.allowed) {
                        // Limite atteinte, afficher le dialog d'upgrade
                        _showUpgradeToPro.value = true
                        _isSubmitting.value = false
                        return@launch
                    }
                },
                onFailure = {
                    // En cas d'erreur de quota, continuer quand même (peut être temporaire)
                }
            )

            // Si le quota est OK, ajouter l'article
            storeRepository.addStoreItem(
                token = token,
                clothesId = selectedCloth.id,
                price = price,
                size = size
            ).fold(
                onSuccess = {
                    _showAddToStore.value = false
                    // Recharger la liste pour obtenir les objets avec clothesId populé
                    loadMyStore(token)
                    _successMessage.value = "Article ajouté à la boutique."
                },
                onFailure = { error ->
                    // Vérifier si l'erreur vient du backend (limite atteinte)
                    val errorMsg = error.message ?: ""
                    if (errorMsg.contains("limit", ignoreCase = true) || 
                        errorMsg.contains("quota", ignoreCase = true) ||
                        errorMsg.contains("exceeded", ignoreCase = true)) {
                        _showUpgradeToPro.value = true
                    } else {
                        // Même en cas d'erreur de parsing, l'item peut avoir été créé
                        // Donc on recharge quand même la liste
                        loadMyStore(token)
                        _errorMessage.value = error.message
                    }
                }
            )

            _isSubmitting.value = false
        }
    }
    
    fun hideUpgradeToPro() {
        _showUpgradeToPro.value = false
    }

    fun showAddToStore() {
        _showAddToStore.value = true
    }

    fun hideAddToStore() {
        _showAddToStore.value = false
    }

    fun updateStorePrice(token: String, storeItemId: String, price: Double, size: String) {
        viewModelScope.launch {
            _isLoading.value = true
            storeRepository.updateStorePrice(token, storeItemId, price = price, size = size).fold(
                onSuccess = { updatedItem ->
                    // Mettre à jour dans les listes
                    _rawStoreItems.value = _rawStoreItems.value.map { 
                        if (it.id == storeItemId) updatedItem else it 
                    }
                    filterItems()
                    _showToast.value = true
                    viewModelScope.launch {
                        delay(2000)
                        _showToast.value = false
                    }
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )
            _isLoading.value = false
        }
    }

    fun markAsSold(token: String, storeItemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            storeRepository.markAsSold(token, storeItemId).fold(
                onSuccess = { updatedItem ->
                    // Mettre à jour dans les listes
                    _rawStoreItems.value = _rawStoreItems.value.map { 
                        if (it.id == storeItemId) updatedItem else it 
                    }
                    filterItems()
                    _showToast.value = true
                    viewModelScope.launch {
                        delay(2000)
                        _showToast.value = false
                    }
                },
                onFailure = { error -> _errorMessage.value = error.message }
            )
            _isLoading.value = false
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
