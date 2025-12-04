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

    // ✨ NOUVEAU : Vêtement sélectionné pour la vente (comme iOS)
    private val _selectedClothe = MutableStateFlow<Cloth?>(null)
    val selectedClothe: StateFlow<Cloth?> = _selectedClothe.asStateFlow()
    
    // ✨ NOUVEAU : Champs du formulaire (comme iOS)
    private val _priceInput = MutableStateFlow("")
    val priceInput: StateFlow<String> = _priceInput.asStateFlow()
    
    private val _selectedSize = MutableStateFlow<String?>("M")
    val selectedSize: StateFlow<String?> = _selectedSize.asStateFlow()
    
    private val _shoeSizeInput = MutableStateFlow("")
    val shoeSizeInput: StateFlow<String> = _shoeSizeInput.asStateFlow()
    
    private val _isShoes = MutableStateFlow(false)
    val isShoes: StateFlow<Boolean> = _isShoes.asStateFlow()

    private val _showToast = MutableStateFlow(false)
    val showToast: StateFlow<Boolean> = _showToast.asStateFlow()

    private val _showUpgradeToPro = MutableStateFlow(false)
    val showUpgradeToPro: StateFlow<Boolean> = _showUpgradeToPro.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // ✨ Suggestions de vente (comme iOS)
    private val _sellSuggestions = MutableStateFlow<List<Cloth>>(emptyList())
    val sellSuggestions: StateFlow<List<Cloth>> = _sellSuggestions.asStateFlow()

    private val _currentSuggestion = MutableStateFlow<Cloth?>(null)
    val currentSuggestion: StateFlow<Cloth?> = _currentSuggestion.asStateFlow()

    private val _showSellSuggestion = MutableStateFlow(false)
    val showSellSuggestion: StateFlow<Boolean> = _showSellSuggestion.asStateFlow()

    private val _dismissedSuggestionIds = MutableStateFlow<Set<String>>(emptySet())
    val dismissedSuggestionIds: StateFlow<Set<String>> = _dismissedSuggestionIds.asStateFlow()

    private var searchJob: Job? = null
    private var initialized = false
    private var cachedToken: String? = null

    init {
        // Debounce pour la recherche (300ms comme iOS)
        viewModelScope.launch {
            try {
            _searchText.collect { query ->
                searchJob?.cancel()
                searchJob = launch {
                    delay(300)
                    filterItems()
                }
                }
            } catch (e: Exception) {
                // Ignorer les erreurs de collect (ViewModel détruit)
            }
        }
    }

    fun initialize(token: String, userId: String) {
        cachedToken = token
        if (!initialized) {
            initialized = true
        loadMyStore(token)
        } else {
            // Si déjà initialisé, recharger les suggestions seulement si on est dans My Items
            // (ne pas réafficher les suggestions rejetées)
            loadSellSuggestions(token)
        }
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
                    loadSellSuggestions(token) // ✨ Charger les suggestions (comme iOS)
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
                    // Recharger les suggestions après suppression
                    cachedToken?.let { loadSellSuggestions(it) }
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
                    // Nettoyer le formulaire
                    clearAddToStoreForm()
                    _showAddToStore.value = false
                    // Recharger la liste pour obtenir les objets avec clothesId populé
                    loadMyStore(token)
                    _successMessage.value = "Article ajouté à la boutique."
                    // Recharger les suggestions après ajout
                    loadSellSuggestions(token)
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
                    // Recharger les suggestions après mise à jour
                    loadSellSuggestions(token)
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
                    // Recharger les suggestions après vente
                    loadSellSuggestions(token)
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

    // ✨ Charger les suggestions de vente (comme iOS)
    fun loadSellSuggestions(token: String) {
        viewModelScope.launch {
            try {
                dressingRepository.fetchSellSuggestions(token).fold(
                    onSuccess = { suggestions ->
                        // Filtrer les suggestions déjà rejetées ou déjà dans le store (comme iOS)
                        val storeClothesIds = _rawStoreItems.value.mapNotNull { it.cloth?.id }.toSet()
                        val filtered: List<Cloth> = suggestions.filter { clothe ->
                            !_dismissedSuggestionIds.value.contains(clothe.id) &&
                            !storeClothesIds.contains(clothe.id)
                        }
                        _sellSuggestions.value = filtered
                        
                        // ✨ NOUVEAU : Ne plus gérer currentSuggestion, toutes les suggestions sont affichées dans la liste
                        // Garder currentSuggestion pour compatibilité avec les fonctions existantes
                        if (filtered.isNotEmpty() && _currentSuggestion.value == null) {
                            _currentSuggestion.value = filtered.first()
                        } else if (filtered.isEmpty()) {
                            _currentSuggestion.value = null
                        }
                    },
                    onFailure = { error ->
                        // Ne pas afficher d'erreur, c'est optionnel
                        android.util.Log.d("StoreViewModel", "Error loading sell suggestions: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("StoreViewModel", "Exception loading sell suggestions", e)
            }
        }
    }

    // ✨ Afficher la suggestion suivante (comme iOS)
    fun showNextSuggestion() {
        try {
            val next = _sellSuggestions.value.firstOrNull { clothe ->
                !_dismissedSuggestionIds.value.contains(clothe.id)
            }
            if (next != null) {
                _currentSuggestion.value = next
                _showSellSuggestion.value = true
            } else {
                _currentSuggestion.value = null
                _showSellSuggestion.value = false
            }
        } catch (e: Exception) {
            android.util.Log.e("StoreViewModel", "Exception showing next suggestion", e)
            _currentSuggestion.value = null
            _showSellSuggestion.value = false
        }
    }

    // ✨ Accepter la suggestion (préparer la vente) (comme iOS)
    fun acceptSellSuggestion(token: String) {
        val suggestion = _currentSuggestion.value ?: return

        try {
            // Préparer le formulaire de vente (comme iOS)
            _selectedClothe.value = suggestion
            _priceInput.value = ""
            _shoeSizeInput.value = ""
            _selectedSize.value = "M"

            // Détection automatique chaussures (comme iOS)
            val category = suggestion.type.ifBlank { "" }.lowercase()
            val isShoesDetected = category.contains("shoe") ||
                    category.contains("sneaker") ||
                    category.contains("basket") ||
                    category.contains("boot") ||
                    category.contains("chaussure") ||
                    category.contains("footwear")
            _isShoes.value = isShoesDetected

            // Reset taille selon type (comme iOS)
            if (isShoesDetected) {
                _shoeSizeInput.value = ""
            } else {
                _selectedSize.value = "M"
            }

            // Fermer la suggestion IMMÉDIATEMENT
            _showSellSuggestion.value = false
            _currentSuggestion.value = null

            // Marquer comme traité
            _dismissedSuggestionIds.value = _dismissedSuggestionIds.value + suggestion.id

            // Charger les vêtements disponibles pour le formulaire
            loadAvailableClothes(token)

            // Ouvrir le sheet d'ajout APRÈS avoir fermé la suggestion (comme iOS)
            viewModelScope.launch {
                kotlinx.coroutines.delay(300)
                _showAddToStore.value = true
            }
        } catch (e: Exception) {
            android.util.Log.e("StoreViewModel", "Exception accepting sell suggestion", e)
        }
    }

    // ✨ Accepter la suggestion (préparer la vente) - version avec suggestion spécifique
    fun acceptSellSuggestionForItem(token: String, suggestionId: String) {
        viewModelScope.launch {
            try {
                // Trouver la suggestion dans la liste
                val suggestion = _sellSuggestions.value.firstOrNull { it.id == suggestionId } ?: return@launch

                // Préparer le formulaire de vente (comme iOS)
                _selectedClothe.value = suggestion
                _priceInput.value = ""
                _shoeSizeInput.value = ""
                _selectedSize.value = "M"

                // Détection automatique chaussures (comme iOS)
                val category = suggestion.type.ifBlank { "" }.lowercase()
                val isShoesDetected = category.contains("shoe") ||
                        category.contains("sneaker") ||
                        category.contains("basket") ||
                        category.contains("boot") ||
                        category.contains("chaussure") ||
                        category.contains("footwear")
                _isShoes.value = isShoesDetected

                // Reset taille selon type (comme iOS)
                if (isShoesDetected) {
                    _shoeSizeInput.value = ""
                } else {
                    _selectedSize.value = "M"
                }

                // Marquer comme traité
                _dismissedSuggestionIds.value = _dismissedSuggestionIds.value + suggestion.id
                
                // Retirer de la liste des suggestions
                _sellSuggestions.value = _sellSuggestions.value.filter { it.id != suggestionId }

                // Charger les vêtements disponibles pour le formulaire
                loadAvailableClothes(token)

                // Ouvrir le sheet d'ajout APRÈS avoir fermé la suggestion (comme iOS)
                kotlinx.coroutines.delay(300)
                _showAddToStore.value = true
            } catch (e: Exception) {
                android.util.Log.e("StoreViewModel", "Exception accepting sell suggestion", e)
            }
        }
    }

    // ✨ Cacher la suggestion (bouton X) - Juste cacher, ne pas retirer de la liste
    fun rejectSellSuggestion() {
        try {
            // NE PAS marquer comme rejeté dans dismissedSuggestionIds
            // Juste cacher l'affichage, garder la suggestion pour qu'elle réapparaisse
            _showSellSuggestion.value = false
            // Garder _currentSuggestion.value pour qu'elle réapparaisse au retour
        } catch (e: Exception) {
            android.util.Log.e("StoreViewModel", "Exception hiding sell suggestion", e)
            // En cas d'erreur, s'assurer que la carte est cachée
            _showSellSuggestion.value = false
        }
    }
    
    // ✨ Passer à la suggestion suivante ("Not Now")
    fun nextSellSuggestion() {
        try {
            val currentId = _currentSuggestion.value?.id
            
            // Marquer la suggestion actuelle comme rejetée (pour ne plus l'afficher)
            if (currentId != null) {
                _dismissedSuggestionIds.value = _dismissedSuggestionIds.value + currentId
            }
            
            // Retirer de la liste des suggestions
            _sellSuggestions.value = _sellSuggestions.value.filter { it.id != currentId }
            
            // Chercher la prochaine suggestion disponible
            val next = _sellSuggestions.value.firstOrNull { clothe ->
                !_dismissedSuggestionIds.value.contains(clothe.id)
            }
            
            if (next != null) {
                // Afficher la suggestion suivante
                _currentSuggestion.value = next
                _showSellSuggestion.value = true
            } else {
                // Plus de suggestions disponibles, cacher
                _currentSuggestion.value = null
                _showSellSuggestion.value = false
            }
        } catch (e: Exception) {
            android.util.Log.e("StoreViewModel", "Exception going to next suggestion", e)
            // En cas d'erreur, cacher
            _showSellSuggestion.value = false
        }
    }
    
    // ✨ NOUVEAU : Fonction pour cacher une suggestion spécifique (bouton X)
    fun dismissSellSuggestion(suggestionId: String) {
        viewModelScope.launch {
            try {
                // Marquer comme rejeté
                _dismissedSuggestionIds.value = _dismissedSuggestionIds.value + suggestionId
                
                // Retirer de la liste des suggestions
                _sellSuggestions.value = _sellSuggestions.value.filter { it.id != suggestionId }
                
                // Si c'était la suggestion actuelle, la retirer aussi
                if (_currentSuggestion.value?.id == suggestionId) {
                    _currentSuggestion.value = null
                    _showSellSuggestion.value = false
                }
            } catch (e: Exception) {
                android.util.Log.e("StoreViewModel", "Error dismissing sell suggestion", e)
            }
        }
    }
    
    // Fonctions pour gérer les champs du formulaire
    fun setPriceInput(price: String) {
        _priceInput.value = price
    }
    
    fun setSelectedSize(size: String?) {
        _selectedSize.value = size
    }
    
    fun setShoeSizeInput(size: String) {
        _shoeSizeInput.value = size
    }
    
    fun setSelectedClothe(clothe: Cloth?) {
        _selectedClothe.value = clothe
        // Détection automatique chaussures
        if (clothe != null) {
            val category = (clothe.type ?: "").lowercase()
            val isShoesDetected = category.contains("shoe") ||
                    category.contains("sneaker") ||
                    category.contains("basket") ||
                    category.contains("boot") ||
                    category.contains("chaussure") ||
                    category.contains("footwear")
            _isShoes.value = isShoesDetected
            if (isShoesDetected) {
                _shoeSizeInput.value = ""
            } else {
                _selectedSize.value = "M"
            }
        }
    }
    
    fun clearAddToStoreForm() {
        _selectedClothe.value = null
        _priceInput.value = ""
        _selectedSize.value = "M"
        _shoeSizeInput.value = ""
        _isShoes.value = false
    }
    
    // ✨ Cacher la suggestion (appelé quand on change d'onglet)
    fun hideSellSuggestion() {
        _showSellSuggestion.value = false
        // Ne pas réinitialiser _currentSuggestion pour garder l'état
    }
    
    // ✨ Réafficher la suggestion actuelle (appelé quand on revient à My Items)
    fun showCurrentSuggestion() {
        try {
            if (_currentSuggestion.value != null) {
                // Réafficher la suggestion actuelle (celle qui a été cachée avec "Not Now")
                _showSellSuggestion.value = true
            } else if (_sellSuggestions.value.isNotEmpty()) {
                // Si aucune suggestion n'est en cours mais qu'il y en a dans la liste, afficher la première
                showNextSuggestion()
            }
        } catch (e: Exception) {
            android.util.Log.e("StoreViewModel", "Exception showing current suggestion", e)
        }
    }
}
