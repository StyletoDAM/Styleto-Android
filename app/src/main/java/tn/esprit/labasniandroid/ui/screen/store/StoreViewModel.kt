package tn.esprit.labasniandroid.ui.screen.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tn.esprit.labasniandroid.models.repositories.StoreRepository

// TODO: Implémenter le ViewModel pour Store
class StoreViewModel(
    private val storeRepository: StoreRepository = StoreRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // TODO: Ajouter les StateFlows pour les produits
    // private val _products = MutableStateFlow<List<Product>>(emptyList())
    // val products: StateFlow<List<Product>> = _products.asStateFlow()

    // TODO: Implémenter les méthodes
    // fun loadProducts()
    // fun searchProducts(query: String)
    // fun getProductById(id: String)
}

