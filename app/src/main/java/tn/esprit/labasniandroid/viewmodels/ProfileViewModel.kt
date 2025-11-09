package tn.esprit.labasniandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.User
import tn.esprit.labasniandroid.services.ProfileService

class ProfileViewModel(
    private val profileService: ProfileService = ProfileService()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun loadProfile(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            profileService.getProfile(token).fold(
                onSuccess = { user ->
                    _user.value = user
                    _errorMessage.value = null
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    _user.value = null
                }
            )

            _isLoading.value = false
        }
    }

    fun resetFeedback() {
        _errorMessage.value = null
    }
}

