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

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }

    // Logique de génération supprimée

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeBackground)
                .padding(innerPadding)
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

                    // Suggestion Card principale (comme iOS) - UI seulement, pas de génération
                    TodaySuggestionCard(
                        themePrimary = themePrimary,
                        themeSecondary = themeSecondary,
                        onSeeSuggestion = { showStylePopup = true }
                    )

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
                                        context = context
                    )
                }
            }
        }
    }
}
            }
        }
    }

    // StyleSelectionPopup (comme iOS) - UI seulement, pas de génération
    if (showStylePopup) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showStylePopup = false },
            containerColor = themeCard,
            sheetState = sheetState
        ) {
            StyleSelectionPopup(
                styles = listOf("Casual", "Formal", "Sporty", "Elegant", "Party"),
                themePrimary = themePrimary,
                themeSecondary = themeSecondary,
                onStyleSelected = { style ->
                    // Pas de génération - juste fermer le popup
                    showStylePopup = false
                },
                onDismiss = { showStylePopup = false }
            )
        }
    }
}

// MARK: - Today's Suggestion Card (comme iOS) - UI seulement
@Composable
private fun TodaySuggestionCard(
    themePrimary: Color,
    themeSecondary: Color,
    onSeeSuggestion: () -> Unit
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
                text = "Today's Suggestion",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Text(
                text = "The weather is nice today! Why not try a light and colorful outfit?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.95f)
            )
            TextButton(
                onClick = onSeeSuggestion,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "See suggestion",
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

// MARK: - Style Selection Popup (comme iOS) - UI seulement
@Composable
private fun StyleSelectionPopup(
    styles: List<String>,
    themePrimary: Color,
    themeSecondary: Color,
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

                // Bouton favoris (comme iOS)
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
                            context = context
                        )
                    }
                }
            }
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
