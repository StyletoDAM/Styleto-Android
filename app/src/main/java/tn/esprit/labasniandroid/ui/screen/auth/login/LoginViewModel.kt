package tn.esprit.labasniandroid.ui.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.content.Context
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.Responses
import tn.esprit.labasniandroid.models.services.AuthService

class LoginViewModel(
    private val authService: AuthService = AuthService()
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _signinResponse = MutableStateFlow<Responses.SigninResponse?>(null)
    val signinResponse: StateFlow<Responses.SigninResponse?> = _signinResponse.asStateFlow()

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun signin(context: Context) {
        viewModelScope.launch {
            resetFeedback()

            if (!validateFields()) {
                return@launch
            }

            _isLoading.value = true

            authService.login(
                context = context,
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
        context: Context,
        googleId: String,
        fullName: String,
        email: String,
        profilePicture: String? = null
    ) {
        viewModelScope.launch {
            resetFeedback()
            _isLoading.value = true

            authService.loginWithGoogle(
                context = context,
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

