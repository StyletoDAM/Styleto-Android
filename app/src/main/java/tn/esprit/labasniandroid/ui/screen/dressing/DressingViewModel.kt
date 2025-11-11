package tn.esprit.labasniandroid.ui.screen.dressing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tn.esprit.labasniandroid.models.repositories.DressingRepository

// TODO: Implémenter le ViewModel pour Dressing
class DressingViewModel(
    private val dressingRepository: DressingRepository = DressingRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // TODO: Ajouter les StateFlows pour les vêtements
    // private val _clothes = MutableStateFlow<List<Cloth>>(emptyList())
    // val clothes: StateFlow<List<Cloth>> = _clothes.asStateFlow()

    // TODO: Implémenter les méthodes
    // fun loadClothes()
    // fun addCloth()
    // fun deleteCloth()
}

