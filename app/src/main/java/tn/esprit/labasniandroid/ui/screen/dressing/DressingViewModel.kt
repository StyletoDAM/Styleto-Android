package tn.esprit.labasniandroid.ui.screen.dressing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.repositories.DressingRepository

class DressingViewModel(
    private val dressingRepository: DressingRepository = DressingRepository()
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
}
