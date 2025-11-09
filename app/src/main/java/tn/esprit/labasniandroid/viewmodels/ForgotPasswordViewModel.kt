package tn.esprit.labasniandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.services.ForgotPasswordService

class ForgotPasswordViewModel(
    private val forgotPasswordService: ForgotPasswordService = ForgotPasswordService()
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _showOtpEntryDialog = MutableStateFlow(false)
    val showOtpEntryDialog: StateFlow<Boolean> = _showOtpEntryDialog.asStateFlow()

    private val _showResetPasswordDialog = MutableStateFlow(false)
    val showResetPasswordDialog: StateFlow<Boolean> = _showResetPasswordDialog.asStateFlow()

    private val _maskedPhoneNumber = MutableStateFlow<String?>(null)
    val maskedPhoneNumber: StateFlow<String?> = _maskedPhoneNumber.asStateFlow()

    private val _resetToken = MutableStateFlow<String?>(null)
    val resetToken: StateFlow<String?> = _resetToken.asStateFlow()

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

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setOtpCode(value: String) {
        // Limiter à 6 chiffres
        val digits = value.filter { it.isDigit() }
        _otpCode.value = digits.take(6)
    }

    fun setNewPassword(value: String) {
        _newPassword.value = value
    }

    fun setConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun requestOtp() {
        viewModelScope.launch {
            clearFlashMessages()

            if (!isValidEmail(_email.value)) {
                _errorMessage.value = "Adresse email invalide."
                return@launch
            }

            _isLoading.value = true

            forgotPasswordService.requestOtp(_email.value.lowercase().trim()).fold(
                onSuccess = { response ->
                    _maskedPhoneNumber.value = response.maskedPhoneNumber
                    _successMessage.value = response.message
                    _showOtpEntryDialog.value = true
                    _otpCode.value = ""
                    startResendCountdown()
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

    fun verifyOtp() {
        viewModelScope.launch {
            clearFlashMessages()

            if (_otpCode.value.length != 6) {
                _errorMessage.value = "Le code doit contenir 6 chiffres."
                return@launch
            }

            _isLoading.value = true

            forgotPasswordService.verifyOtp(
                email = _email.value.lowercase().trim(),
                code = _otpCode.value
            ).fold(
                onSuccess = { response ->
                    _resetToken.value = response.resetToken
                    _successMessage.value = response.message
                    _showOtpEntryDialog.value = false
                    _showResetPasswordDialog.value = true
                    stopResendTimer()
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

    fun resetPassword() {
        viewModelScope.launch {
            clearFlashMessages()

            val token = _resetToken.value
            if (token == null) {
                _errorMessage.value = "Veuillez valider le code avant de définir un nouveau mot de passe."
                return@launch
            }

            if (!isStrongPassword(_newPassword.value)) {
                _errorMessage.value = "Le mot de passe doit contenir au moins 6 caractères, une majuscule et un caractère spécial."
                return@launch
            }

            if (_newPassword.value != _confirmPassword.value) {
                _errorMessage.value = "Les mots de passe ne correspondent pas."
                return@launch
            }

            _isLoading.value = true

            forgotPasswordService.resetPassword(token, _newPassword.value).fold(
                onSuccess = { response ->
                    _successMessage.value = response.message
                    _showResetPasswordDialog.value = false
                    clearSensitive()
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

    fun resendOtp() {
        if (!canResendCode.value) return
        requestOtp()
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

    fun clearFlashMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun resetErrorMessages() {
        _errorMessage.value = null
    }

    fun clearSensitive() {
        _otpCode.value = ""
        _newPassword.value = ""
        _confirmPassword.value = ""
        _resetToken.value = null
    }

    fun resetAllFields() {
        _email.value = ""
        clearSensitive()
        _maskedPhoneNumber.value = null
        clearFlashMessages()
        stopResendTimer()
    }

    fun dismissOtpDialog() {
        _showOtpEntryDialog.value = false
        _otpCode.value = ""
        resetErrorMessages()
        stopResendTimer()
    }

    fun dismissResetPasswordDialog() {
        _showResetPasswordDialog.value = false
        _newPassword.value = ""
        _confirmPassword.value = ""
        resetErrorMessages()
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return pattern.matches(email)
    }

    private fun isStrongPassword(password: String): Boolean {
        if (password.length < 6) return false
        val hasUppercase = password.any { it.isUpperCase() }
        val hasSpecialChar = password.any {
            it in "!@#$%^&*()_+-=[]{}|;:'\",.<>?/"
        }
        return hasUppercase && hasSpecialChar
    }

    override fun onCleared() {
        super.onCleared()
        stopResendTimer()
    }
}

