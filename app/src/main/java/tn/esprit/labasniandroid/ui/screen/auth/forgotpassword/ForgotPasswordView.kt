package tn.esprit.labasniandroid.ui.screen.auth.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.ui.screen.auth.forgotpassword.ForgotPasswordViewModel
import tn.esprit.labasniandroid.ui.components.LabasniTopBar
import tn.esprit.labasniandroid.ui.components.LabasniOutlinedField
import tn.esprit.labasniandroid.ui.components.LabasniPillButton
import tn.esprit.labasniandroid.ui.components.OtpEntryDialog
import tn.esprit.labasniandroid.ui.components.ResetPasswordDialog

@Composable
fun ForgotPasswordView(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val email by viewModel.email.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val showOtpDialog by viewModel.showOtpEntryDialog.collectAsState()
    val showResetDialog by viewModel.showResetPasswordDialog.collectAsState()
    val maskedPhoneNumber by viewModel.maskedPhoneNumber.collectAsState()
    val otpCode by viewModel.otpCode.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val resendSecondsRemaining by viewModel.resendSecondsRemaining.collectAsState()
    val canResend = viewModel.canResendCode

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Afficher les messages de succès/erreur
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // Naviguer vers login après réinitialisation réussie
    LaunchedEffect(successMessage) {
        if (successMessage != null && !showOtpDialog && !showResetDialog) {
            // Attendre un peu avant de naviguer
            kotlinx.coroutines.delay(1500)
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PinkPrimary.copy(alpha = 0.15f), Color.White)
                )
            )
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LabasniTopBar(onBack = onBack)

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(TealAccent.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = null,
                    tint = TealAccent,
                    modifier = Modifier.size(52.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Mot de passe oublié ?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PinkPrimary
                    )
                )
                Text(
                    text = "Entrez votre email et nous vous enverrons un code\npour réinitialiser votre mot de passe",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TealAccent,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email de récupération",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TealAccent
                )
                LabasniOutlinedField(
                    value = email,
                    onValueChange = { viewModel.setEmail(it) },
                    placeholder = "votre@email.com",
                    leading = {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = null,
                            tint = TealAccent
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
            LabasniPillButton(
                    text = if (isLoading) "" else "Envoyer le code de réinitialisation",
                    onClick = { viewModel.requestOtp() },
                modifier = Modifier.fillMaxWidth(),
                background = PinkPrimary,
                contentColor = Color.White,
                    enabled = email.isNotBlank() && !isLoading
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Dialog OTP Entry
    if (showOtpDialog && maskedPhoneNumber != null) {
        OtpEntryDialog(
            maskedPhone = maskedPhoneNumber!!,
            code = otpCode,
            onCodeChange = { viewModel.setOtpCode(it) },
            errorMessage = errorMessage,
            isLoading = isLoading,
            resendSecondsRemaining = resendSecondsRemaining,
            canResend = canResend,
            onVerify = { viewModel.verifyOtp() },
            onResend = { viewModel.resendOtp() },
            onDismiss = { viewModel.dismissOtpDialog() }
        )
    }

    // Dialog Reset Password
    if (showResetDialog) {
        ResetPasswordDialog(
            newPassword = newPassword,
            onNewPasswordChange = { viewModel.setNewPassword(it) },
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = { viewModel.setConfirmPassword(it) },
            errorMessage = errorMessage,
            isLoading = isLoading,
            onReset = { viewModel.resetPassword() },
            onDismiss = { viewModel.dismissResetPasswordDialog() }
        )
    }
}
