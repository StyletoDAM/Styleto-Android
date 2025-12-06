package tn.esprit.labasniandroid.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.repositories.SubscriptionRepository
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.TokenManager

/**
 * Modal plein écran affichant les détails du pack Premium
 * Avec sélecteur Mensuel/Annuel et paiement via page web Stripe
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumPackDetails(
    onDismiss: () -> Unit,
    onSubscribe: (isAnnual: Boolean) -> Unit = {},
    onSubscriptionSuccess: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val subscriptionRepository = remember { SubscriptionRepository() }

    // États
    var isAnnual by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf<String?>(null) }
    var currentPlan by remember { mutableStateOf<String?>(null) }
    var isLoadingPlan by remember { mutableStateOf(true) }
    val hasPremiumPlan = remember(currentPlan) { currentPlan == "PREMIUM" }

    // États pour WebView
    var showPaymentWebView by remember { mutableStateOf(false) }
    var paymentUrl by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }

    // Récupérer le plan actuel
    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context)
        if (token != null) {
            scope.launch {
                subscriptionRepository.getMyStats(token).fold(
                    onSuccess = { stats ->
                        currentPlan = stats.plan
                        isLoadingPlan = false
                    },
                    onFailure = {
                        currentPlan = "FREE"
                        isLoadingPlan = false
                    }
                )
            }
        } else {
            currentPlan = "FREE"
            isLoadingPlan = false
        }
    }

    // Couleurs dynamiques
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()

    // Prix
    val monthlyPrice = 9.99
    val annualPrice = 99.0
    val annualPricePerMonth = annualPrice / 12
    val discountPercentage = ((monthlyPrice * 12 - annualPrice) / (monthlyPrice * 12) * 100).toInt()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeBackground,
        modifier = modifier.fillMaxSize(),
        dragHandle = null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (showPaymentWebView) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header pour le WebView avec bouton retour
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showPaymentWebView = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = themePrimary
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "Secure Payment",
                            style = MaterialTheme.typography.titleLarge,
                            color = themePrimary
                        )
                    }
                    PaymentWebView(
                        url = paymentUrl,
                        token = token,
                        subscriptionRepository = subscriptionRepository,
                        onSuccess = {
                            showPaymentWebView = false
                            onSubscriptionSuccess?.invoke()
                            onDismiss()
                        },
                        onCancel = { showPaymentWebView = false },
                        onError = { error -> paymentError = error }
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header fixe
                    PremiumPackHeader(
                        onBackClick = onDismiss,
                        themePrimary = themePrimary,
                        themeText = themeText
                    )

                    // Header visuel
                    PremiumPackVisualHeader(
                        themePrimary = themePrimary,
                        themeText = themeText,
                        themeSecondaryText = themeSecondaryText
                    )

                    // Carte scrollable
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
                                PremiumPackPriceSelectorContent(
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
                                HorizontalDivider(
                                    color = themeSecondaryText.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )
                                PremiumPackFeaturesListContent(
                                    themePrimary = themePrimary,
                                    themeText = themeText,
                                    themeSecondaryText = themeSecondaryText
                                )
                                HorizontalDivider(
                                    color = themeSecondaryText.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )
                                PremiumPackInfoBlockContent(
                                    themeText = themeText,
                                    themeSecondaryText = themeSecondaryText
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                // Bouton CTA sticky
                PremiumPackCTAButton(
                    isProcessing = isProcessing,
                    hasPremiumPlan = hasPremiumPlan,
                    isLoadingPlan = isLoadingPlan,
                    onClick = {
                        if (hasPremiumPlan) return@PremiumPackCTAButton
                        val currentToken = TokenManager.getToken(context)
                        if (currentToken == null) {
                            paymentError = "Vous devez être connecté pour souscrire"
                            return@PremiumPackCTAButton
                        }
                        token = currentToken
                        isProcessing = true
                        paymentError = null
                        scope.launch {
                            // ✨ Créer une Checkout Session Stripe
                            val interval = if (isAnnual) "year" else "month"
                            val result = subscriptionRepository.createCheckoutSession(
                                token = currentToken,
                                plan = "PREMIUM",
                                interval = interval
                            )
                            result.onSuccess { checkoutResponse ->
                                isProcessing = false
                                // ✨ Afficher le WebView au lieu d'ouvrir le navigateur
                                paymentUrl = checkoutResponse.checkoutUrl
                                showPaymentWebView = true
                            }.onFailure { error ->
                                paymentError = "Erreur: ${error.message ?: "Impossible de créer le paiement"}"
                                isProcessing = false
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
    }

    // Dialog d'erreur
    paymentError?.let { error ->
        AlertDialog(
            onDismissRequest = { paymentError = null },
            title = { Text(text = "Erreur de paiement", fontWeight = FontWeight.Bold) },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { paymentError = null }) {
                    Text(text = "OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// Les autres composables (PremiumPackHeader, PremiumPackVisualHeader, etc.)
// restent identiques au code original - je les ai omis pour la brièveté

// MARK: - Header avec flèche retour et titre
@Composable
private fun PremiumPackHeader(
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
            text = "Pack Details",
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

// MARK: - Header visuel avec icône couronne
@Composable
private fun PremiumPackVisualHeader(
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
        // Icône couronne dans un cercle rose
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
                imageVector = Icons.Rounded.Star,
                contentDescription = "Premium",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Premium Access",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            ),
            color = themePrimary
        )

        Text(
            text = "For fashion enthusiasts who want to go further",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                lineHeight = 20.sp
            ),
            color = themeSecondaryText,
            textAlign = TextAlign.Center
        )
    }
}

// MARK: - Contenu du sélecteur Mensuel/Annuel avec prix (sans Card, pour être intégré dans la grande card)
@Composable
private fun PremiumPackPriceSelectorContent(
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

                Text(
                    text = if (isAnnual) {
                        "$annualPrice DT / an"
                    } else {
                        "par mois"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    color = themeSecondaryText
                )
            }
    }
}

// MARK: - Contenu de la liste des avantages (sans Card, pour être intégré dans la grande card)
@Composable
private fun PremiumPackFeaturesListContent(
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
            icon = Icons.Rounded.Person,
            title = "Avatar 3D Premium",
            description = "Personnalisez votre avatar virtuel"
        ),
        FeatureItem(
            icon = Icons.Rounded.Help,
            title = "Support prioritaire",
            description = "Réponses rapides à vos questions"
        ),
        FeatureItem(
            icon = Icons.Rounded.Star,
            title = "Fonctionnalités exclusives",
            description = "Accédez aux nouvelles features en premier"
        ),
        FeatureItem(
            icon = Icons.Rounded.Store,
            title = "Vente limitée",
            description = "Jusqu'à 3 articles par mois",
            isLimited = true
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
                PremiumFeatureRow(
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
private fun PremiumFeatureRow(
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = themeText
                )

                if (feature.isLimited) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "Limité",
                        tint = themeSecondaryText.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

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

// MARK: - Contenu du bloc "Bon à savoir" (sans Card, pour être intégré dans la grande card)
@Composable
private fun PremiumPackInfoBlockContent(
    themeText: Color,
    themeSecondaryText: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Good to know",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            color = themeText,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        val infoItems = listOf(
            "Cancel anytime, no commitment",
            "Change pack whenever you want",
            "Secure payment",
            "Customer support available 24/7"
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
private fun PremiumPackCTAButton(
    isProcessing: Boolean,
    hasPremiumPlan: Boolean = false,
    isLoadingPlan: Boolean = false,
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
                enabled = !isProcessing && !hasPremiumPlan && !isLoadingPlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = if (hasPremiumPlan) Color.Gray.copy(alpha = 0.3f) else themePrimary.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                when {
                    isProcessing -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    hasPremiumPlan -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "You already have Premium Access",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = "Upgrade to Premium Access",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Safe area padding pour les appareils avec notch
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

