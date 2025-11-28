package tn.esprit.labasniandroid.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    
    // État pour le pack actuel
    var currentPlan by remember { mutableStateOf("FREE") }
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
    fun refreshSubscriptionData() {
        val token = TokenManager.getToken(context)
        if (token != null) {
            scope.launch {
                subscriptionRepository.getMyStats(token).fold(
                    onSuccess = { stats ->
                        currentPlan = stats.plan
                        
                        // Extraire les valeurs numériques ou "unlimited"
                        clothesDetectionUsed = stats.clothesDetection.used
                        clothesDetectionLimit = when (val limit = stats.clothesDetection.limit) {
                            is Int -> limit
                            "unlimited" -> Int.MAX_VALUE
                            else -> 5
                        }
                        
                        outfitSuggestionsUsed = stats.outfitSuggestions.used
                        outfitSuggestionsLimit = when (val limit = stats.outfitSuggestions.limit) {
                            is Int -> limit
                            "unlimited" -> Int.MAX_VALUE
                            else -> 3
                        }
                        
                        itemsSoldUsed = stats.storeSelling.used
                        itemsSoldLimit = when (val limit = stats.storeSelling.limit) {
                            is Int -> limit
                            "unlimited" -> Int.MAX_VALUE
                            else -> 3
                        }
                        
                        isLoading = false
                    },
                    onFailure = {
                        // En cas d'erreur, utiliser les valeurs par défaut (FREE)
                        currentPlan = "FREE"
                        isLoading = false
                    }
                )
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
        "PREMIUM" -> "👑"
        "PRO_SELLER" -> "💼"
        else -> "⭐"
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // MARK: - Card 1: Free Pack + Usage
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
                        Text(
                            text = packIcon,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 28.sp),
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
                    
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "View details",
                        tint = themeSecondaryText,
                        modifier = Modifier.size(14.dp)
                    )
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
        
        // MARK: - Card 2: Upgrade to Premium
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
                        text = when (currentPlan) {
                            "PREMIUM" -> "Manage Subscription"
                            "PRO_SELLER" -> "You're on Pro Seller!"
                            else -> "Upgrade to Premium"
                        },
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    }
                    
                    Text(
                        text = "Unlock all AI features\nand create your personalized 3D avatar.",
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
                // Rafraîchir les données après fermeture (au cas où un achat aurait été effectué)
                scope.launch {
                    kotlinx.coroutines.delay(500) // Petit délai avant rafraîchissement
                    refreshKey++
                }
            },
            onSubscribe = { isAnnual ->
                // Le paiement est géré dans PremiumPackDetails
            },
            onSubscriptionSuccess = {
                // Rafraîchir immédiatement après achat réussi
                refreshSubscriptionData()
            }
        )
    }
    
    // Sheet pour les détails Pro Seller
    if (showProDetails) {
        ProPackDetails(
            onDismiss = { 
                showProDetails = false
                // Rafraîchir les données après fermeture (au cas où un achat aurait été effectué)
                scope.launch {
                    kotlinx.coroutines.delay(500) // Petit délai avant rafraîchissement
                    refreshKey++
                }
            },
            onSubscribe = { isAnnual ->
                // Le paiement est géré dans ProPackDetails
            },
            onSubscriptionSuccess = {
                // Rafraîchir immédiatement après achat réussi
                refreshSubscriptionData()
            }
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
            val maxInt = max.toIntOrNull() ?: 1
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
                        .fillMaxWidth(fraction = (current.toFloat() / maxInt.toFloat()).coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(6.dp))
                        .background(themeTeal)
                )
            }
        }
    }
}
