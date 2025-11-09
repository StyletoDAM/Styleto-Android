package tn.esprit.labasniandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.SigninResponse
import tn.esprit.labasniandroid.services.SigninService
import tn.esprit.labasniandroid.services.GoogleAuthService

class LoginViewModel(
    private val signinService: SigninService = SigninService(),
    private val googleAuthService: GoogleAuthService = GoogleAuthService()
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _signinResponse = MutableStateFlow<SigninResponse?>(null)
    val signinResponse: StateFlow<SigninResponse?> = _signinResponse.asStateFlow()

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun signin() {
        viewModelScope.launch {
            resetFeedback()

            if (!validateFields()) {
                return@launch
            }

            _isLoading.value = true

            signinService.signin(
                email = _email.value.lowercase().trim(),
                password = _password.value
            ).fold(
                onSuccess = { response ->
                    _signinResponse.value = response
                    _errorMessage.value = null
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    _signinResponse.value = null
                }
            )

            _isLoading.value = false
        }
    }

    fun signInWithGoogle(
        googleId: String,
        fullName: String,
        email: String,
        profilePicture: String? = null
    ) {
        viewModelScope.launch {
            resetFeedback()
            _isLoading.value = true

            googleAuthService.authenticate(
                googleId = googleId,
                fullName = fullName,
                email = email,
                profilePicture = profilePicture,
                gender = null
            ).fold(
                onSuccess = { response ->
                    _signinResponse.value = response
                    _errorMessage.value = null
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    _signinResponse.value = null
                }
            )

            _isLoading.value = false
        }
    }

    fun resetFeedback() {
        _errorMessage.value = null
        _signinResponse.value = null
    }

    private fun validateFields(): Boolean {
        if (!isValidEmail(_email.value)) {
            _errorMessage.value = "Adresse email invalide."
            return false
        }

        if (_password.value.length < 6) {
            _errorMessage.value = "Mot de passe trop court."
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return pattern.matches(email)
    }
}

