package tn.esprit.labasniandroid.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

enum class PlanType(val displayName: String) {
    FREE("Free Pack"),
    PREMIUM("Premium Access"),
    PRO("Pro Seller")
}

data class SubscriptionPlan(
    val type: PlanType,
    val price: String,
    val features: List<String>,
    val icon: String,
    val iconColor: Color,
    val badge: String? = null,
    val backgroundGradient: Brush? = null
)

/**
 * Convertit le plan du backend (FREE, PREMIUM, PRO_SELLER) vers PlanType
 */
private fun mapBackendPlanToPlanType(backendPlan: String?): PlanType {
    if (backendPlan == null) {
        android.util.Log.w("ViewPackages", "⚠️ Plan null, utilisation FREE par défaut")
        return PlanType.FREE
    }

    val normalizedPlan = backendPlan.trim().uppercase()
    android.util.Log.d("ViewPackages", "🔄 Normalisation plan: '$backendPlan' -> '$normalizedPlan'")

    return when (normalizedPlan) {
        "PREMIUM" -> {
            android.util.Log.d("ViewPackages", "✅ Plan mappé vers PREMIUM")
            PlanType.PREMIUM
        }
        "PRO_SELLER", "PRO" -> {
            android.util.Log.d("ViewPackages", "✅ Plan mappé vers PRO")
            PlanType.PRO
        }
        "FREE" -> {
            android.util.Log.d("ViewPackages", "✅ Plan mappé vers FREE")
            PlanType.FREE
        }
        else -> {
            android.util.Log.w("ViewPackages", "⚠️ Plan inconnu '$normalizedPlan', utilisation FREE par défaut")
            PlanType.FREE
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPackages(
    onDismiss: () -> Unit,
    onPremiumClick: () -> Unit = {},
    onProClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val subscriptionRepository = remember { SubscriptionRepository() }

    var selectedPlan by remember { mutableStateOf(PlanType.FREE) }
    var currentUserPlan by remember { mutableStateOf<PlanType?>(null) }
    var isLoadingPlan by remember { mutableStateOf(true) }

    // Récupérer le plan actuel de l'utilisateur
    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context)
        if (token != null) {
            scope.launch {
                // Essayer d'abord getMySubscription (plus fiable pour le plan)
                subscriptionRepository.getMySubscription(token).fold(
                    onSuccess = { subscription ->
                        android.util.Log.d("ViewPackages", "📊 Plan reçu du backend (getMySubscription): ${subscription.plan}")
                        val mappedPlan = mapBackendPlanToPlanType(subscription.plan)
                        android.util.Log.d("ViewPackages", "✅ Plan mappé: ${mappedPlan.displayName}")
                        currentUserPlan = mappedPlan
                        isLoadingPlan = false
                    },
                    onFailure = { error ->
                        android.util.Log.w("ViewPackages", "⚠️ Erreur getMySubscription, essai avec getMyStats: ${error.message}")
                        // Fallback sur getMyStats
                        subscriptionRepository.getMyStats(token).fold(
                            onSuccess = { stats ->
                                android.util.Log.d("ViewPackages", "📊 Plan reçu du backend (getMyStats): ${stats.plan}")
                                val mappedPlan = mapBackendPlanToPlanType(stats.plan)
                                android.util.Log.d("ViewPackages", "✅ Plan mappé: ${mappedPlan.displayName}")
                                currentUserPlan = mappedPlan
                                isLoadingPlan = false
                            },
                            onFailure = { statsError ->
                                android.util.Log.e("ViewPackages", "❌ Erreur récupération plan: ${statsError.message}")
                                // En cas d'erreur, ne pas définir de plan (null) pour ne pas afficher "Current Pack" par erreur
                                currentUserPlan = null
                                isLoadingPlan = false
                            }
                        )
                    }
                )
            }
        } else {
            android.util.Log.w("ViewPackages", "⚠️ Pas de token, pas de plan défini")
            currentUserPlan = null
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

    // Plans de subscription (exactement comme iOS)
    val plans = remember {
        listOf(
            SubscriptionPlan(
                type = PlanType.FREE,
                price = "Free",
                features = listOf(
                    "5 clothing scans / month",
                    "3 outfit suggestions / month",
                    "3 items for sale / month",
                    "Basic wardrobe access"
                ),
                icon = "⭐",
                iconColor = themePrimary.copy(alpha = 0.8f),
                backgroundGradient = Brush.linearGradient(
                    colors = listOf(
                        themeCard.copy(alpha = 0.95f), // Fond de carte visible
                        themeCard.copy(alpha = 0.85f)  // Légèrement transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                )
            ),
            SubscriptionPlan(
                type = PlanType.PREMIUM,
                price = "9.99 DT/month",
                features = listOf(
                    "Unlimited clothing detection",
                    "Unlimited outfit suggestions",
                    "3 items for sale / month",
                    "Personalized 3D Avatar",
                    "Priority support"
                ),
                icon = "👑",
                iconColor = Color.White,
                badge = "Most Popular",
                backgroundGradient = Brush.verticalGradient(
                    colors = listOf(
                        themePrimary.copy(alpha = 0.25f), // Plus visible
                        themePrimary.copy(alpha = 0.15f)  // Plus visible
                    )
                )
            ),
            SubscriptionPlan(
                type = PlanType.PRO,
                price = "24.99 DT/month",
                features = listOf(
                    "Unlimited clothing detection",
                    "Unlimited outfit suggestions",
                    "Unlimited sales on the Store",
                    "Advanced sales analytics",
                    "Professional seller badge",
                    "VIP priority support"
                ),
                icon = "🛍️",
                iconColor = Color.White,
                backgroundGradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4AA3A2).copy(alpha = 0.22f), // Plus visible
                        Color(0xFF6BC4C3).copy(alpha = 0.16f)  // Plus visible
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                )
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = themeBackground,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = themePrimary
                    )
                }

                Text(
                    text = "Choose Your Pack",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = themeText,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer pour équilibrer le layout
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Plans list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(plans) { plan ->
                    PlanCard(
                        plan = plan,
                        isSelected = selectedPlan == plan.type,
                        isCurrentPlan = currentUserPlan == plan.type,
                        isLoading = isLoadingPlan,
                        onSelect = {
                            // Ne pas ouvrir les détails si c'est le pack actuel
                            if (currentUserPlan != plan.type) {
                                selectedPlan = plan.type
                                // Si c'est Premium ou Pro, ouvrir la page de détails correspondante
                                when (plan.type) {
                                    PlanType.PREMIUM -> onPremiumClick()
                                    PlanType.PRO -> onProClick()
                                    else -> {}
                                }
                            }
                        },
                        themePrimary = themePrimary,
                        themeTeal = themeTeal,
                        themeCard = themeCard,
                        themeText = themeText,
                        themeSecondaryText = themeSecondaryText
                    )
                }

                // Tip (comme iOS)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = themeCard.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "💡",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = "Tip: Upgrade to Premium or Pro Seller anytime. Cancel whenever you want, no commitment.",
                                style = MaterialTheme.typography.labelMedium,
                                color = themeSecondaryText
                            )
                        }
                    }
                }

                // Bottom padding
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    isCurrentPlan: Boolean,
    isLoading: Boolean = false,
    onSelect: () -> Unit,
    themePrimary: Color,
    themeTeal: Color,
    themeCard: Color,
    themeText: Color,
    themeSecondaryText: Color,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f),
        label = "card_scale"
    )

    val clickableEnabled = !isCurrentPlan && plan.type != PlanType.FREE

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // Padding externe
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                brush = plan.backgroundGradient!!,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(enabled = clickableEnabled) {
                if (clickableEnabled) {
                    onSelect()
                }
            }
    ) {
        // Overlay pour bordure (comme iOS .overlay())
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.5.dp,
                        color = themePrimary.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(24.dp)
                    )
            )
        }

        // Contenu (comme iOS padding(20))
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Badges en haut à droite (style uniforme)
            if (plan.badge != null || isCurrentPlan) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Badge "Most Popular"
                    plan.badge?.let { badge ->
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    themePrimary,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Espacement entre badges si les deux sont présents
                    if (plan.badge != null && isCurrentPlan) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Badge "Current Pack" (seulement si pas en chargement)
                    if (!isLoading && isCurrentPlan) {
                        Text(
                            text = "Current Pack",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = themeTeal,
                            modifier = Modifier
                                .background(
                                    themeTeal.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Header (EXACTEMENT comme Free Pack - sans badge dans le Row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon (comme iOS)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            plan.iconColor.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = plan.icon,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                        color = plan.iconColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Plan info
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = plan.type.displayName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = themeText
                    )
                    Text(
                        text = plan.price,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = themePrimary
                    )
                }
            }

            // Features
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                plan.features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Feature included",
                            tint = themePrimary,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                            color = themeText.copy(alpha = 0.9f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Action button
            if (isCurrentPlan) {
                // Pack actuel - Afficher message et désactiver
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = themeTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "You are currently using this pack",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = themeTeal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (plan.type == PlanType.FREE) {
                // Free quand non actuel - Message au lieu de bouton
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel current pack to return to Free",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = themeSecondaryText,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Pack non actuel - Bouton d'upgrade
                Button(
                    onClick = {
                        onSelect()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (plan.type) {
                            PlanType.PREMIUM -> themePrimary
                            PlanType.PRO -> Color(0xFF4AA3A2)
                            else -> themePrimary
                        },
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = when (plan.type) {
                            PlanType.PREMIUM -> "Upgrade to Premium"
                            PlanType.PRO -> "Upgrade to Pro Seller"
                            else -> ""
                        },
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