package tn.esprit.labasniandroid.ui.screen.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import tn.esprit.labasniandroid.models.Responses
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.models.services.AuthService

class SignupViewModel(
    private val authService: AuthService = AuthService()
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _selectedGender = MutableStateFlow<User.Gender?>(null)
    val selectedGender: StateFlow<User.Gender?> = _selectedGender.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _needsAcceptance = MutableStateFlow(false)
    val needsAcceptance: StateFlow<Boolean> = _needsAcceptance.asStateFlow()

    private val _signinResponse = MutableStateFlow<Responses.SigninResponse?>(null)
    val signinResponse: StateFlow<Responses.SigninResponse?> = _signinResponse.asStateFlow()

    private val _showPinEntry = MutableStateFlow(false)
    val showPinEntry: StateFlow<Boolean> = _showPinEntry.asStateFlow()

    private val _pinCode = MutableStateFlow("")
    val pinCode: StateFlow<String> = _pinCode.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    private val _resendSecondsRemaining = MutableStateFlow(0)
    val resendSecondsRemaining: StateFlow<Int> = _resendSecondsRemaining.asStateFlow()

    private var tempToken: String? = null
    private var resendJob: kotlinx.coroutines.Job? = null

    val canResendCode: Boolean
        get() = _resendSecondsRemaining.value == 0 && !_isLoading.value

    fun setFullName(value: String) {
        _fullName.value = value
    }

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun setPhoneNumber(value: String) {
        _phoneNumber.value = value
    }

    fun selectGender(gender: User.Gender) {
        _selectedGender.value = gender
    }

    fun setPinCode(value: String) {
        _pinCode.value = value.filter { it.isDigit() }.take(6)
    }

    fun attemptSignup() {
        resetMessages()
        if (!validateFields()) {
            return
        }
        _needsAcceptance.value = true
    }

    fun performSignupAfterAcceptance() {
        viewModelScope.launch {
            resetMessages()
            _needsAcceptance.value = false
            _pinError.value = null
            _pinCode.value = ""

            if (!validateFields()) {
                return@launch
            }

            _isLoading.value = true

            val gender = _selectedGender.value
            if (gender == null) {
                _errorMessage.value = "Veuillez sélectionner un sexe."
                _isLoading.value = false
                return@launch
            }

            authService.register(
                fullName = _fullName.value.trim(),
                email = _email.value.lowercase().trim(),
                password = _password.value,
                gender = gender,
                phoneNumber = _phoneNumber.value.takeIf { it.isNotBlank() }?.trim(),
                preferences = null
            ).fold(
                onSuccess = { response ->
                    tempToken = response.tempToken
                    _successMessage.value = response.message
                    _showPinEntry.value = true
                    startResendCountdown()
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isLoading.value = false
        }
    }

    fun verifyPinCode() {
        viewModelScope.launch {
            _pinError.value = null

            if (_pinCode.value.length != 6) {
                _pinError.value = "Le code doit contenir 6 chiffres."
                return@launch
            }

            val token = tempToken
            if (token == null) {
                _pinError.value = "Token manquant. Veuillez réessayer l'inscription."
                return@launch
            }

            _isLoading.value = true

            authService.verifyEmailCode(
                tempToken = token,
                code = _pinCode.value
            ).fold(
                onSuccess = { response ->
                    _showPinEntry.value = false
                    stopResendCountdown()
                    resetResendCountdown()
                    clearSensitiveFields()
                    _successMessage.value = response.message
                    _navigateToLogin.value = true
                },
                onFailure = { error ->
                    _pinError.value = error.message ?: "Code invalide ou expiré."
                }
            )

            _isLoading.value = false
        }
    }

    fun resendVerificationCode() {
        viewModelScope.launch {
            if (!canResendCode) {
                return@launch
            }

            _pinError.value = null
            _isLoading.value = true

            val gender = _selectedGender.value
            if (gender == null) {
                _errorMessage.value = "Veuillez sélectionner un sexe."
                _isLoading.value = false
                return@launch
            }

            authService.register(
                fullName = _fullName.value.trim(),
                email = _email.value.lowercase().trim(),
                password = _password.value,
                gender = gender,
                phoneNumber = _phoneNumber.value.takeIf { it.isNotBlank() }?.trim(),
                preferences = null
            ).fold(
                onSuccess = { response ->
                    tempToken = response.tempToken
                    _successMessage.value = response.message
                    startResendCountdown()
                },
                onFailure = { error ->
                    _pinError.value = error.message
                }
            )

            _isLoading.value = false
        }
    }

    fun dismissPinEntry() {
        _showPinEntry.value = false
        _pinCode.value = ""
        _pinError.value = null
        stopResendCountdown()
    }

    fun pushBlockingError(message: String) {
        _errorMessage.value = message
    }

    private fun resetMessages() {
        _errorMessage.value = null
        _successMessage.value = null
        _needsAcceptance.value = false
        _pinError.value = null
    }

    private fun validateFields(): Boolean {
        if (_fullName.value.isBlank()) {
            _errorMessage.value = "Le nom complet est requis."
            return false
        }

        if (!isValidEmail(_email.value)) {
            _errorMessage.value = "Adresse email invalide."
            return false
        }

        if (_password.value.length < 6) {
            _errorMessage.value = "Le mot de passe doit contenir au moins 6 caractères."
            return false
        }

        if (_phoneNumber.value.isBlank()) {
            _errorMessage.value = "Le numéro de téléphone est requis."
            return false
        }

        if (_selectedGender.value == null) {
            _errorMessage.value = "Veuillez sélectionner un sexe."
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return pattern.matches(email)
    }

    private fun startResendCountdown() {
        stopResendCountdown()
        _resendSecondsRemaining.value = 60
        resendJob = viewModelScope.launch {
            while (_resendSecondsRemaining.value > 0) {
                delay(1000)
                _resendSecondsRemaining.value--
            }
        }
    }

    private fun stopResendCountdown() {
        resendJob?.cancel()
        resendJob = null
    }

    private fun resetResendCountdown() {
        _resendSecondsRemaining.value = 0
    }

    private fun clearSensitiveFields() {
        _password.value = ""
        _pinCode.value = ""
        tempToken = null
    }
}

