package tn.esprit.labasniandroid.ui.screen.auth.login

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.R
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.ui.components.LabasniOutlinedField
import tn.esprit.labasniandroid.ui.components.LabasniPillButton
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.BuildConfig

@Composable
fun LoginView(
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
    
    // ✨ NOUVEAU : État pour afficher/masquer le mot de passe (comme iOS)
    var passwordVisible by remember { mutableStateOf(false) }

    // Configuration Google Sign-In
    // ✨ NOUVEAU : Client ID depuis BuildConfig (depuis local.properties)
    // Ajoutez GOOGLE_CLIENT_ID dans local.properties avec votre Client ID Android
    // Pour Android, vous devez créer un Client ID Android dans Google Cloud Console
    // et ajouter le SHA-1 de votre clé de signature
    val googleClientId = BuildConfig.GOOGLE_CLIENT_ID.ifBlank { 
        // Fallback vers le Client ID iOS si non configuré (pour tests)
        "654276245605-bj14tf7v33v9cucd6cgq99d9jcfr5mud.apps.googleusercontent.com"
    }
    
    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleClientId) // ✨ NOUVEAU : Client ID depuis BuildConfig
            .requestId() // garantit un identifiant Google stable pour lier un compte existant
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
                    handleGoogleSignInResult(task, viewModel, context, scope, snackbarHostState)
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Error processing result", e)
                    scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Error processing Google sign-in: ${e.message}",
                        duration = SnackbarDuration.Short
                    )
                    }
                }
            } ?: run {
                Log.w("GoogleSignIn", "No data received from Google")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "No data received from Google.",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } else {
            // User cancelled or an error occurred
            Log.w("GoogleSignIn", "Sign-in failed or cancelled. Result code: ${result.resultCode}")
            
            // Check if it's a cancellation or an error
            val errorMessage = when (result.resultCode) {
                Activity.RESULT_CANCELED -> 
                    "Google sign-in cancelled. If the problem persists, check your Google Cloud Console configuration (SHA-1 and Client ID)."
                else -> 
                    "Google sign-in failed (Code: ${result.resultCode}). Check your Google Cloud Console configuration:\n" +
                    "1. Create an OAuth 2.0 Client ID for Android\n" +
                    "2. Add the SHA-1 of your signing key\n" +
                    "3. Verify that the package name matches"
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
            // Le token est déjà sauvegardé par AuthService
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
                    text = "Styleto",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PinkPrimary
                    )
                )
                Text(
                    text = "Welcome! Sign in",
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
                    placeholder = "your@email.com",
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
                    text = "Password",
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
                    trailing = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = if (isDark) colorScheme.onSurface.copy(alpha = 0.7f) else TealAccent
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
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
                            text = "Create an account",
                            color = if (isDark) colorScheme.primary else TealAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    TextButton(
                        onClick = onForgotPassword
                    ) {
                        Text(
                            text = "Forgot password?",
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
                    text = if (isLoading) "" else "Sign in",
                    onClick = { viewModel.signin(context) },
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

            // Separator OR
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
                    text = "OR",
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
    // ✨ NOUVEAU : Utiliser OutlinedButton pour une meilleure visibilité (comme iOS)
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
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
                modifier = Modifier.padding(end = 8.dp),
                fontSize = 18.sp
            )
            Text(
                text = "Continue with Google",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TealAccent
                )
            )
        }
    }
}

private fun handleGoogleSignInResult(
    task: Task<GoogleSignInAccount>,
    viewModel: LoginViewModel,
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState: androidx.compose.material3.SnackbarHostState
) {
    try {
        Log.d("GoogleSignIn", "Processing Google Sign-In result")
        val account = task.getResult(ApiException::class.java)
        Log.d("GoogleSignIn", "Account retrieved: ${account?.email}")
        account?.let {
            val primaryGoogleId = it.id.orEmpty()
            val fallbackIdToken = it.idToken.orEmpty()
            val fullName = it.displayName ?: ""
            val email = it.email ?: ""
            val profilePicture = it.photoUrl?.toString()

            val googleId = when {
                primaryGoogleId.isNotBlank() -> primaryGoogleId
                fallbackIdToken.isNotBlank() -> fallbackIdToken
                email.isNotBlank() -> "email:${email.lowercase()}"
                else -> ""
            }

            // ✨ NOUVEAU : Normaliser l'email en minuscules (comme iOS) pour que le backend trouve le profil existant
            val normalizedEmail = email.lowercase().trim()

            if (googleId.isNotEmpty() && normalizedEmail.isNotEmpty()) {
                Log.d("GoogleSignIn", "Calling signInWithGoogle with ID: $googleId, Email: $normalizedEmail")
                viewModel.signInWithGoogle(
                    context = context,
                    googleId = googleId,
                    fullName = fullName,
                    email = normalizedEmail, // ✨ Utiliser l'email normalisé
                    profilePicture = profilePicture
                )
            } else {
                Log.w("GoogleSignIn", "Missing Google ID or email. ID: $googleId, Email: $email")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Unable to retrieve Google information (missing ID or email).",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } ?: run {
            scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "No Google account found.",
                        duration = SnackbarDuration.Short
                    )
            }
        }
    } catch (e: ApiException) {
        Log.e("GoogleSignIn", "ApiException: Status code ${e.statusCode}, Message: ${e.message}", e)
        val errorMessage = when (e.statusCode) {
            12501 -> // SIGN_IN_CANCELLED
                "Google sign-in cancelled."
            7 -> // NETWORK_ERROR
                "Network error during Google sign-in."
            8 -> // INTERNAL_ERROR
                "Internal Google Sign-In error."
            5 -> // INVALID_ACCOUNT
                "Invalid Google account."
            4 -> // SIGN_IN_REQUIRED
                "Google sign-in required. Please try again."
            17 -> // API_NOT_CONNECTED
                "Google API not connected. Check your configuration."
            10 -> // DEVELOPER_ERROR
                "Google Sign-In configuration error. Check SHA-1 and Client ID."
            else -> "Error during Google sign-in: ${e.message ?: "Error code: ${e.statusCode}"}"
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
                        message = "Unexpected error: ${e.message}",
                        duration = SnackbarDuration.Short
                    )
        }
    }
}

