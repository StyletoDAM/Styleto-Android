package tn.esprit.labasniandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent

/**
 * Dialog pour saisir le code OTP SMS lors de la réinitialisation de mot de passe
 */
@Composable
fun OtpEntryDialog(
    maskedPhone: String,
    code: String,
    onCodeChange: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    resendSecondsRemaining: Int,
    canResend: Boolean,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Vérifiez votre identité",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Un code à 6 chiffres a été envoyé au numéro $maskedPhone.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TealAccent,
                        textAlign = TextAlign.Center
                    )
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "000000",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 6.sp
                            ),
                            color = TealAccent.copy(alpha = 0.5f)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp,
                        textAlign = TextAlign.Center
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = PinkPrimary.copy(alpha = 0.7f),
                        cursorColor = PinkPrimary
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                TextButton(
                    onClick = onResend,
                    enabled = canResend && !isLoading
                ) {
                    Text(
                        text = if (canResend) {
                            "Renvoyer le code SMS"
                        } else {
                            "Renvoyer le code ($resendSecondsRemaining s)"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (canResend) PinkPrimary else Color.Gray
                        )
                    )
                }
            }
        },
        confirmButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                LabasniPillButton(
                    text = if (isLoading) "" else "Valider le code",
                    onClick = onVerify,
                    modifier = Modifier.fillMaxWidth(),
                    background = PinkPrimary,
                    contentColor = Color.White,
                    enabled = code.length == 6 && !isLoading
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
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Annuler",
                    color = TealAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Dialog pour définir un nouveau mot de passe après vérification OTP
 */
@Composable
fun ResetPasswordDialog(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Définir un nouveau mot de passe",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Veuillez entrer votre nouveau mot de passe.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TealAccent,
                        textAlign = TextAlign.Center
                    )
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Nouveau mot de passe",
                            color = TealAccent.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = TealAccent
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(
                                imageVector = if (showNewPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (showNewPassword) "Masquer" else "Afficher",
                                tint = PinkPrimary
                            )
                        }
                    },
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = PinkPrimary.copy(alpha = 0.8f),
                        cursorColor = PinkPrimary
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Confirmer le mot de passe",
                            color = TealAccent.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = TealAccent
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (showConfirmPassword) "Masquer" else "Afficher",
                                tint = PinkPrimary
                            )
                        }
                    },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = PinkPrimary.copy(alpha = 0.8f),
                        cursorColor = PinkPrimary
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        },
        confirmButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                LabasniPillButton(
                    text = if (isLoading) "" else "Réinitialiser le mot de passe",
                    onClick = onReset,
                    modifier = Modifier.fillMaxWidth(),
                    background = PinkPrimary,
                    contentColor = Color.White,
                    enabled = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && !isLoading
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
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Annuler",
                    color = TealAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Dialog pour saisir le code PIN email lors de l'inscription
 */
@Composable
fun PinEntryDialog(
    email: String,
    pinCode: String,
    onPinCodeChange: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    resendSecondsRemaining: Int,
    canResend: Boolean,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Vérification requise",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Un code à 6 chiffres a été envoyé à",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TealAccent,
                        textAlign = TextAlign.Center
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PinkPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinkPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pinCode,
                    onValueChange = onPinCodeChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "000000",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 6.sp
                            ),
                            color = TealAccent.copy(alpha = 0.5f)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp,
                        textAlign = TextAlign.Center
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = PinkPrimary.copy(alpha = 0.7f),
                        cursorColor = PinkPrimary
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                TextButton(
                    onClick = onResend,
                    enabled = canResend && !isLoading
                ) {
                    Text(
                        text = if (canResend) {
                            "Renvoyer le code"
                        } else {
                            "Renvoyer le code ($resendSecondsRemaining s)"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (canResend) PinkPrimary else Color.Gray
                        )
                    )
                }
            }
        },
        confirmButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                LabasniPillButton(
                    text = if (isLoading) "" else "Vérifier le code",
                    onClick = onVerify,
                    modifier = Modifier.fillMaxWidth(),
                    background = PinkPrimary,
                    contentColor = Color.White,
                    enabled = pinCode.length == 6 && !isLoading
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
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Annuler",
                    color = TealAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

