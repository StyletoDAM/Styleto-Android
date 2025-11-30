package tn.esprit.labasniandroid.ui.screen.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.ui.screen.auth.signup.SignupViewModel
import tn.esprit.labasniandroid.ui.components.LabasniGradientBackground
import tn.esprit.labasniandroid.ui.components.LabasniTopBar
import tn.esprit.labasniandroid.ui.components.LabasniOutlinedField
import tn.esprit.labasniandroid.ui.components.LabasniPillButton
import tn.esprit.labasniandroid.ui.components.GenderChip
import tn.esprit.labasniandroid.ui.components.PhoneInputField
import tn.esprit.labasniandroid.ui.components.PinEntryDialog

@Composable
fun SignupView(
    onBack: () -> Unit,
    onAccountCreated: () -> Unit,
    viewModel: SignupViewModel = viewModel()
) {
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val selectedGender by viewModel.selectedGender.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val needsAcceptance by viewModel.needsAcceptance.collectAsState()
    val signinResponse by viewModel.signinResponse.collectAsState()
    val showPinEntry by viewModel.showPinEntry.collectAsState()
    val pinCode by viewModel.pinCode.collectAsState()
    val pinError by viewModel.pinError.collectAsState()
    val navigateToLogin by viewModel.navigateToLogin.collectAsState()
    val resendSecondsRemaining by viewModel.resendSecondsRemaining.collectAsState()
    val canResendCode = viewModel.canResendCode

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle successful signup and auto-login
    LaunchedEffect(signinResponse) {
        val response = signinResponse
        if (response != null) {
            // Sauvegarder le token et l'ID utilisateur
            TokenManager.saveToken(context, response.accessToken)
            val userId = response.user.userId
            if (userId.isNotEmpty()) {
                TokenManager.saveUserId(context, userId)
            }
            // Rediriger vers le profil
            onAccountCreated()
        }
    }

    // Naviguer vers login après vérification email réussie
    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            // L'utilisateur doit se connecter manuellement après vérification
            onBack()
        }
    }

    // Handle successful signup (si pas de connexion automatique)
    LaunchedEffect(successMessage) {
        if (successMessage != null && signinResponse == null) {
            // Si l'inscription a réussi mais pas la connexion automatique
            // L'utilisateur devra se connecter manuellement
        }
    }

    // Handle error messages
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

    LabasniGradientBackground(
        topColor = PinkGradientTop.copy(alpha = 0.35f),
        bottomColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            LabasniTopBar(onBack = onBack)

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Create an account",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PinkPrimary
                    )
                )
                Text(
                    text = "Join Styleto and discover your style",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TealAccent,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Full name",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TealAccent
                )
                LabasniOutlinedField(
                    value = fullName,
                    onValueChange = { viewModel.setFullName(it) },
                    placeholder = "Enter your name",
                    leading = {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = TealAccent
                        )
                    }
                )

                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TealAccent
                )
                LabasniOutlinedField(
                    value = email,
                    onValueChange = { viewModel.setEmail(it) },
                    placeholder = "your@email.com",
                    leading = {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = null,
                            tint = TealAccent
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Text(
                    text = "Phone number",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TealAccent
                )
                PhoneInputField(
                    number = phoneNumber,
                    onNumberChange = { viewModel.setPhoneNumber(it) }
                )

                Text(
                    text = "Password",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TealAccent
                )
                LabasniOutlinedField(
                    value = password,
                    onValueChange = { viewModel.setPassword(it) },
                    placeholder = "••••••••",
                    leading = {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = TealAccent
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Gender",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TealAccent
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Female", "Male").forEach { option ->
                        val genderValue = when (option) {
                            "Female" -> User.Gender.FEMALE
                            "Male" -> User.Gender.MALE
                            else -> null
                        }
                        GenderChip(
                            title = option,
                            selected = selectedGender == genderValue,
                            onClick = { genderValue?.let { viewModel.selectGender(it) } }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                LabasniPillButton(
                    text = if (isLoading) "" else "Create my account",
                    onClick = { viewModel.attemptSignup() },
                    modifier = Modifier.fillMaxWidth(),
                    background = PinkPrimary,
                    contentColor = Color.White,
                    enabled = selectedGender != null &&
                        fullName.isNotBlank() &&
                        email.isNotBlank() &&
                        phoneNumber.isNotBlank() &&
                        password.isNotBlank() &&
                        !isLoading
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

            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Already have an account? Sign in",
                    color = TealAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Boîte de dialogue pour les termes et conditions
        if (needsAcceptance) {
            TermsAndConditionsDialog(
                onAccept = {
                    // Accepter les termes et effectuer l'inscription
                    viewModel.performSignupAfterAcceptance()
                },
                onDecline = {
                    // Refuser les termes
                    viewModel.pushBlockingError("You must accept the terms to create an account.")
                }
            )
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Dialog PIN Entry pour vérification email
    if (showPinEntry) {
        PinEntryDialog(
            email = email,
            pinCode = pinCode,
            onPinCodeChange = { viewModel.setPinCode(it) },
            errorMessage = pinError,
            isLoading = isLoading,
            resendSecondsRemaining = resendSecondsRemaining,
            canResend = canResendCode,
            onVerify = { viewModel.verifyPinCode() },
            onResend = { viewModel.resendVerificationCode() },
            onDismiss = { viewModel.dismissPinEntry() }
        )
    }
}

@Composable
private fun TermsAndConditionsDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDecline,
        title = {
            Text(
                text = "Terms of Use",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "By creating a Styleto account, you agree to:",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PinkPrimary
                    )
                )
                TermsBullet("The processing of your data to personalize your style recommendations.")
                TermsBullet("The possible receipt of notifications related to your activity and our news.")
                TermsBullet("The secure use of your information in accordance with our privacy policy.")
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp)),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = PinkPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Accept and create my account",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDecline,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Decline",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TealAccent
                    )
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun TermsBullet(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(PinkPrimary)
                .padding(top = 6.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TealAccent
            ),
                modifier = Modifier.weight(1f)
        )
    }
}
