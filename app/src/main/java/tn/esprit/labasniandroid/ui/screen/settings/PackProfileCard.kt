package tn.esprit.labasniandroid.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.repositories.SubscriptionRepository
import tn.esprit.labasniandroid.ui.components.ViewPackages
import tn.esprit.labasniandroid.ui.components.PremiumPackDetails
import tn.esprit.labasniandroid.ui.components.ProPackDetails
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.TokenManager

@Composable
fun PackProfileCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val subscriptionRepository = remember { SubscriptionRepository() }

    var showPlans by remember { mutableStateOf(false) }
    var showPremiumDetails by remember { mutableStateOf(false) }
    var showProDetails by remember { mutableStateOf(false) }
    var showManageSubscription by remember { mutableStateOf(false) }

    // État pour le pack actuel et stats
    var currentPlan by remember { mutableStateOf("FREE") }
    var isCanceled by remember { mutableStateOf(false) }
    var expirationMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // État pour les stats d'usage
    var clothesDetectionUsed by remember { mutableStateOf(0) }
    var clothesDetectionLimit by remember { mutableStateOf(5) }
    var outfitSuggestionsUsed by remember { mutableStateOf(0) }
    var outfitSuggestionsLimit by remember { mutableStateOf(3) }
    var itemsSoldUsed by remember { mutableStateOf(0) }
    var itemsSoldLimit by remember { mutableStateOf(3) }

    // État pour forcer le rafraîchissement
    var refreshKey by remember { mutableStateOf(0) }

    // Fonction pour charger/rafraîchir les données
    // Fonction pour charger/rafraîchir les données
    // Fonction pour charger/rafraîchir les données
    fun refreshSubscriptionData() {
        val token = TokenManager.getToken(context)
        if (token != null) {
            scope.launch {
                // Charger les stats d'usage
                subscriptionRepository.getMyStats(token).fold(
                    onSuccess = { stats ->
                        currentPlan = stats.plan

                        clothesDetectionUsed = stats.clothesDetection.used
                        clothesDetectionLimit = when (val limit: Any = stats.clothesDetection.limit) {
                            is Int -> limit
                            "unlimited" -> Int.MAX_VALUE
                            else -> 5
                        }

                        outfitSuggestionsUsed = stats.outfitSuggestions.used
                        outfitSuggestionsLimit = when (val limit: Any = stats.outfitSuggestions.limit) {
                            is Int -> limit
                            "unlimited" -> Int.MAX_VALUE
                            else -> 3
                        }

                        itemsSoldUsed = stats.storeSelling.used
                        itemsSoldLimit = when (val limit: Any = stats.storeSelling.limit) {
                            is Int -> limit
                            "unlimited" -> Int.MAX_VALUE
                            else -> 3
                        }
                    },
                    onFailure = {
                        currentPlan = "FREE"
                    }
                )

                // ✅ Charger les détails de l'abonnement pour avoir le status
                if (currentPlan != "FREE") {
                    subscriptionRepository.getMySubscription(token).fold(
                        onSuccess = { subscription ->
                            // ✅ Utiliser les propriétés calculées comme iOS
                            isCanceled = subscription.isCanceled
                            expirationMessage = subscription.expirationMessage
                        },
                        onFailure = {
                            isCanceled = false
                            expirationMessage = null
                        }
                    )
                }

                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    // Charger le pack actuel et les stats au démarrage et quand refreshKey change
    LaunchedEffect(refreshKey) {
        refreshSubscriptionData()
    }

    // Couleurs dynamiques
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()
    val themeSoftPink = DynamicThemeColors.softPink(isMale)

    // Fonction pour obtenir le nom du pack
    val packName = when (currentPlan) {
        "PREMIUM" -> "Premium"
        "PRO_SELLER" -> "Pro Seller"
        else -> "Free Pack"
    }

    // Icône selon le pack
    val packIcon = when (currentPlan) {
        "PREMIUM" -> Icons.Rounded.Star
        "PRO_SELLER" -> Icons.Rounded.Store
        else -> Icons.Rounded.StarBorder
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // MARK: - Card 1: Pack actuel + Usage
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.05f)
                ),
            colors = CardDefaults.cardColors(containerColor = themeCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = themePrimary
                        )
                    } else {
                        Icon(
                            imageVector = packIcon,
                            contentDescription = null,
                            tint = themePrimary.copy(alpha = 0.8f),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Your current pack",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = themeSecondaryText
                        )

                        Text(
                            text = if (isLoading) "Loading..." else packName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = themePrimary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (!isLoading) {
                        if (currentPlan == "FREE") {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = "View details",
                                tint = themeSecondaryText,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { showPlans = true }
                            )
                        } else {
                            Button(
                                onClick = { showManageSubscription = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = themePrimary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Manage",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "Your usage this month",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = themeText.copy(alpha = 0.9f)
                )

                // Progress bars
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = themePrimary)
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProgressRow(
                            icon = "👕",
                            title = "Clothing scans",
                            current = clothesDetectionUsed,
                            max = if (clothesDetectionLimit == Int.MAX_VALUE) "∞" else clothesDetectionLimit.toString(),
                            isUnlimited = clothesDetectionLimit == Int.MAX_VALUE,
                            themeTeal = themeTeal,
                            themeText = themeText,
                            themePrimary = themePrimary,
                            themeSoftPink = themeSoftPink
                        )
                        ProgressRow(
                            icon = "✨",
                            title = "Outfit suggestions",
                            current = outfitSuggestionsUsed,
                            max = if (outfitSuggestionsLimit == Int.MAX_VALUE) "∞" else outfitSuggestionsLimit.toString(),
                            isUnlimited = outfitSuggestionsLimit == Int.MAX_VALUE,
                            themeTeal = themeTeal,
                            themeText = themeText,
                            themePrimary = themePrimary,
                            themeSoftPink = themeSoftPink
                        )
                        ProgressRow(
                            icon = "🛍️",
                            title = "Items for sale",
                            current = itemsSoldUsed,
                            max = if (itemsSoldLimit == Int.MAX_VALUE) "∞" else itemsSoldLimit.toString(),
                            isUnlimited = itemsSoldLimit == Int.MAX_VALUE,
                            themeTeal = themeTeal,
                            themeText = themeText,
                            themePrimary = themePrimary,
                            themeSoftPink = themeSoftPink
                        )
                    }
                }
            }
        }

        // MARK: - Card 2: Upgrade (seulement si pas PRO_SELLER)
        if (currentPlan != "PRO_SELLER") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = themePrimary.copy(alpha = 0.4f),
                        spotColor = themePrimary.copy(alpha = 0.4f)
                    ),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    themePrimary.copy(alpha = 0.85f),
                                    themePrimary.copy(alpha = 0.7f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowUpward,
                                contentDescription = "Upgrade",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )

                            Text(
                                text = if (currentPlan == "FREE") "Upgrade to Premium" else "Go Pro Seller",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }

                        Text(
                            text = if (currentPlan == "FREE") {
                                "Unlock all AI features\nand create your personalized 3D avatar."
                            } else {
                                "Unlimited sales + all Premium features"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            ),
                            color = Color.White.copy(alpha = 0.95f)
                        )

                        Button(
                            onClick = { showPlans = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("👑")
                                Text(
                                    text = "View packs",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Sheet pour les plans
    if (showPlans) {
        ViewPackages(
            onDismiss = { showPlans = false },
            onPremiumClick = {
                showPlans = false
                showPremiumDetails = true
            },
            onProClick = {
                showPlans = false
                showProDetails = true
            }
        )
    }

    // Sheet pour les détails Premium
    if (showPremiumDetails) {
        PremiumPackDetails(
            onDismiss = {
                showPremiumDetails = false
                // Rafraîchir les données après fermeture
                scope.launch {
                    delay(500)
                    refreshKey++
                }
            },
            onSubscriptionSuccess = {
                refreshSubscriptionData()
            }
        )
    }

    // Sheet pour les détails Pro Seller
    if (showProDetails) {
        ProPackDetails(
            onDismiss = {
                showProDetails = false
                // Rafraîchir les données après fermeture
                scope.launch {
                    delay(500)
                    refreshKey++
                }
            },
            onSubscriptionSuccess = {
                refreshSubscriptionData()
            }
        )
    }

    // Sheet pour gérer l'abonnement
    if (showManageSubscription) {
        ManageSubscription(
            currentPlan = currentPlan,
            isCanceled = isCanceled,
            expirationMessage = expirationMessage,
            clothesDetectionUsed = clothesDetectionUsed,
            clothesDetectionLimit = clothesDetectionLimit,
            outfitSuggestionsUsed = outfitSuggestionsUsed,
            outfitSuggestionsLimit = outfitSuggestionsLimit,
            itemsSoldUsed = itemsSoldUsed,
            itemsSoldLimit = itemsSoldLimit,
            onDismiss = {
                showManageSubscription = false
                // Rafraîchir après fermeture
                scope.launch {
                    delay(500)
                    refreshKey++
                }
            },
            onDataChanged = {
                refreshSubscriptionData()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSubscription(
    currentPlan: String,
    isCanceled: Boolean,
    expirationMessage: String?,
    clothesDetectionUsed: Int,
    clothesDetectionLimit: Int,
    outfitSuggestionsUsed: Int,
    outfitSuggestionsLimit: Int,
    itemsSoldUsed: Int,
    itemsSoldLimit: Int,
    onDismiss: () -> Unit,
    onDataChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val subscriptionRepository = remember { SubscriptionRepository() }

    var isProcessing by remember { mutableStateOf(false) }
    var showCancelConfirmation by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf<String?>(null) }

    // Couleurs dynamiques
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeBackground,
        modifier = modifier.fillMaxSize(),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Close",
                        tint = themePrimary
                    )
                }

                Spacer(Modifier.width(16.dp))

                Text(
                    text = "Manage Subscription",
                    style = MaterialTheme.typography.titleLarge,
                    color = themePrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Current Plan Card
            Card(
                colors = CardDefaults.cardColors(containerColor = themeCard),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (currentPlan == "PREMIUM") Icons.Rounded.Star else Icons.Rounded.Store,
                            contentDescription = null,
                            tint = themePrimary,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Current Plan",
                                style = MaterialTheme.typography.labelMedium,
                                color = themeSecondaryText
                            )

                            Text(
                                text = if (currentPlan == "PREMIUM") "Premium" else "Pro Seller",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = themeText
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCanceled) Color(0xFFFF9800) else Color(0xFF4CAF50)
                        ) {
                            Text(
                                text = if (isCanceled) "Canceled" else "Active",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (expirationMessage != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = expirationMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = themeSecondaryText
                            )
                        }
                    }
                }
            }

            // Usage Stats
            Card(
                colors = CardDefaults.cardColors(containerColor = themeCard),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Usage This Month",
                        style = MaterialTheme.typography.titleMedium,
                        color = themeText
                    )

                    UsageRow(
                        icon = Icons.Rounded.CameraAlt,
                        title = "Clothing Scans",
                        used = clothesDetectionUsed,
                        isUnlimited = clothesDetectionLimit == Int.MAX_VALUE
                    )

                    UsageRow(
                        icon = Icons.Rounded.AutoAwesome,
                        title = "Outfit Suggestions",
                        used = outfitSuggestionsUsed,
                        isUnlimited = outfitSuggestionsLimit == Int.MAX_VALUE
                    )

                    UsageRow(
                        icon = Icons.Rounded.Store,
                        title = "Items for Sale",
                        used = itemsSoldUsed,
                        isUnlimited = itemsSoldLimit == Int.MAX_VALUE
                    )
                }
            }

            // Cancel Section
            if (currentPlan != "FREE") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = themeCard),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Cancel Subscription",
                            style = MaterialTheme.typography.titleMedium,
                            color = themeText
                        )

                        if (isCanceled) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50)
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = "Your subscription has been canceled and will expire on ${expirationMessage ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = themeSecondaryText
                                )
                            }
                        } else {
                            Text(
                                text = "You can cancel your subscription at any time. You'll continue to have access until the end of your billing period.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = themeSecondaryText
                            )

                            Button(
                                onClick = { showCancelConfirmation = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Cancel,
                                        contentDescription = null,
                                        tint = Color.White
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Text(
                                        text = "Cancel Subscription",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = themeCard.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = Color.Blue
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "Good to Know",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBullet(text = "Your subscription renews automatically")
                        InfoBullet(text = "You can upgrade or downgrade anytime")
                        InfoBullet(text = "Cancellation takes effect at period end")
                        InfoBullet(text = "No refunds for partial months")
                    }
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }

    // Confirmation Dialog
    if (showCancelConfirmation) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmation = false },
            title = { Text("Cancel Subscription") },
            text = { Text("Your subscription will remain active until the end of the current billing period. You can still use all features until then.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelConfirmation = false
                        isProcessing = true
                        val token = TokenManager.getToken(context)
                        if (token != null) {
                            scope.launch {
                                subscriptionRepository.cancelSubscription(token).fold(
                                    onSuccess = {
                                        showSuccessDialog = true
                                        onDataChanged()
                                        isProcessing = false
                                    },
                                    onFailure = { error ->
                                        paymentError = error.message
                                        isProcessing = false
                                    }
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Yes, Cancel Subscription")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onDataChanged()
            },
            title = { Text("Success") },
            text = { Text("Subscription canceled successfully") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onDataChanged()
                }) {
                    Text("OK")
                }
            }
        )
    }

    // Error Dialog
    paymentError?.let { error ->
        AlertDialog(
            onDismissRequest = { paymentError = null },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { paymentError = null }) {
                    Text("OK")
                }
            }
        )
    }

    // Loading Overlay
    if (isProcessing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

@Composable
private fun UsageRow(
    icon: ImageVector,
    title: String,
    used: Int,
    isUnlimited: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DynamicThemeColors.primary(ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE),
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = DynamicThemeColors.text(ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE)
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = if (isUnlimited) "Unlimited" else "$used used",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (isUnlimited) Color(0xFF4CAF50) else DynamicThemeColors.primary(ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE)
        )
    }
}

@Composable
private fun InfoBullet(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "•",
            color = DynamicThemeColors.secondaryText()
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = DynamicThemeColors.secondaryText()
        )
    }
}

@Composable
private fun ProgressRow(
    icon: String,
    title: String,
    current: Int,
    max: String,
    isUnlimited: Boolean,
    themeTeal: Color,
    themeText: Color,
    themePrimary: Color,
    themeSoftPink: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.width(28.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = themeText,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = if (isUnlimited) "Unlimited" else "$current/$max",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = themePrimary
            )
        }

        // Progress bar (seulement si limité)
        if (!isUnlimited) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(themeSoftPink.copy(alpha = 0.25f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = (current.toFloat() / max.toIntOrNull()!!.toFloat()).coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(6.dp))
                        .background(themeTeal)
                )
            }
        }
    }
}