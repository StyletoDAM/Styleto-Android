package tn.esprit.labasniandroid.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.repositories.SubscriptionRepository
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.PaymentService
import tn.esprit.labasniandroid.utils.TokenManager

/**
 * Modal plein écran affichant les détails du pack Pro Seller
 * Avec sélecteur Mensuel/Annuel et liste des avantages
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProPackDetails(
    onDismiss: () -> Unit,
    onSubscribe: (isAnnual: Boolean) -> Unit = {},
    onSubscriptionSuccess: (() -> Unit)? = null, // Callback après achat réussi
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val scope = rememberCoroutineScope()
    val subscriptionRepository = remember { SubscriptionRepository() }
    
    // États
    var isAnnual by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Variable pour stocker le clientSecret à présenter
    var pendingClientSecret by remember { mutableStateOf<String?>(null) }
    
    // Couleurs dynamiques
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()
    val themeSoftPink = DynamicThemeColors.softPink(isMale)
    
    // Prix
    val monthlyPrice = 24.99
    val annualPrice = 249.0
    val annualPricePerMonth = annualPrice / 12
    val discountPercentage = ((monthlyPrice * 12 - annualPrice) / (monthlyPrice * 12) * 100).toInt()
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    
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
                                        // Paiement réussi, confirmer l'abonnement avec le backend
                                        val token = TokenManager.getToken(context)
                                        if (token != null) {
                                            val result = subscriptionRepository.upgradeSubscription(
                                                token = token,
                                                plan = "PRO_SELLER"
                                            )
                                            result.onSuccess {
                                                showSuccessDialog = true
                                                isProcessing = false
                                                // Appeler le callback pour rafraîchir les données après un court délai
                                                // pour laisser le backend se mettre à jour
                                                scope.launch {
                                                    kotlinx.coroutines.delay(800) // Délai avant de rafraîchir
                                                    onSubscriptionSuccess?.invoke()
                                                }
                                            }.onFailure { error ->
                                                paymentError = "Paiement réussi mais confirmation échouée: ${error.message}"
                                                isProcessing = false
                                            }
                                        } else {
                                            paymentError = "Token d'authentification manquant"
                                            isProcessing = false
                                        }
                                    }
                                }
                                is PaymentSheetResult.Canceled -> {
                                    isProcessing = false
                                    // Ne pas afficher d'erreur pour une annulation
                                }
                                is PaymentSheetResult.Failed -> {
                                    val errorMsg = paymentResult.error?.message ?: "Erreur inconnue"
                                    paymentError = "Erreur de paiement: $errorMsg"
                                    isProcessing = false
                                }
                            }
                            pendingClientSecret = null
                        }
                    )
                } catch (e: Exception) {
                    paymentError = "Erreur: ${e.message ?: "Impossible d'ouvrir le paiement"}"
                    pendingClientSecret = null
                    isProcessing = false
                }
            }
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeBackground,
        modifier = modifier.fillMaxSize(),
        dragHandle = null
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header fixe (non scrollable)
                ProPackHeader(
                    onBackClick = onDismiss,
                    themePrimary = themePrimary,
                    themeText = themeText
                )
                
                // Zone 1 - Header visuel (non scrollable)
                ProPackVisualHeader(
                    themePrimary = themePrimary,
                    themeText = themeText,
                    themeSecondaryText = themeSecondaryText
                )
                
                // Zone 2 & 3 combinées - Une seule carte scrollable avec sélecteur prix + avantages + info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = themeCard),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Sélecteur Mensuel/Annuel avec prix
                            ProPackPriceSelectorContent(
                                isAnnual = isAnnual,
                                onToggle = { isAnnual = it },
                                monthlyPrice = monthlyPrice,
                                annualPrice = annualPrice,
                                annualPricePerMonth = annualPricePerMonth,
                                discountPercentage = discountPercentage,
                                themeCard = themeCard,
                                themePrimary = themePrimary,
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText
                            )
                            
                            // Divider
                            HorizontalDivider(
                                color = themeSecondaryText.copy(alpha = 0.2f),
                                thickness = 1.dp
                            )
                            
                            // Liste des avantages
                            ProPackFeaturesListContent(
                                themePrimary = themePrimary,
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText
                            )
                            
                            // Divider
                            HorizontalDivider(
                                color = themeSecondaryText.copy(alpha = 0.2f),
                                thickness = 1.dp
                            )
                            
                            // Bloc "Bon à savoir"
                            ProPackInfoBlockContent(
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText
                            )
                        }
                    }
                    
                    // Padding en bas pour le bouton sticky
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
            
            // Zone 4 - Bouton CTA sticky (fixe en bas)
            ProPackCTAButton(
                isProcessing = isProcessing,
                onClick = {
                    val token = TokenManager.getToken(context)
                    if (token == null) {
                        paymentError = "Vous devez être connecté pour souscrire"
                        return@ProPackCTAButton
                    }
                    
                    if (activity != null) {
                        isProcessing = true
                        paymentError = null
                        
                        scope.launch {
                            // Calculer le montant selon mensuel/annuel
                            val amount = if (isAnnual) annualPrice else monthlyPrice
                            
                            // Créer le PaymentIntent via Stripe
                            val result = subscriptionRepository.createSubscriptionPaymentIntent(
                                token = token,
                                amount = amount,
                                currency = "usd"
                            )
                            
                            result.onSuccess { clientSecret ->
                                // Stocker le clientSecret pour que LaunchedEffect le présente
                                pendingClientSecret = clientSecret
                            }.onFailure { error ->
                                paymentError = "Erreur: ${error.message ?: "Impossible de créer le paiement"}"
                                isProcessing = false
                            }
                        }
                    }
                },
                themePrimary = themePrimary,
                themeBackground = themeBackground,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
    
    // Dialog de succès après paiement
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onDismiss()
            },
            title = {
                Text(
                    text = "✅ Abonnement réussi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                )
            },
            text = {
                Text(
                    text = "Votre abonnement Pro Seller a été activé avec succès !",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.Bold,
                        color = themePrimary
                    )
                }
            }
        )
    }
    
    // AlertDialog pour les erreurs
    paymentError?.let { error ->
        AlertDialog(
            onDismissRequest = { paymentError = null },
            title = {
                Text(
                    text = "Erreur",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(error)
            },
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

// MARK: - Header avec flèche retour et titre
@Composable
private fun ProPackHeader(
    onBackClick: () -> Unit,
    themePrimary: Color,
    themeText: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Retour",
                tint = themePrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Détails du Pack",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = themePrimary
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Espace pour équilibrer avec la flèche
        Spacer(modifier = Modifier.width(40.dp))
    }
}

// MARK: - Header visuel avec icône briefcase
@Composable
private fun ProPackVisualHeader(
    themePrimary: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icône briefcase dans un cercle (pink/bleu selon genre)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            themePrimary.copy(alpha = 0.9f),
                            themePrimary.copy(alpha = 0.7f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Store,
                contentDescription = "Pro Seller",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Text(
            text = "Pro Seller",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            ),
            color = themePrimary
        )
        
        Text(
            text = "For professional sellers who want to maximize their sales",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                lineHeight = 20.sp
            ),
            color = themeSecondaryText,
            textAlign = TextAlign.Center
        )
    }
}

// MARK: - Contenu du sélecteur Mensuel/Annuel avec prix (sans Card)
@Composable
private fun ProPackPriceSelectorContent(
    isAnnual: Boolean,
    onToggle: (Boolean) -> Unit,
    monthlyPrice: Double,
    annualPrice: Double,
    annualPricePerMonth: Double,
    discountPercentage: Int,
    themeCard: Color,
    themePrimary: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Segmented Control (Toggle Mensuel/Annuel)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(themeCard.copy(alpha = 0.5f)),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Bouton Mensuel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(
                        if (!isAnnual) themePrimary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggle(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mensuel",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = if (!isAnnual) Color.White else themeText
                )
            }
            
            // Bouton Annuel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(
                        if (isAnnual) themePrimary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggle(true) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Annuel",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        ),
                        color = if (isAnnual) Color.White else themeText
                    )
                    
                    if (isAnnual) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.3f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "-$discountPercentage%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
        
        // Prix affiché
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (isAnnual) {
                        String.format("%.0f", annualPricePerMonth)
                    } else {
                        String.format("%.2f", monthlyPrice)
                    },
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 48.sp
                    ),
                    color = themePrimary
                )
                
                Text(
                    text = "DT",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = themePrimary
                )
            }
            
            if (isAnnual) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$annualPrice DT / an",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        ),
                        color = themeSecondaryText
                    )
                    
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = themeSecondaryText
                    )
                    
                    Text(
                        text = "${String.format("%.2f", annualPricePerMonth)} DT/mois",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        ),
                        color = themeSecondaryText
                    )
                }
            } else {
                Text(
                    text = "par mois",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    color = themeSecondaryText
                )
            }
        }
    }
}

// MARK: - Contenu de la liste des avantages (sans Card)
@Composable
private fun ProPackFeaturesListContent(
    themePrimary: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    val features = listOf(
        FeatureItem(
            icon = Icons.Rounded.CameraAlt,
            title = "Scan illimité",
            description = "Détectez autant de vêtements que vous voulez"
        ),
        FeatureItem(
            icon = Icons.Rounded.AutoAwesome,
            title = "IA illimitée",
            description = "Suggestions de tenues sans limite"
        ),
        FeatureItem(
            icon = Icons.Rounded.Store,
            title = "Vente illimitée",
            description = "Sur la boutique Labas"
        ),
        FeatureItem(
            icon = Icons.Rounded.BarChart,
            title = "Statistiques avancées",
            description = "Suivez vos performances"
        ),
        FeatureItem(
            icon = Icons.Rounded.CheckCircle,
            title = "Badge vendeur professionnel",
            description = "Obtenez plus de visibilité"
        ),
        FeatureItem(
            icon = Icons.Rounded.Star,
            title = "Support VIP prioritaire",
            description = "Vous êtes notre priorité"
        )
    )
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Ce qui est inclus",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = themePrimary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        features.forEach { feature ->
            ProFeatureRow(
                feature = feature,
                themeText = themeText,
                themeSecondaryText = themeSecondaryText,
                themePrimary = themePrimary
            )
            
            if (feature != features.last()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = themeSecondaryText.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }
    }
}


@Composable
private fun ProFeatureRow(
    feature: FeatureItem,
    themeText: Color,
    themeSecondaryText: Color,
    themePrimary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icône
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(themePrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                tint = themePrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Texte
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = feature.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = themeText
            )
            
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                ),
                color = themeSecondaryText
            )
        }
        
        // Coche
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = "Inclus",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(24.dp)
        )
    }
}

// MARK: - Contenu du bloc "Bon à savoir" (sans Card)
@Composable
private fun ProPackInfoBlockContent(
    themeText: Color,
    themeSecondaryText: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = "Bon à savoir",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = themeText
            )
        }
        
        val infoItems = listOf(
            "Annulez à tout moment, sans engagement",
            "Changez de pack quand vous voulez",
            "Paiement sécurisé",
            "Support client disponible 7j/7"
        )
        
        infoItems.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = themeSecondaryText.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = themeSecondaryText
                )
            }
            
            if (item != infoItems.last()) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

// MARK: - Bouton CTA sticky
@Composable
private fun ProPackCTAButton(
    isProcessing: Boolean,
    onClick: () -> Unit,
    themePrimary: Color,
    themeBackground: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(0.dp),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ),
        color = themeBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(themeBackground)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onClick,
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = themePrimary.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "S'abonner à Pro Seller",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            
            // Safe area padding
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

