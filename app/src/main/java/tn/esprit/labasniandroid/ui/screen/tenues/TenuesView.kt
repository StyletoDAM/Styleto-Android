package tn.esprit.labasniandroid.ui.screen.tenues

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.Outfit
import tn.esprit.labasniandroid.ui.screen.tenues.TenuesViewModel
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.PinkSecondary
import tn.esprit.labasniandroid.ui.theme.TealAccent

@OptIn(ExperimentalLayoutApi::class)
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
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val availableClothes by viewModel.availableClothes.collectAsState()
    val isLoadingClothes by viewModel.isLoadingClothes.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()
    val suggestedOutfit by viewModel.suggestedOutfit.collectAsState()
    val isGeneratingSuggestion by viewModel.isGeneratingSuggestion.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredOutfits by remember(outfits, searchQuery) {
        derivedStateOf {
            outfits.filter { outfit ->
                searchQuery.isBlank() ||
                    outfit.eventType.orEmpty().contains(searchQuery, ignoreCase = true) ||
                    outfit.weatherType.orEmpty().contains(searchQuery, ignoreCase = true)
            }
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog && availableClothes.isEmpty() && !isLoadingClothes) {
        viewModel.loadAvailableClothes(token)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            HeaderBar(
                title = "Mes Tenues",
                onBack = onBack,
                onAction = onOpenFavorites
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Rechercher une tenue...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = PinkSecondary
                    )
                },
                singleLine = true
            )

            SuggestionCard(
                isLoading = isSubmitting || isGeneratingSuggestion,
                onGenerateSuggestion = {
                    if (availableClothes.isEmpty() && !isLoadingClothes) {
                        viewModel.loadAvailableClothes(token)
                    }
                    viewModel.generateRandomSuggestion(token, force = true)
                }
            )

            suggestedOutfit?.let { suggestion ->
                SuggestedOutfitCard(
                    outfit = suggestion,
                    isProcessing = isSubmitting,
                    onApprove = {
                        viewModel.approveSuggestion(token)
                    },
                    onReject = {
                        viewModel.rejectSuggestion(token)
                    },
                    onDismiss = {
                        viewModel.clearSuggestion()
                    }
                )
            }

            Text(
                text = "Tenues récentes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TealAccent
                )
            )

            if (isLoading && filteredOutfits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PinkPrimary)
                }
            } else if (filteredOutfits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune tenue pour le moment. Ajoutez votre première combinaison avec le bouton +.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TealAccent.copy(alpha = 0.7f)
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredOutfits, key = { it.id }) { outfit ->
                        GradientOutfitCard(
                            outfit = outfit,
                            onToggleFavorite = { viewModel.toggleFavorite(outfit.id) },
                            onDelete = { viewModel.deleteOutfit(token, outfit.id) },
                            isDeleting = deletingIds.contains(outfit.id)
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PinkPrimary,
                contentColor = Color.White,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Nouvelle tenue")
            }
        }
    }

    if (showAddDialog) {
        AddOutfitDialog(
            isSubmitting = isSubmitting,
            isLoadingClothes = isLoadingClothes,
            clothes = availableClothes,
            onDismiss = { if (!isSubmitting) showAddDialog = false },
            onConfirm = { selectedClothes, eventType ->
                if (selectedClothes.isEmpty()) {
                    scope.launch { snackbarHostState.showSnackbar("Sélectionnez au moins un vêtement.") }
                } else {
                    viewModel.addOutfit(
                        token = token,
                        selectedClothes = selectedClothes,
                        eventType = eventType
                    )
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun FavoriteTab(
    modifier: Modifier = Modifier,
    viewModel: TenuesViewModel = viewModel(),
    onBack: () -> Unit
) {
    val outfits by viewModel.outfits.collectAsState()
    val favorites = remember(outfits) { outfits.filter { it.isFavorite } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        HeaderBar(
            title = "Mes Favoris",
            onBack = onBack,
            onAction = null
        )

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Vous n'avez pas encore de tenues favorites.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TealAccent.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites, key = { it.id }) { outfit ->
                    GradientOutfitCard(
                        outfit = outfit,
                        onToggleFavorite = { viewModel.toggleFavorite(outfit.id) },
                        onDelete = { },
                        isDeleting = false
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderBar(
    title: String,
    onBack: () -> Unit,
    onAction: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PinkGradientTop.copy(alpha = 0.25f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Retour",
                tint = PinkPrimary
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = PinkPrimary
            )
        )
        if (onAction != null) {
            IconButton(
                onClick = onAction,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PinkGradientTop.copy(alpha = 0.25f))
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = "Favoris",
                    tint = PinkPrimary
                )
            }
        } else {
            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
private fun GradientOutfitCard(
    outfit: Outfit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    isDeleting: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        PinkGradientTop.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.4f)
                    )
                )
            )
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = outfit.eventType?.ifBlank { "Tenue" }?.replaceFirstChar { it.uppercase() } ?: "Tenue",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TealAccent
                        )
                    )
                    Text(
                        text = "${outfit.clothes.size} article(s)",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TealAccent.copy(alpha = 0.65f))
                    )
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (outfit.isFavorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favori",
                        tint = if (outfit.isFavorite) PinkPrimary else TealAccent.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                outfit.clothes.forEach { cloth ->
                    ClothCard(cloth = cloth)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatStatus(outfit.status),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor(outfit.status)
                    )
                )
                IconButton(
                    onClick = onDelete,
                    enabled = !isDeleting,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PinkGradientTop.copy(alpha = 0.18f))
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = PinkPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.RemoveCircle,
                            contentDescription = "Supprimer",
                            tint = PinkPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClothCard(cloth: Cloth) {
    Column(
        modifier = Modifier
            .width(86.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(parseColorToCompose(cloth.colorHex).copy(alpha = 0.16f))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emojiForType(cloth.type),
                fontSize = 18.sp
            )
        }
        Text(
            text = cloth.name.ifBlank { cloth.type },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = TealAccent
            ),
            maxLines = 2
        )
    }
}

@Composable
private fun AddOutfitDialog(
    isSubmitting: Boolean,
    isLoadingClothes: Boolean,
    clothes: List<Cloth>,
    onDismiss: () -> Unit,
    onConfirm: (selected: List<Cloth>, eventType: String?) -> Unit
) {
    val eventTypes = listOf("Casual", "Business", "Soirée", "Sport", "Weekend", "Autre")

    var eventType by rememberSaveable { mutableStateOf(eventTypes.first()) }
    var selectedIds by remember { mutableStateOf(emptySet<String>()) }
    var formError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Text(
                text = "Créer une tenue",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DropdownField(
                    label = "Type d'évènement",
                    value = eventType,
                    options = eventTypes,
                    enabled = !isSubmitting,
                    onValueSelected = { eventType = it }
                )

                Text(
                    text = "Sélectionnez des vêtements",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                when {
                    isLoadingClothes -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PinkPrimary)
                        }
                    }
                    clothes.isEmpty() -> {
                        Text(
                            text = "Votre dressing est vide pour le moment.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TealAccent.copy(alpha = 0.7f))
                        )
                    }
                    else -> {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            clothes.forEach { cloth ->
                                ClothSelectionChip(
                                    cloth = cloth,
                                    selected = selectedIds.contains(cloth.id),
                                    onClick = {
                                        selectedIds = if (selectedIds.contains(cloth.id)) {
                                            selectedIds - cloth.id
                                        } else {
                                            selectedIds + cloth.id
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                formError?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFD23F57))
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isSubmitting) return@TextButton
                    if (selectedIds.isEmpty()) {
                        formError = "Sélectionnez au moins un vêtement."
                    } else {
                        formError = null
                        val selectedClothes = clothes.filter { selectedIds.contains(it.id) }
                        onConfirm(selectedClothes, eventType)
                    }
                },
                enabled = !isSubmitting
            ) {
                Text(text = if (isSubmitting) "Création..." else "Créer")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isSubmitting) onDismiss() }) {
                Text(text = "Annuler")
            }
        }
    )
}

@Composable
private fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    enabled: Boolean,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = { },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled) { expanded = true },
                trailingIcon = {
                    IconButton(onClick = { if (enabled) expanded = true }) {
                        Icon(imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null)
                    }
                },
                singleLine = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClothSelectionChip(
    cloth: Cloth,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (selected) PinkPrimary.copy(alpha = 0.22f) else PinkGradientTop.copy(alpha = 0.16f)
            )
            .border(
                width = 1.dp,
                color = if (selected) PinkPrimary else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emojiForType(cloth.type),
                fontSize = 24.sp
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = cloth.name.ifBlank { cloth.type },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TealAccent
                )
            )
            Text(
                text = cloth.type,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TealAccent.copy(alpha = 0.7f)
                )
            )
        }
    }
}

private fun parseColorToCompose(colorHex: String): Color {
    return try {
        val normalized = normalizeColorForInput(colorHex)
        Color(AndroidColor.parseColor(normalized))
    } catch (_: Exception) {
        Color(0xFFF6D4E3)
    }
}

private fun normalizeColorForInput(input: String): String {
    val fallback = "#F6D4E3"
    if (input.isBlank()) return fallback
    val candidate = input.trim()
    val withoutHash = candidate.removePrefix("#")
    val hex = when (withoutHash.length) {
        3 -> withoutHash.flatMap { listOf(it, it) }.joinToString(separator = "")
        6 -> withoutHash
        8 -> withoutHash.substring(2)
        else -> return fallback
    }
    return "#${hex.uppercase()}"
}

private fun contentColorForBackground(background: Color): Color {
    return if (background.luminance() > 0.5f) Color(0xFF24313C) else Color.White
}

private fun formatStatus(status: String?): String {
    return when (status?.lowercase()) {
        "accepted" -> "Validée"
        "rejected" -> "Rejetée"
        "pending" -> "En attente"
        else -> "En attente"
    }
}

private fun statusColor(status: String?): Color {
    return when (status?.lowercase()) {
        "accepted" -> Color(0xFF3CB371)
        "rejected" -> Color(0xFFD9534F)
        else -> TealAccent.copy(alpha = 0.7f)
    }
}

@Composable
private fun SuggestionCard(
    isLoading: Boolean,
    onGenerateSuggestion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(PinkPrimary, TealAccent)
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Suggestion du jour",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            text = "Il fait beau aujourd'hui ! Pourquoi ne pas essayer une tenue légère et colorée ?",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.92f))
        )
        TextButton(
            onClick = onGenerateSuggestion,
            enabled = !isLoading,
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .border(1.dp, Color.White, RoundedCornerShape(50))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Voir la suggestion")
            }
        }
    }
}

@Composable
private fun SuggestedOutfitCard(
    outfit: Outfit,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        TealAccent.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.4f)
                    )
                )
            )
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Suggestion du jour",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TealAccent
                        )
                    )
                    Text(
                        text = "${outfit.clothes.size} article(s)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TealAccent.copy(alpha = 0.6f))
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.RemoveCircle,
                        contentDescription = "Fermer",
                        tint = TealAccent.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                outfit.clothes.forEach { cloth ->
                    ClothCard(cloth = cloth)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onReject,
                    enabled = !isProcessing,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFD9534F).copy(alpha = 0.1f)),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD9534F)
                    )
                ) {
                    Text(
                        text = "Rejeter",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                TextButton(
                    onClick = onApprove,
                    enabled = !isProcessing,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF3CB371).copy(alpha = 0.1f)),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF3CB371)
                    )
                ) {
                    Text(
                        text = "Approuver",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

private fun emojiForType(type: String): String {
    return when (type.lowercase()) {
        "t-shirt", "tshirt", "haut", "chemise" -> "👕"
        "pull", "sweat" -> "🧥"
        "pantalon", "jean" -> "👖"
        "short" -> "🩳"
        "robe" -> "👗"
        "chaussures", "chaussure", "baskets" -> "👟"
        "accessoire", "accessoires", "sac" -> "👜"
        else -> "✨"
    }
}

