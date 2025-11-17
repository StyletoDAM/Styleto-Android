package tn.esprit.labasniandroid.ui.screen.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.models.repositories.ProfileRepository
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.utils.TokenManager

class ProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _accountDeleted = MutableStateFlow(false)
    val accountDeleted: StateFlow<Boolean> = _accountDeleted.asStateFlow()

    private val _isPhotoUpdating = MutableStateFlow(false)
    val isPhotoUpdating: StateFlow<Boolean> = _isPhotoUpdating.asStateFlow()

    fun loadProfile(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            profileRepository.getProfile(token).fold(
                onSuccess = { user ->
                    _user.value = user
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isLoading.value = false
        }
    }

    fun updateProfile(
        token: String,
        fullName: String? = null,
        email: String? = null,
        gender: String? = null,
        phoneNumber: String? = null,
        preferences: List<String>? = null,
        password: String? = null
    ) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "=== START UPDATE PROFILE ===")
                Log.d("ProfileViewModel", "fullName: $fullName, email: $email, gender: $gender")
                
                _isLoading.value = true
                _errorMessage.value = null
                _successMessage.value = null

                val result = profileRepository.updateProfile(
                    token = token,
                    fullName = fullName,
                    email = email,
                    gender = gender,
                    phoneNumber = phoneNumber,
                    preferences = preferences,
                    password = password
                )
                
                result.fold(
                    onSuccess = { updatedUser ->
                        Log.d("ProfileViewModel", "Update SUCCESS - New fullName: ${updatedUser.fullName}")
                        _user.value = updatedUser
                        _successMessage.value = "Profil mis à jour avec succès !"
                        Log.d("ProfileViewModel", "Success message set: ${_successMessage.value}")
                    },
                    onFailure = { error ->
                        Log.e("ProfileViewModel", "Update FAILED: ${error.message}")
                        _errorMessage.value = error.message
                    }
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception in updateProfile: ${e.message}", e)
                _errorMessage.value = "Erreur inattendue: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("ProfileViewModel", "=== END UPDATE PROFILE ===")
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun acknowledgeAccountDeleted() {
        _accountDeleted.value = false
    }

    fun setInitialUser(initialUser: User?) {
        if (initialUser != null) {
            _user.value = initialUser
        }
    }

    fun deleteAccount(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            profileRepository.deleteAccount(token).fold(
                onSuccess = { response ->
                    _user.value = null
                    _accountDeleted.value = true
                    _successMessage.value = response.message
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isLoading.value = false
        }
    }

    fun uploadProfilePhoto(token: String, imageData: ByteArray) {
        viewModelScope.launch {
            _isPhotoUpdating.value = true
            _errorMessage.value = null
            _successMessage.value = null

            profileRepository.uploadProfilePhoto(token, imageData).fold(
                onSuccess = { updatedUser ->
                    _user.value = updatedUser
                    _successMessage.value = "Photo de profil mise à jour avec succès !"
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isPhotoUpdating.value = false
        }
    }

    fun setProfilePictureFromUrl(token: String, imageUrl: String) {
        viewModelScope.launch {
            _isPhotoUpdating.value = true
            _errorMessage.value = null
            _successMessage.value = null

            profileRepository.updateProfile(
                token = token,
                profilePictureUrl = imageUrl
            ).fold(
                onSuccess = { updatedUser ->
                    _user.value = updatedUser
                    _successMessage.value = "Photo de profil mise à jour !"
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isPhotoUpdating.value = false
        }
    }
}

