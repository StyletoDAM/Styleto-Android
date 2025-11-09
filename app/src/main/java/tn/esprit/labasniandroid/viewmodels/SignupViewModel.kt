package tn.esprit.labasniandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import tn.esprit.labasniandroid.models.SigninResponse
import tn.esprit.labasniandroid.models.SignupResponse
import tn.esprit.labasniandroid.models.User
import tn.esprit.labasniandroid.services.SigninService
import tn.esprit.labasniandroid.services.SignupService
import tn.esprit.labasniandroid.services.VerifyEmailService

class SignupViewModel(
    private val signupService: SignupService = SignupService(),
    private val signinService: SigninService = SigninService(),
    private val verifyEmailService: VerifyEmailService = VerifyEmailService()
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

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

    private val _signinResponse = MutableStateFlow<SigninResponse?>(null)
    val signinResponse: StateFlow<SigninResponse?> = _signinResponse.asStateFlow()

    private val _tempToken = MutableStateFlow<String?>(null)
    val tempToken: StateFlow<String?> = _tempToken.asStateFlow()

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

    private var resendTimerJob: Job? = null

    val canResendCode: StateFlow<Boolean> = combine(
        _resendSecondsRemaining,
        _isLoading
    ) { seconds: Int, loading: Boolean ->
        seconds == 0 && !loading
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun setFullName(value: String) {
        _fullName.value = value
    }

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPhoneNumber(value: String) {
        _phoneNumber.value = value
    }

    fun setPinCode(value: String) {
        // Limiter à 6 chiffres
        val digits = value.filter { it.isDigit() }
        _pinCode.value = digits.take(6)
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun selectGender(gender: User.Gender) {
        _selectedGender.value = gender
    }

    fun attemptSignup() {
        _errorMessage.value = null
        _successMessage.value = null
        _needsAcceptance.value = false

        if (!validateFields()) {
            return
        }

        _needsAcceptance.value = true
    }

    fun performSignupAfterAcceptance() {
        viewModelScope.launch {
            _errorMessage.value = null
            _successMessage.value = null
            _needsAcceptance.value = false
            _pinError.value = null
            _pinCode.value = ""

            _isLoading.value = true

            val gender = _selectedGender.value
            if (gender == null) {
                _errorMessage.value = "Veuillez sélectionner un sexe."
                _isLoading.value = false
                return@launch
            }

            // Utiliser le numéro de téléphone tel quel
            val phoneNumberValue = _phoneNumber.value.trim()
            if (phoneNumberValue.isEmpty() || phoneNumberValue.filter { it.isDigit() }.length < 6) {
                _errorMessage.value = "Numéro de téléphone invalide (minimum 6 chiffres)."
                _isLoading.value = false
                return@launch
            }

            signupService.signup(
                fullName = _fullName.value.trim(),
                email = _email.value.lowercase().trim(),
                password = _password.value,
                gender = gender,
                phoneNumber = phoneNumberValue,
                preferences = null
            ).fold(
                onSuccess = { response ->
                    // Le backend retourne maintenant un tempToken pour la vérification email
                    if (response.tempToken != null) {
                        _tempToken.value = response.tempToken
                        _successMessage.value = response.message
                        _showPinEntry.value = true
                        startResendCountdown()
                    } else {
                        // Fallback si pas de tempToken (ancien comportement)
                        _errorMessage.value = "Erreur lors de l'inscription."
                    }
                },
                onFailure = { error ->
                    _errorMessage.value = when (error) {
                        is tn.esprit.labasniandroid.models.NetworkError.ServerMessage -> error.message
                        else -> "Une erreur est survenue."
                    }
                }
            )

            _isLoading.value = false
        }
    }

    fun verifyPinCode() {
        viewModelScope.launch {
            _pinError.value = null
            _isLoading.value = true

            val token = _tempToken.value
            if (token == null) {
                _pinError.value = "Token invalide."
                _isLoading.value = false
                return@launch
            }

            verifyEmailService.verify(token, _pinCode.value).fold(
                onSuccess = { response ->
                    // Après vérification réussie, se connecter automatiquement
                    val email = _email.value.lowercase().trim()
                    val password = _password.value
                    
                    signinService.signin(email, password).fold(
                        onSuccess = { signinResponse ->
                            _signinResponse.value = signinResponse
                            _showPinEntry.value = false
                            stopResendTimer()
                            clearSensitiveFields()
                            _navigateToLogin.value = true
                        },
                        onFailure = { error ->
                            // La vérification a réussi mais la connexion automatique a échoué
                            _showPinEntry.value = false
                            stopResendTimer()
                            clearSensitiveFields()
                            _navigateToLogin.value = true
                        }
                    )
                },
                onFailure = { error ->
                    _pinError.value = when (error) {
                        is tn.esprit.labasniandroid.models.NetworkError.ServerMessage -> error.message
                        else -> "Code invalide ou expiré."
                    }
                }
            )

            _isLoading.value = false
        }
    }

    fun resendVerificationCode() {
        if (!canResendCode.value) return
        viewModelScope.launch {
            _pinError.value = null
            _isLoading.value = true

            val gender = _selectedGender.value
            if (gender == null) {
                _pinError.value = "Veuillez sélectionner un sexe."
                _isLoading.value = false
                return@launch
            }

            val phoneNumberValue = _phoneNumber.value.trim()
            if (phoneNumberValue.isEmpty() || phoneNumberValue.filter { it.isDigit() }.length < 6) {
                _pinError.value = "Numéro de téléphone invalide (minimum 6 chiffres)."
                _isLoading.value = false
                return@launch
            }

            signupService.signup(
                fullName = _fullName.value.trim(),
                email = _email.value.lowercase().trim(),
                password = _password.value,
                gender = gender,
                phoneNumber = phoneNumberValue,
                preferences = null
            ).fold(
                onSuccess = { response ->
                    if (response.tempToken != null) {
                        _tempToken.value = response.tempToken
                        _successMessage.value = response.message
                        startResendCountdown()
                    }
                },
                onFailure = { error ->
                    _pinError.value = when (error) {
                        is tn.esprit.labasniandroid.models.NetworkError.ServerMessage -> error.message
                        else -> "Une erreur est survenue."
                    }
                }
            )

            _isLoading.value = false
        }
    }


    private fun startResendCountdown(duration: Int = 60) {
        resendTimerJob?.cancel()
        _resendSecondsRemaining.value = duration

        resendTimerJob = viewModelScope.launch {
            repeat(duration) {
                delay(1000)
                _resendSecondsRemaining.value = duration - it - 1
            }
        }
    }

    fun stopResendTimer() {
        resendTimerJob?.cancel()
        resendTimerJob = null
        _resendSecondsRemaining.value = 0
    }

    fun dismissPinEntry() {
        _showPinEntry.value = false
        _pinCode.value = ""
        _pinError.value = null
        stopResendTimer()
    }

    fun resetFeedback() {
        _errorMessage.value = null
        _successMessage.value = null
        _needsAcceptance.value = false
    }

    fun pushBlockingError(message: String) {
        _errorMessage.value = message
    }

    fun resetMessages() {
        resetFeedback()
    }

    private fun validateFields(): Boolean {
        if (_fullName.value.trim().isEmpty()) {
            _errorMessage.value = "Le nom complet est requis."
            return false
        }

        if (!isValidFullName(_fullName.value.trim())) {
            _errorMessage.value = "Le nom complet doit contenir au moins deux mots séparés par un espace."
            return false
        }

        if (!isValidEmail(_email.value)) {
            _errorMessage.value = "Adresse email invalide."
            return false
        }

        val phoneDigits = _phoneNumber.value.filter { it.isDigit() }
        if (phoneDigits.length < 6) {
            _errorMessage.value = "Le numéro de téléphone est requis (6 chiffres minimum)."
            return false
        }

        if (!isStrongPassword(_password.value)) {
            _errorMessage.value = "Le mot de passe doit contenir au moins 6 caractères, une majuscule et un caractère spécial."
            return false
        }

        if (_selectedGender.value == null) {
            _errorMessage.value = "Veuillez sélectionner un sexe."
            return false
        }

        return true
    }

    private fun isValidFullName(name: String): Boolean {
        val words = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        return words.size >= 2
    }

    private fun isStrongPassword(password: String): Boolean {
        if (password.length < 6) return false
        
        val hasUppercase = password.any { it.isUpperCase() }
        val hasSpecialChar = password.any { 
            it in "!@#$%^&*()_+-=[]{}|;:'\",.<>?/" 
        }
        
        return hasUppercase && hasSpecialChar
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return pattern.matches(email)
    }

    private fun clearSensitiveFields() {
        _password.value = ""
        _phoneNumber.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        stopResendTimer()
    }
}

