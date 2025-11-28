package tn.esprit.labasniandroid.ui.screen.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.ui.screen.profile.ProfileViewModel
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.PaymentService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceTopUpSheet(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel,
    token: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    
    // États
    var selectedAmount by remember { mutableDoubleStateOf(0.0) }
    var customAmount by remember { mutableStateOf("") }
    var isCustomSelected by remember { mutableStateOf(false) }
    
    // Variable pour stocker le clientSecret à présenter
    var pendingClientSecret by remember { mutableStateOf<String?>(null) }
    
    // État pour les erreurs de paiement
    var paymentError by remember { mutableStateOf<String?>(null) }
    
    // Montants prédéfinis (comme dans iOS)
    val presetAmounts = listOf(50.0, 100.0, 200.0, 500.0, 1000.0)
    
    // Couleurs dynamiques
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()
    
    // États du ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val isProcessingPayment by viewModel.isProcessingPayment.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    // Présenter PaymentSheet quand le clientSecret est disponible
    LaunchedEffect(pendingClientSecret) {
        if (pendingClientSecret != null && activity != null) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                try {
                    PaymentService.presentPaymentSheet(
                        activity = activity,
                        clientSecret = pendingClientSecret!!,
                        onResult = { paymentResult ->
                            when (paymentResult) {
                                is PaymentSheetResult.Completed -> {
                                    scope.launch {
                                        // Paiement réussi, confirmer le top-up avec le backend
                                        viewModel.confirmTopUpWithBackend(token)
                                    }
                                }
                                is PaymentSheetResult.Canceled -> {
                                    viewModel.resetPaymentState()
                                    // Ne pas afficher d'erreur pour une annulation
                                }
                                is PaymentSheetResult.Failed -> {
                                    val errorMsg = paymentResult.error?.message ?: "Erreur inconnue"
                                    paymentError = "Erreur de paiement: $errorMsg"
                                    viewModel.resetPaymentState()
                                }
                            }
                            pendingClientSecret = null
                        }
                    )
                } catch (e: Exception) {
                    paymentError = "Erreur: ${e.message ?: "Impossible d'ouvrir le paiement"}"
                    pendingClientSecret = null
                }
            }
        }
    }
    
    // Gérer les messages de succès/erreur
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            onDismiss()
            viewModel.clearMessages()
        }
    }
    
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // L'erreur sera affichée dans l'AlertDialog
        }
    }
    
    // Focus automatique sur le champ custom amount quand "Other" est sélectionné
    LaunchedEffect(isCustomSelected) {
        if (isCustomSelected) {
            kotlinx.coroutines.delay(100) // Petit délai pour l'animation
            focusRequester.requestFocus()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = themeBackground,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding() // Gère automatiquement le clavier
                .verticalScroll(scrollState) // Permet le défilement
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header avec bouton de fermeture
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Up Balance",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = themeTeal
                )
                
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                themeCard,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = themeText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
            }
            
            // Description
            Text(
                text = "Choose an amount to add to your balance",
                style = MaterialTheme.typography.bodyMedium,
                color = themeSecondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Grille des montants prédéfinis + bouton "Other"
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(presetAmounts) { amount ->
                    AmountButton(
                        amount = amount,
                        isSelected = selectedAmount == amount && !isCustomSelected,
                        onClick = {
                            selectedAmount = amount
                            isCustomSelected = false
                            customAmount = ""
                        },
                        themeTeal = themeTeal,
                        themeCard = themeCard
                    )
                }
                
                // Bouton "Other" (comme iOS)
                item {
                    OtherButton(
                        isSelected = isCustomSelected,
                        onClick = {
                            // Animation et état immédiat
                            selectedAmount = 0.0
                            customAmount = ""
                            isCustomSelected = true
                        },
                        themePrimary = themePrimary,
                        themeTeal = themeTeal,
                        themeCard = themeCard
                    )
                }
            }
            
            // Champ montant personnalisé (seulement si "Other" est sélectionné, comme iOS)
            if (isCustomSelected) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Custom Amount",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = themeText
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customAmount,
                            onValueChange = { newValue ->
                                // Permettre seulement les chiffres et un point décimal
                                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    customAmount = newValue
                                }
                            },
                            placeholder = { Text("Enter amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeTeal,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = themeTeal,
                                focusedContainerColor = themeBackground,
                                unfocusedContainerColor = themeBackground
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .background(themeBackground, RoundedCornerShape(16.dp))
                        )
                        
                        Text(
                            text = "TND",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = themeText
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bouton Cancel
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = themeText
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(themeCard, themeCard))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                
                // Bouton Top Up (avec Stripe)
                Button(
                    onClick = {
                        val finalAmount = if (isCustomSelected && customAmount.isNotEmpty()) {
                            customAmount.replace(",", ".").toDoubleOrNull() ?: 0.0
                        } else {
                            selectedAmount
                        }
                        
                        if (finalAmount > 0 && activity != null) {
                            scope.launch {
                                // Créer le Payment Intent via Stripe
                                val result = viewModel.initiateTopUp(token, finalAmount)
                                result.onSuccess { clientSecret ->
                                    // Stocker le clientSecret pour que LaunchedEffect le présente
                                    pendingClientSecret = clientSecret
                                }.onFailure { error ->
                                    paymentError = "Erreur: ${error.message ?: "Impossible de créer le paiement"}"
                                }
                            }
                        }
                    },
                    enabled = !isLoading && !isProcessingPayment && (
                        (isCustomSelected && customAmount.isNotEmpty() && (customAmount.replace(",", ".").toDoubleOrNull() ?: 0.0) > 0) ||
                        (!isCustomSelected && selectedAmount > 0)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(themePrimary, themeTeal)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading || isProcessingPayment) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Continue",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // AlertDialog pour les erreurs du ViewModel
    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = { 
                Text(
                    text = "Erreur",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearMessages() }) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
    
    // AlertDialog pour les erreurs de paiement
    paymentError?.let { error ->
        AlertDialog(
            onDismissRequest = { paymentError = null },
            title = { 
                Text(
                    text = "Erreur de paiement",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { paymentError = null }) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

@Composable
private fun AmountButton(
    amount: Double,
    isSelected: Boolean,
    onClick: () -> Unit,
    themeTeal: Color,
    themeCard: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) themeTeal else themeCard
            )
            .border(
                width = 2.dp,
                color = if (isSelected) themeTeal else themeTeal.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${amount.toInt()}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected) Color.White else themeTeal
            )
            
            Text(
                text = "TND",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = if (isSelected) Color.White else themeTeal.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun OtherButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    themePrimary: Color,
    themeTeal: Color,
    themeCard: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) themePrimary else themeCard
            )
            .border(
                width = 2.dp,
                color = if (isSelected) themePrimary else themeTeal.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Other",
                tint = if (isSelected) Color.White else themePrimary,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = "Other",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isSelected) Color.White else themePrimary
            )
        }
    }
}
