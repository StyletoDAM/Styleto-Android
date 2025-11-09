package tn.esprit.labasniandroid.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.R
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.viewmodels.LoginViewModel

@Composable
fun LabasniLoginScreen(
    onBack: () -> Unit,
    onForgotPassword: () -> Unit,
    onCreateAccount: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val signinResponse by viewModel.signinResponse.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Configuration Google Sign-In
    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    // Launcher pour Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("GoogleSignIn", "Result code: ${result.resultCode}, Data: ${result.data != null}")
        
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleGoogleSignInResult(task, viewModel, scope, snackbarHostState)
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Error processing result", e)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Erreur lors du traitement de la connexion Google: ${e.message}",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            } ?: run {
                Log.w("GoogleSignIn", "No data received from Google")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Aucune donnée reçue de Google.",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } else {
            // L'utilisateur a annulé ou une erreur s'est produite
            Log.w("GoogleSignIn", "Sign-in failed or cancelled. Result code: ${result.resultCode}")
            
            // Vérifier si c'est une annulation ou une erreur
            val errorMessage = when (result.resultCode) {
                Activity.RESULT_CANCELED -> 
                    "Connexion Google annulée. Si le problème persiste, vérifiez la configuration Google Cloud Console (SHA-1 et Client ID)."
                else -> 
                    "Connexion Google échouée (Code: ${result.resultCode}). Vérifiez la configuration Google Cloud Console:\n" +
                    "1. Créez un OAuth 2.0 Client ID pour Android\n" +
                    "2. Ajoutez le SHA-1 de votre clé de signature\n" +
                    "3. Vérifiez que le package name correspond"
            }
            
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    // Handle successful login
    LaunchedEffect(signinResponse) {
        val response = signinResponse
        if (response != null) {
            // Sauvegarder le token et l'ID utilisateur
            TokenManager.saveToken(context, response.accessToken)
            val userId = response.user.userId
            if (userId.isNotEmpty()) {
                TokenManager.saveUserId(context, userId)
            }
            onLoginSuccess()
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

    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background != Color.White
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isDark) {
                    Modifier.background(colorScheme.background)
                } else {
                    Modifier.background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, AquaSoft.copy(alpha = 0.35f))
                )
                    )
                }
            )
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.logocercle),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(160.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Labasni",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PinkPrimary
                    )
                )
                Text(
                    text = "Bienvenue ! Connectez-vous",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDark) colorScheme.onBackground.copy(alpha = 0.8f) else TealAccent,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isDark) colorScheme.onBackground else TealAccent
                )
                LabasniOutlinedField(
                    value = email,
                    onValueChange = { viewModel.setEmail(it) },
                    placeholder = "votre@email.com",
                    leading = {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = null,
                            tint = if (isDark) colorScheme.onSurface.copy(alpha = 0.7f) else TealAccent
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Text(
                    text = "Mot de passe",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isDark) colorScheme.onBackground else TealAccent
                )
                LabasniOutlinedField(
                    value = password,
                    onValueChange = { viewModel.setPassword(it) },
                    placeholder = "••••••••",
                    leading = {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = if (isDark) colorScheme.onSurface.copy(alpha = 0.7f) else TealAccent
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onCreateAccount
                    ) {
                        Text(
                            text = "Créer un compte",
                            color = if (isDark) colorScheme.primary else TealAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    TextButton(
                        onClick = onForgotPassword
                    ) {
                        Text(
                            text = "Mot de passe oublié ?",
                            color = if (isDark) colorScheme.primary else TealAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                LabasniPillButton(
                    text = if (isLoading) "" else "Se connecter",
                    onClick = { viewModel.signin() },
                    modifier = Modifier.fillMaxWidth(),
                    background = PinkPrimary,
                    contentColor = Color.White,
                    enabled = !isLoading
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

            // Séparateur OU
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(TealAccent.copy(alpha = 0.3f))
                )
                Text(
                    text = "OU",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TealAccent.copy(alpha = 0.7f)
                    )
                )
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(TealAccent.copy(alpha = 0.3f))
                )
            }

            // Bouton OAuth Google
            GoogleSignInButton(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = TealAccent
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, TealAccent.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌐",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Continuer avec Google",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

private fun handleGoogleSignInResult(
    task: Task<GoogleSignInAccount>,
    viewModel: LoginViewModel,
    scope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState: androidx.compose.material3.SnackbarHostState
) {
    try {
        Log.d("GoogleSignIn", "Processing Google Sign-In result")
        val account = task.getResult(ApiException::class.java)
        Log.d("GoogleSignIn", "Account retrieved: ${account?.email}")
        account?.let {
            val googleId = it.id ?: ""
            val fullName = it.displayName ?: ""
            val email = it.email ?: ""
            val profilePicture = it.photoUrl?.toString()

            if (googleId.isNotEmpty() && email.isNotEmpty()) {
                Log.d("GoogleSignIn", "Calling signInWithGoogle with ID: $googleId, Email: $email")
                viewModel.signInWithGoogle(
                    googleId = googleId,
                    fullName = fullName,
                    email = email,
                    profilePicture = profilePicture
                )
            } else {
                Log.w("GoogleSignIn", "Missing Google ID or email. ID: $googleId, Email: $email")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Impossible de récupérer les informations Google (ID ou email manquant).",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } ?: run {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Aucun compte Google trouvé.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    } catch (e: ApiException) {
        Log.e("GoogleSignIn", "ApiException: Status code ${e.statusCode}, Message: ${e.message}", e)
        val errorMessage = when (e.statusCode) {
            12501 -> // SIGN_IN_CANCELLED
                "Connexion Google annulée."
            7 -> // NETWORK_ERROR
                "Erreur réseau lors de la connexion Google."
            8 -> // INTERNAL_ERROR
                "Erreur interne Google Sign-In."
            5 -> // INVALID_ACCOUNT
                "Compte Google invalide."
            4 -> // SIGN_IN_REQUIRED
                "Connexion Google requise. Veuillez réessayer."
            17 -> // API_NOT_CONNECTED
                "API Google non connectée. Vérifiez votre configuration."
            10 -> // DEVELOPER_ERROR
                "Erreur de configuration Google Sign-In. Vérifiez le SHA-1 et le Client ID."
            else -> "Erreur lors de la connexion Google: ${e.message ?: "Code d'erreur: ${e.statusCode}"}"
        }
        scope.launch {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short
            )
        }
    } catch (e: Exception) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "Erreur inattendue: ${e.message}",
                duration = SnackbarDuration.Short
            )
        }
    }
}
