package tn.esprit.labasniandroid.ui.screen.tenues

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.Outfit
import tn.esprit.labasniandroid.ui.screen.tenues.TenuesViewModel
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.api.AIRecommendationResponse
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenuesTab(
    modifier: Modifier = Modifier,
    token: String,
    userId: String,
    viewModel: TenuesViewModel = viewModel(),
    onBack: () -> Unit,
    onOpenFavorites: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val outfits by viewModel.outfits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val aiSuggestion by viewModel.aiSuggestion.collectAsState()
    val isAccepting by viewModel.isAccepting.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Déterminer isMale depuis ThemeVariant (BLUE = MALE, PINK = FEMALE)
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE

    // Couleurs dynamiques
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeSecondary = DynamicThemeColors.secondary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeSecondaryText = DynamicThemeColors.secondaryText()

    var showStylePopup by remember { mutableStateOf(false) }

    LaunchedEffect(token, userId) {
        if (token.isNotBlank() && userId.isNotBlank()) {
            viewModel.initialize(token, userId)
        }
    }

    // ✨ NOUVEAU: Ne plus utiliser Snackbar pour les erreurs de recommandation
    // Les erreurs sont maintenant affichées dans une carte dédiée (RecommendationErrorCard)
    // On garde le Snackbar uniquement pour les autres messages (succès, etc.)
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            // Détecter si c'est une erreur de recommandation (message contient des mots-clés)
            val isRecommendationError = message.contains("don't have enough clothes", ignoreCase = true) ||
                    message.contains("unable to generate", ignoreCase = true) ||
                    message.contains("recommendation", ignoreCase = true) ||
                    message.contains("style", ignoreCase = true) ||
                    message.contains("wardrobe", ignoreCase = true)
            
            if (isRecommendationError && aiSuggestion == null && !isGenerating) {
                // C'est une erreur de recommandation, elle sera affichée dans la carte
                // Ne rien faire ici, la carte s'affichera automatiquement
            } else {
                // Autres erreurs (création d'outfit, etc.) - utiliser Snackbar
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
            }
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Bouton favoris dans la toolbar (comme iOS)
            Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onOpenFavorites
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Favorites",
                        tint = themePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeBackground)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
        ) {
            // ScrollView principal avec pull to refresh (comme iOS)
            androidx.compose.foundation.rememberScrollState().let { scrollState ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Header "My Outfits" (comme iOS)
                    Text(
                        text = "My Outfits",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = themePrimary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Suggestion Card principale (comme iOS)
                    TodaySuggestionCard(
                        themePrimary = themePrimary,
                        themeSecondary = themeSecondary,
                        onSeeSuggestion = { showStylePopup = true },
                        isLoading = isGenerating
                    )

                    // ✅ CARTE AI SUGGESTION (si disponible) - comme iOS
                    aiSuggestion?.let { suggestion ->
                        AISuggestionCard(
                            suggestion = suggestion,
                            isAccepting = isAccepting,
                            themePrimary = themePrimary,
                            themeSecondary = themeSecondary,
                            themeTeal = themeTeal,
                            themeCard = themeCard,
                            themeSecondaryText = themeSecondaryText,
                            onAccept = {
                                viewModel.acceptAISuggestion(token)
                            },
                            onReject = {
                                viewModel.rejectAISuggestion(token)
                            },
                            context = context
                        )
                    }
                    
                    // ✨ NOUVEAU: Carte d'erreur de recommandation (au lieu du Snackbar)
                    errorMessage?.let { message ->
                        // Afficher seulement si c'est une erreur de recommandation (pas de suggestion en cours)
                        if (aiSuggestion == null && !isGenerating) {
                            RecommendationErrorCard(
                                message = message,
                                themePrimary = themePrimary,
                                themeSecondary = themeSecondary,
                                themeCard = themeCard,
                                themeSecondaryText = themeSecondaryText,
                                onDismiss = {
                                    viewModel.clearMessages()
                                }
                            )
                        }
                    }

                    // Section Header "Recent Outfits" (comme iOS)
            Text(
                        text = "Recent Outfits",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = themeTeal
                    )

                    // États de chargement/erreur/vide
                    when {
                        isLoading && outfits.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                                    .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                                CircularProgressIndicator(color = themePrimary)
                            }
                        }
                        errorMessage != null && outfits.isEmpty() -> {
                            Text(
                                text = errorMessage ?: "Error",
                                color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                        outfits.isEmpty() -> {
                            EmptyState(
                                themeSecondaryText = themeSecondaryText
                            )
                        }
                        else -> {
                            // Liste des tenues
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
                                outfits.forEach { outfit ->
                                    TenueCard(
                        outfit = outfit,
                                        isSuggestion = false,
                                        themePrimary = themePrimary,
                                        themeSecondary = themeSecondary,
                                        themeTeal = themeTeal,
                                        themeCard = themeCard,
                                        themeSecondaryText = themeSecondaryText,
                                        onToggleFavorite = {
                                            viewModel.toggleFavorite(outfit.id)
                                        },
                                        onDelete = {
                                            viewModel.deleteOutfit(token, outfit.id)
                                        },
                                        isDeleting = viewModel.deletingIds.value.contains(outfit.id),
                                        context = context
                    )
                }
            }
        }
    }
}
            }
        }

            // ✅ Overlay de chargement pour la génération AI (comme iOS)
            if (isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = themePrimary.copy(alpha = 0.95f))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color.White,
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = "AI is creating your perfect outfit...",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
        }
    }
}
            }
        }
    }

    // StyleSelectionPopup (comme iOS)
    if (showStylePopup) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showStylePopup = false },
            containerColor = themeCard,
            sheetState = sheetState
        ) {
            StyleSelectionPopup(
                styles = listOf("Casual", "Formal", "Sport"),
                themePrimary = themePrimary,
                themeSecondary = themeSecondary,
                isLoading = isGenerating,
                onStyleSelected = { style ->
                    // Appeler la recommandation avec le style sélectionné (comme iOS)
                    val preference = when (style.lowercase()) {
                        "casual" -> "casual"
                        "formal" -> "formal"
                        "sport", "sporty" -> "sport"
                        else -> "casual"
                    }
                    viewModel.generateAISuggestion(token, preference)
                    showStylePopup = false
                },
                onDismiss = { showStylePopup = false }
            )
        }
    }
}

// MARK: - Today's Suggestion Card (comme iOS)
@Composable
private fun TodaySuggestionCard(
    themePrimary: Color,
    themeSecondary: Color,
    onSeeSuggestion: () -> Unit,
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(themeSecondary, themePrimary),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Today's AI Suggestion",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Text(
                text = "Let our AI create the perfect outfit based on weather and your style!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.95f)
            )
            TextButton(
                onClick = onSeeSuggestion,
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = themePrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                Text(
                        text = "Get AI Suggestion",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = themePrimary
                )
                }
            }
        }
    }
}

// MARK: - Style Selection Popup (comme iOS)
@Composable
private fun StyleSelectionPopup(
    styles: List<String>,
    themePrimary: Color,
    themeSecondary: Color,
    isLoading: Boolean = false,
    onStyleSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Which style are you looking for today?",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            styles.forEach { style ->
                Button(
                    onClick = { onStyleSelected(style) },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(themePrimary, themeSecondary),
                                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, 0f)
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                        Text(
                            text = style,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
        Text(
                text = "Cancel",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Red
            )
        }
    }
}

// MARK: - Tenue Card (comme iOS)
@Composable
private fun TenueCard(
    outfit: Outfit,
    isSuggestion: Boolean,
    themePrimary: Color,
    themeSecondary: Color,
    themeTeal: Color,
    themeCard: Color,
    themeSecondaryText: Color,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit, // ✨ NOUVEAU
    isDeleting: Boolean = false, // ✨ NOUVEAU
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuggestion) themeCard.copy(alpha = 0.95f) else themeCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header avec titre et bouton favoris
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
                    Text(
                        text = outfit.eventType ?: "Outfit",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = themeTeal
                    )
                    Text(
                        text = "${outfit.clothes.size} article${if (outfit.clothes.size > 1) "s" else ""}",
                style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = themeSecondaryText
                    )
                }

                // ✨ Boutons favoris et suppression (comme iOS)
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Bouton favoris
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(themeCard.copy(alpha = 0.8f))
                ) {
                        Icon(
                        imageVector = if (outfit.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (outfit.isFavorite) themePrimary else themeSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    }
                    
                    // ✨ Bouton suppression (en dessous du cœur)
                    IconButton(
                        onClick = onDelete,
                        enabled = !isDeleting,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(themeCard.copy(alpha = 0.8f))
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.Red
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Preview des vêtements (56x56 comme iOS, max 3)
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                outfit.clothes.take(3).forEach { cloth ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(cloth.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = cloth.name,
                modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                // Placeholders si moins de 3
                repeat(3 - outfit.clothes.take(3).size) {
        Box(
            modifier = Modifier
                            .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                            .background(themeSecondary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color.Gray.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Date (comme iOS)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = null,
                            tint = themeTeal.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
            Text(
                    text = formatRelativeDate(outfit.createdAt),
                style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = themeTeal.copy(alpha = 0.7f)
                )
            }

            // Boutons Accept/Reject uniquement si suggestion (pas utilisé dans TenueCard normale)
        }
    }
}

// MARK: - AI Suggestion Card (comme iOS AISuggestionCard)
@Composable
private fun AISuggestionCard(
    suggestion: AIRecommendationResponse,
    isAccepting: Boolean,
    themePrimary: Color,
    themeSecondary: Color,
    themeTeal: Color,
    themeCard: Color,
    themeSecondaryText: Color,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    context: android.content.Context
) {
    val outfit = suggestion.outfit ?: return
    val top = outfit.top
    val bottom = outfit.bottom
    val footwear = outfit.footwear
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header avec icône AI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite, // Utiliser sparkles si disponible
                        contentDescription = null,
                        tint = Color(0xFFFFD700), // Yellow
                        modifier = Modifier.size(24.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "AI Suggestion",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = themePrimary
                        )
                        // Info météo si disponible
                        suggestion.metadata?.weather?.let { weather ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${weather.temperature?.toInt() ?: 0}°C",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = themeSecondaryText
                                )
                                weather.city?.let {
                                    Text(
                                        text = "• $it",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = themeSecondaryText
                                    )
                                }
                            }
                        }
                    }
                }
                // Bouton pour fermer/rejeter
                IconButton(
                    onClick = onReject,
                    modifier = Modifier.size(32.dp),
                    enabled = !isAccepting
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Reject",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Preview des 3 vêtements
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top
                top?.let {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        outfitItemContent(
                            clothe = it,
                            label = "Top",
                            themeCard = themeCard,
                            themeSecondaryText = themeSecondaryText,
                            context = context
                        )
                    }
                }

                // Bottom
                bottom?.let {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        outfitItemContent(
                            clothe = it,
                            label = "Bottom",
                            themeCard = themeCard,
                            themeSecondaryText = themeSecondaryText,
                            context = context
                        )
                    }
                }

                // Footwear
                footwear?.let {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        outfitItemContent(
                            clothe = it,
                            label = "Shoes",
                            themeCard = themeCard,
                            themeSecondaryText = themeSecondaryText,
                            context = context
                        )
                    }
                }
            }

            // Explication AI (badges)
            suggestion.metadata?.let { metadata ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    metadata.preference?.let {
                        explanationBadge(
                            icon = Icons.Filled.Check,
                            text = it.replaceFirstChar { char -> char.uppercase() },
                            themeTeal = themeTeal,
                            themeSecondaryText = themeSecondaryText
                        )
                    }
                    metadata.season?.let {
                        explanationBadge(
                            icon = Icons.Filled.CalendarToday,
                            text = it.replaceFirstChar { char -> char.uppercase() },
                            themeTeal = themeTeal,
                            themeSecondaryText = themeSecondaryText
                        )
                    }
                }
            }

            // Boutons Approve/Reject (comme iOS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bouton Reject
                Button(
                    onClick = onReject,
                    enabled = !isAccepting,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Red.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Reject",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.Red,
                            maxLines = 1
                        )
                    }
                }

                // Bouton Accept
                Button(
                    onClick = onAccept,
                    enabled = !isAccepting,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(themeTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAccepting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Accept",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// MARK: - Outfit Item Content (comme iOS)
@Composable
private fun outfitItemContent(
    clothe: tn.esprit.labasniandroid.api.RecommendedCloth,
    label: String,
    themeCard: Color,
    themeSecondaryText: Color,
    context: android.content.Context
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(themeCard),
        contentAlignment = Alignment.Center
    ) {
        val imageUrl = clothe.imageUrl?.takeIf { it.isNotBlank() }
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                    .error(android.R.drawable.ic_menu_gallery)
                    .build(),
                contentDescription = label,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = themeSecondaryText.copy(alpha = 0.5f),
                strokeWidth = 2.dp
            )
        }
    }
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium
        ),
        color = themeSecondaryText
    )
}

// MARK: - Explanation Badge (comme iOS)
@Composable
private fun explanationBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    themeTeal: Color,
    themeSecondaryText: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(themeTeal.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = themeTeal,
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = themeSecondaryText
        )
    }
}

// MARK: - Empty State (comme iOS)
@Composable
private fun EmptyState(
    themeSecondaryText: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "👕",
            fontSize = 50.sp
        )
        Text(
            text = "No outfits at the moment",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp
            ),
            color = themeSecondaryText
        )
    }
}

// MARK: - Favorite Tab (comme iOS)
@Composable
fun FavoriteTab(
    modifier: Modifier = Modifier,
    token: String, // ✨ NOUVEAU : Ajouter token pour pouvoir supprimer
    viewModel: TenuesViewModel = viewModel(),
    onBack: () -> Unit
) {
    val outfits by viewModel.outfits.collectAsState()
    val favorites = remember(outfits) { outfits.filter { it.isFavorite } }
    
    // Déterminer isMale
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    
    // Couleurs dynamiques
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeSecondaryText = DynamicThemeColors.secondaryText()

    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            // Bouton de retour (comme iOS NavigationStack)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = themePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeBackground)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header "My Favorites" (comme iOS)
            Text(
                text = "My Favorites",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = themePrimary,
                modifier = Modifier.fillMaxWidth()
            )

            if (favorites.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "💔",
                        fontSize = 50.sp
                    )
                    Text(
                        text = "No favorite outfits",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp
                        ),
                        color = themeSecondaryText
                    )
                    Text(
                        text = "Tap the heart in \"My Outfits\" to add them here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Liste des favoris
                Column(
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    favorites.forEach { outfit ->
                        TenueCard(
                            outfit = outfit,
                            isSuggestion = false,
                            themePrimary = themePrimary,
                            themeSecondary = DynamicThemeColors.secondary(isMale),
                            themeTeal = themeTeal,
                            themeCard = themeCard,
                            themeSecondaryText = themeSecondaryText,
                            onToggleFavorite = {
                                viewModel.toggleFavorite(outfit.id)
                            },
                            onDelete = {
                                viewModel.deleteOutfit(token, outfit.id)
                            },
                            isDeleting = viewModel.deletingIds.value.contains(outfit.id),
                            context = context
                        )
                    }
                }
            }
        }
    }
}

// MARK: - Recommendation Error Card (comme iOS, style UI intégré)
@Composable
private fun RecommendationErrorCard(
    message: String,
    themePrimary: Color,
    themeSecondary: Color,
    themeCard: Color,
    themeSecondaryText: Color,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF9800).copy(alpha = 0.08f) // Orange léger pour l'erreur
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header avec icône et bouton fermer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = Color(0xFFFF9800), // Orange pour l'erreur
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Recommendation",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = themePrimary
                    )
                }
                // Bouton pour fermer
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Message d'erreur
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                ),
                color = Color(0xFFFF9800), // Orange pour le texte
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Helper function pour formater la date relative
private fun formatRelativeDate(dateString: String?): String {
    if (dateString == null) return "Recently"
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = format.parse(dateString)
        if (date != null) {
            val now = Date()
            val diff = now.time - date.time
            val days = (diff / (1000 * 60 * 60 * 24)).toInt()
            when {
                days == 0 -> "Today"
                days == 1 -> "1 day ago"
                days < 7 -> "$days days ago"
                days < 30 -> "${days / 7} weeks ago"
                else -> "${days / 30} months ago"
            }
        } else {
            "Recently"
        }
    } catch (e: Exception) {
        "Recently"
    }
}
