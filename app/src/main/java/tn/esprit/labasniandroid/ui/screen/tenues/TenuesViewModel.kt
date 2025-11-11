package tn.esprit.labasniandroid.ui.screen.tenues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tn.esprit.labasniandroid.models.repositories.TenuesRepository

// TODO: Implémenter le ViewModel pour Tenues
class TenuesViewModel(
    private val tenuesRepository: TenuesRepository = TenuesRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // TODO: Ajouter les StateFlows pour les tenues
    // private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    // val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    // TODO: Implémenter les méthodes
    // fun loadOutfits()
    // fun createOutfit()
    // fun updateOutfit()
    // fun deleteOutfit()
}

