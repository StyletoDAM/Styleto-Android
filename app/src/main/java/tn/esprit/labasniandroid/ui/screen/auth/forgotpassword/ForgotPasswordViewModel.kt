package tn.esprit.labasniandroid.ui.screen.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import tn.esprit.labasniandroid.models.Responses
import tn.esprit.labasniandroid.models.services.AuthService

class ForgotPasswordViewModel(
    private val authService: AuthService = AuthService()
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

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

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _resendSecondsRemaining = MutableStateFlow(0)
    val resendSecondsRemaining: StateFlow<Int> = _resendSecondsRemaining.asStateFlow()

    private var resetToken: String? = null
    private var resendJob: kotlinx.coroutines.Job? = null

    val canResendCode: Boolean
        get() = _resendSecondsRemaining.value == 0 && !_isLoading.value

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setOtpCode(value: String) {
        _otpCode.value = value.filter { it.isDigit() }.take(6)
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

            authService.requestPasswordReset(
                email = _email.value.lowercase().trim()
            ).fold(
                onSuccess = { response ->
                    _maskedPhoneNumber.value = response.maskedPhoneNumber
                    _successMessage.value = response.message
                    _showOtpEntryDialog.value = true
                    _otpCode.value = ""
                    startResendCountdown()
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isLoading.value = false
        }
    }

    fun resendOtp() {
        viewModelScope.launch {
            if (!canResendCode) {
                return@launch
            }
            requestOtp()
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

            authService.verifyOtpCode(
                email = _email.value.lowercase().trim(),
                code = _otpCode.value
            ).fold(
                onSuccess = { response ->
                    resetToken = response.resetToken
                    _successMessage.value = response.message
                    _showOtpEntryDialog.value = false
                    _showResetPasswordDialog.value = true
                    stopResendCountdown()
                    resetResendCountdown()
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isLoading.value = false
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            clearFlashMessages()

            if (_newPassword.value.length < 6) {
                _errorMessage.value = "Le mot de passe doit contenir au moins 6 caractères."
                return@launch
            }

            if (_newPassword.value != _confirmPassword.value) {
                _errorMessage.value = "Les mots de passe ne correspondent pas."
                return@launch
            }

            val token = resetToken
            if (token == null) {
                _errorMessage.value = "Token manquant. Veuillez recommencer le processus."
                return@launch
            }

            _isLoading.value = true

            authService.resetPassword(
                resetToken = token,
                newPassword = _newPassword.value
            ).fold(
                onSuccess = { response ->
                    _successMessage.value = response.message
                    _showResetPasswordDialog.value = false
                    clearSensitiveFields()
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _isLoading.value = false
        }
    }

    fun dismissOtpDialog() {
        _showOtpEntryDialog.value = false
        _otpCode.value = ""
    }

    fun dismissResetPasswordDialog() {
        _showResetPasswordDialog.value = false
        _newPassword.value = ""
        _confirmPassword.value = ""
    }

    private fun clearFlashMessages() {
        _errorMessage.value = null
        _successMessage.value = null
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
        _otpCode.value = ""
        _newPassword.value = ""
        _confirmPassword.value = ""
        resetToken = null
    }
}

