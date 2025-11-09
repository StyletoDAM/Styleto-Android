package tn.esprit.labasniandroid.screens

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
import tn.esprit.labasniandroid.models.User
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.viewmodels.SignupViewModel

@Composable
fun LabasniSignupScreen(
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
    val canResendCode by viewModel.canResendCode.collectAsState()

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
                    text = "Créer un compte",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PinkPrimary
                    )
                )
                Text(
                    text = "Rejoignez Labasni et découvrez votre style",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TealAccent,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Nom complet",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TealAccent
                )
                LabasniOutlinedField(
                    value = fullName,
                    onValueChange = { viewModel.setFullName(it) },
                    placeholder = "Entrez votre nom",
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

                Text(
                    text = "Numéro de téléphone",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TealAccent
                )
                PhoneInputField(
                    number = phoneNumber,
                    onNumberChange = { viewModel.setPhoneNumber(it) }
                )

                Text(
                    text = "Mot de passe",
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
                    text = "Sexe",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TealAccent
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Femme", "Homme").forEach { option ->
                        val genderValue = when (option) {
                            "Femme" -> User.Gender.FEMALE
                            "Homme" -> User.Gender.MALE
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
                    text = if (isLoading) "" else "Créer mon compte",
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
                    text = "Déjà un compte ? Se connecter",
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
                    viewModel.pushBlockingError("Vous devez accepter les conditions pour créer un compte.")
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
                text = "Conditions d'utilisation",
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
                    text = "En créant un compte Labasni, vous acceptez :",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PinkPrimary
                    )
                )
                TermsBullet("Le traitement de vos données afin de personnaliser vos recommandations de style.")
                TermsBullet("La réception éventuelle de notifications liées à votre activité et à nos nouveautés.")
                TermsBullet("L'utilisation sécurisée de vos informations conformément à notre politique de confidentialité.")
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
                    text = "Accepter et créer mon compte",
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
                    text = "Refuser",
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
