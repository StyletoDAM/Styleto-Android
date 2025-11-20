package tn.esprit.labasniandroid.ui.screen.store

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.StoreItem
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.TokenManager

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun StoreTab(
    token: String,
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel = viewModel(),
    onNavigateToCart: () -> Unit = {},
    onNavigateToMessaging: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Récupérer isMale depuis ThemeVariant (BLUE = MALE, PINK = FEMALE) - se met à jour en temps réel
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    
    // Couleurs dynamiques basées sur le genre
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeSecondary = DynamicThemeColors.secondary(isMale)
    val themeSoftPink = DynamicThemeColors.softPink(isMale)
    val themeAqua = DynamicThemeColors.aqua(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeBackground = DynamicThemeColors.background()
    val themeCard = DynamicThemeColors.card()

    val storeItems by viewModel.storeItems.collectAsState()
    val discoverItems by viewModel.discoverItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()
    val showAddToStore by viewModel.showAddToStore.collectAsState()
    val showToast by viewModel.showToast.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val availableClothes by viewModel.availableClothes.collectAsState()
    val isLoadingClothes by viewModel.isLoadingClothes.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

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

    var showEditDialog by remember { mutableStateOf<StoreItem?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<StoreItem?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(themeBackground)
        ) {
            // Scrollable Content (comme iOS)
        Column(
            modifier = Modifier
                .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header avec boutons (comme iOS) - Dark pink, bold, large
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Store",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            color = themePrimary
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    // Boutons Messages et Panier (comme iOS)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Bouton Messages
                        IconButton(
                            onClick = { onNavigateToMessaging() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(themePrimary)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape,
                                    spotColor = Color.Black.copy(alpha = 0.2f)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Message,
                                contentDescription = "Messages",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Bouton Panier
                        IconButton(
                            onClick = { onNavigateToCart() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(themePrimary)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape,
                                    spotColor = Color.Black.copy(alpha = 0.2f)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Panier",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Search Bar (comme iOS)
                StoreSearchBar(
                    value = searchText,
                    onValueChange = { viewModel.setSearchText(it) },
                    themeSecondary = themeSecondary,
                    themeSoftPink = themeSoftPink
                )

                // My Items Section (comme iOS)
                if (storeItems.isNotEmpty()) {
                    SectionHeader(
                        title = "My Items",
                        themeTeal = themeTeal
                    )
                    MyItemsGrid(
                        items = storeItems,
                        deletingIds = deletingIds,
                        themePrimary = themePrimary,
                        themeCard = themeCard,
                        themeTeal = themeTeal,
                        themeSecondary = themeSecondary,
                        themeAqua = themeAqua,
                        onDelete = { }, // Plus utilisé
                        onEdit = { showEditDialog = it }
                    )
                }

                // Discover Section (comme iOS)
                if (discoverItems.isNotEmpty()) {
                    SectionHeader(
                        title = "Discover",
                        themeTeal = themeTeal
                    )
                    DiscoverGrid(
                        items = discoverItems,
                        themeCard = themeCard,
                        themeTeal = themeTeal,
                        themeSecondary = themeSecondary,
                        themeAqua = themeAqua
                    )
                }

                // Loading state
                if (isLoading && storeItems.isEmpty() && discoverItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                            .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                        CircularProgressIndicator(color = themePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(100.dp)) // Espace pour le bouton flottant
            }

            // Toast Notification (comme iOS)
            if (showToast) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    ToastNotification(
                        message = "Item deleted",
                        onDismiss = { 
                            // Le toast se ferme automatiquement après 2 secondes
                        }
                    )
                }
            }

            // Bouton flottant EN BAS À DROITE (comme iOS)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 24.dp, bottom = 24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingAddButton(
                    onClick = {
                        viewModel.showAddToStore()
                        viewModel.loadAvailableClothes(token)
                    },
                    themePrimary = themePrimary
                )
            }
        }
    }

    // AddToStoreSheet (comme iOS)
    if (showAddToStore) {
        AddToStoreSheet(
            viewModel = viewModel,
            token = token,
            availableClothes = availableClothes,
            isLoadingClothes = isLoadingClothes,
            isSubmitting = isSubmitting,
            themePrimary = themePrimary,
            themeSecondary = themeSecondary,
            themeSoftPink = themeSoftPink,
            themeCard = themeCard,
            themeText = DynamicThemeColors.text(isMale),
            themeSecondaryText = DynamicThemeColors.secondaryText(),
            onDismiss = { viewModel.hideAddToStore() }
        )
    }

    // Edit Dialog (comme iOS) - contient maintenant Update Price, Mark as Sold et Delete
    showEditDialog?.let { item ->
        EditStoreDialog(
            storeItem = item,
            viewModel = viewModel,
            token = token,
            themePrimary = themePrimary,
            themeSecondary = themeSecondary,
            themeAqua = themeAqua,
            themeCard = themeCard,
            themeSoftPink = themeSoftPink,
            themeTeal = themeTeal,
            themeText = DynamicThemeColors.text(isMale),
            onDismiss = { showEditDialog = null },
            onDeleteClick = { storeItem ->
                showEditDialog = null // Fermer le dialog d'édition
                showDeleteConfirmation = storeItem // Ouvrir le dialog de confirmation
            }
        )
    }

    // Delete Confirmation Dialog
    showDeleteConfirmation?.let { item ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = {
                Text(
                    text = "Delete this item?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteStoreItem(token, item.id)
                        showDeleteConfirmation = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

// Search Bar (exactement comme iOS)
@Composable
private fun StoreSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    themeSecondary: Color,
    themeSoftPink: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(
                color = themeSoftPink.copy(alpha = 0.25f), // Fill comme iOS
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 2.dp, // lineWidth: 2 comme iOS
                color = themeSecondary.copy(alpha = 0.5f), // stroke opacity 0.5 comme iOS
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp) // Hauteur totale du Box
                .padding(horizontal = 16.dp, vertical = 0.dp), // padding horizontal comme iOS
            horizontalArrangement = Arrangement.spacedBy(10.dp), // spacing 10 comme iOS
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Magnifying glass icon (18dp comme iOS)
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = themeSecondary,
                modifier = Modifier.size(18.dp)
            )

            // Text field simple comme iOS - Utilisation de Box pour afficher le placeholder
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // TextField transparent
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = themeSecondary
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                
                // Placeholder (affiché quand value est vide, au-dessus du TextField)
                if (value.isEmpty()) {
                    Text(
                        text = "Search for an item...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = themeSecondary.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 0.dp)
                            .zIndex(1f) // Au-dessus du TextField
                    )
                }
            }

            // Clear button (xmark.circle.fill comme iOS)
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = { onValueChange("") },
                    modifier = Modifier.size(24.dp)
            ) {
                Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Section Header (comme iOS)
@Composable
private fun SectionHeader(
    title: String,
    themeTeal: Color
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = themeTeal
        )
    )
}

// My Items Grid (comme iOS)
@Composable
private fun MyItemsGrid(
    items: List<StoreItem>,
    deletingIds: Set<String>,
    themePrimary: Color,
    themeCard: Color,
    themeTeal: Color,
    themeSecondary: Color,
    themeAqua: Color,
    onDelete: (StoreItem) -> Unit,
    onEdit: (StoreItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ProductCard(
                storeItem = item,
                isDeleting = deletingIds.contains(item.id),
                showDeleteButton = false, // Plus d'icône poubelle
                themePrimary = themePrimary,
                themeCard = themeCard,
                themeTeal = themeTeal,
                themeSecondary = themeSecondary,
                themeAqua = themeAqua,
                onDelete = { }, // Plus utilisé
                onTap = { onEdit(item) }
            )
        }
    }
}

// Discover Grid (comme iOS)
@Composable
private fun DiscoverGrid(
    items: List<StoreItem>,
    themeCard: Color,
    themeTeal: Color,
    themeSecondary: Color,
    themeAqua: Color
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ProductCard(
                storeItem = item,
                isDeleting = false,
                showDeleteButton = false,
                themePrimary = Color.Transparent, // Pas utilisé dans Discover
                themeCard = themeCard,
                themeTeal = themeTeal,
                themeSecondary = themeSecondary,
                themeAqua = themeAqua,
                onDelete = {},
                onTap = {}
            )
        }
    }
}

// Product Card (comme iOS)
@Composable
private fun ProductCard(
    storeItem: StoreItem,
    isDeleting: Boolean,
    showDeleteButton: Boolean,
    themePrimary: Color,
    themeCard: Color,
    themeTeal: Color,
    themeSecondary: Color,
    themeAqua: Color,
    onDelete: () -> Unit,
    onTap: () -> Unit
) {
    val isAvailable = storeItem.status?.lowercase() != "sold"
    val opacity = if (isAvailable) 1.0f else 0.7f
    val imageOpacity = if (isAvailable) 1.0f else 0.5f

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = themeCard),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() }
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = Color.Black.copy(alpha = 0.10f)
            )
    ) {
        Column {
            // Image avec gradient (comme iOS)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                themeSecondary.copy(alpha = 0.35f),
                                themeAqua.copy(alpha = 0.45f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (storeItem.cloth?.imageUrl?.isNotBlank() == true) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(storeItem.cloth!!.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = storeItem.cloth!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                }

                // Badge "SOLD" (comme iOS)
                if (!isAvailable) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SOLD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            ),
                            modifier = Modifier
                            .background(
                                    Color.Red,
                                    shape = RoundedCornerShape(50.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .shadow(4.dp, RoundedCornerShape(50.dp))
                        )
                    }
                }
            }

            // Infos (sans icône poubelle)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeCard)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = storeItem.cloth?.type?.replaceFirstChar { it.uppercaseChar() } ?: "Unknown",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = themeTeal
                    ),
                    maxLines = 1
                )
                Text(
                    text = "${String.format("%.2f", storeItem.price)} DT",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = if (isAvailable) themePrimary else Color.Gray
                    )
                )
            }
        }
    }
}

// Floating Add Button (comme iOS)
@Composable
private fun FloatingAddButton(
    onClick: () -> Unit,
    themePrimary: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(themePrimary)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = Color.Black.copy(alpha = 0.20f)
            )
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Ajouter un article",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Toast Notification (comme iOS)
@Composable
private fun ToastNotification(
    message: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onDismiss()
    }

    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        color = Color.White,
        modifier = Modifier
            .background(
                Color.Red.copy(alpha = 0.9f),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(4.dp, RoundedCornerShape(50.dp))
    )
}

// AddToStoreSheet (comme iOS)
@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
private fun AddToStoreSheet(
    viewModel: StoreViewModel,
    token: String,
    availableClothes: List<Cloth>,
    isLoadingClothes: Boolean,
    isSubmitting: Boolean,
    themePrimary: Color,
    themeSecondary: Color,
    themeSoftPink: Color,
    themeCard: Color,
    themeText: Color,
    themeSecondaryText: Color,
    onDismiss: () -> Unit
) {
    var selectedCloth by remember { mutableStateOf<Cloth?>(null) }
    var priceInput by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        containerColor = themeCard,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // Permet de scroller si le clavier masque le contenu
                .imePadding() // Ajuste automatiquement quand le clavier apparaît
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacement réduit
        ) {
            // Header
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
            Text(
                    text = "Add to Store",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = themePrimary
                    )
                )
                Text(
                    text = "Select an item and set a price",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeSecondaryText
                )
                )
            }

            // Clothes List ou Empty State
                if (isLoadingClothes) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                        .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                    CircularProgressIndicator(color = themePrimary)
                }
            } else if (availableClothes.isEmpty()) {
                EmptyStateClothes(
                    themeSecondary = themeSecondary,
                    themeSecondaryText = themeSecondaryText,
                    themeCard = themeCard
                )
            } else {
                ClothesList(
                    clothes = availableClothes,
                    selectedCloth = selectedCloth,
                    onSelectCloth = { selectedCloth = it },
                    themePrimary = themePrimary,
                    themeCard = themeCard,
                    themeText = themeText,
                    themeSecondaryText = themeSecondaryText,
                    themeSoftPink = themeSoftPink,
                    context = context
                )
            }

            // Espacement réduit entre la liste et le prix
            Spacer(modifier = Modifier.height(8.dp))

            // Price Input
            PriceInput(
                value = priceInput,
                onValueChange = { priceInput = it },
                themeText = themeText,
                themeCard = themeCard,
                themeSecondary = themeSecondary,
                themePrimary = themePrimary
            )

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = { if (!isSubmitting) onDismiss() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = themeSecondary)
                }

                androidx.compose.material3.Button(
                    onClick = {
                        if (selectedCloth != null && priceInput.isNotBlank()) {
                            val price = priceInput.toDoubleOrNull() ?: 0.0
                            if (price > 0) {
                                viewModel.addStoreItem(token, selectedCloth!!, price)
                                selectedCloth = null
                                priceInput = ""
                            }
                        }
                    },
                    enabled = selectedCloth != null && priceInput.isNotBlank() && !isSubmitting && priceInput.toDoubleOrNull() != null && priceInput.toDoubleOrNull()!! > 0,
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (selectedCloth != null && priceInput.isNotBlank() && !isSubmitting) themePrimary else Color.Gray.copy(alpha = 0.3f)
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                    )
                } else {
                        Text("Add", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Empty State Clothes
@Composable
private fun EmptyStateClothes(
    themeSecondary: Color,
    themeSecondaryText: Color,
    themeCard: Color
) {
    Column(
                            modifier = Modifier
                                .fillMaxWidth()
            .padding(32.dp)
            .background(themeCard, RoundedCornerShape(20.dp))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "👕",
            fontSize = 48.sp
        )
        Text(
            text = "No clothes available",
            style = MaterialTheme.typography.titleMedium.copy(
                color = themeSecondaryText
            )
        )
        Text(
            text = "Add clothes from your wardrobe first.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = themeSecondaryText.copy(alpha = 0.8f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

// Clothes List
@Composable
private fun ClothesList(
    clothes: List<Cloth>,
    selectedCloth: Cloth?,
    onSelectCloth: (Cloth) -> Unit,
    themePrimary: Color,
    themeCard: Color,
    themeText: Color,
    themeSecondaryText: Color,
    themeSoftPink: Color,
    context: android.content.Context
) {
    androidx.compose.foundation.lazy.LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
            .height(250.dp), // Hauteur réduite pour moins d'espace
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(clothes.size) { index ->
            val clothe = clothes[index]
            val isSelected = selectedCloth?.id == clothe.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectCloth(clothe) }
                    .background(
                        if (isSelected) themeSoftPink.copy(alpha = 0.3f) else themeCard,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
                                ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                        .data(clothe.imageUrl)
                                                .crossfade(true)
                                                .build(),
                    contentDescription = clothe.name,
                                            modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(6.dp, RoundedCornerShape(14.dp))
                                    .background(
                                        themePrimary.copy(alpha = 0.1f),
                                        RoundedCornerShape(14.dp)
                                    )
                            } else Modifier
                        ),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = clothe.type,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = themeText
                        )
                    )
                    Text(
                        text = clothe.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeSecondaryText
                        )
                    )
                }

                if (isSelected) {
                                Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Selected",
                        tint = themePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// Price Input
@Composable
private fun PriceInput(
    value: String,
    onValueChange: (String) -> Unit,
    themeText: Color,
    themeCard: Color,
    themeSecondary: Color,
    themePrimary: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Price",
            style = MaterialTheme.typography.titleSmall.copy(
                color = themeText
            )
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = themeCard,
                    unfocusedContainerColor = themeCard,
                    focusedBorderColor = themeSecondary.copy(alpha = 0.3f),
                    unfocusedBorderColor = themeSecondary.copy(alpha = 0.3f),
                    cursorColor = themePrimary
                ),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "DT",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = themePrimary
                ),
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

// EditStoreDialog (comme iOS)
@Composable
private fun EditStoreDialog(
    storeItem: StoreItem,
    viewModel: StoreViewModel,
    token: String,
    themePrimary: Color,
    themeSecondary: Color,
    themeAqua: Color,
    themeCard: Color,
    themeSoftPink: Color,
    themeTeal: Color,
    themeText: Color,
    onDismiss: () -> Unit,
    onDeleteClick: (StoreItem) -> Unit // Callback pour ouvrir le dialog de confirmation
) {
    var newPrice by rememberSaveable { mutableStateOf(storeItem.price.toInt().toString()) }
    val isAvailable = storeItem.status?.lowercase() != "sold"
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Item",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
                                    text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image + Infos
                                        Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                            .data(storeItem.cloth?.imageUrl ?: "")
                                                        .crossfade(true)
                                                        .build(),
                        contentDescription = storeItem.cloth?.name,
                                                    modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = storeItem.cloth?.type?.replaceFirstChar { it.uppercaseChar() } ?: "Item",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = themeTeal
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = themePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${storeItem.price.toInt()} DT",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themePrimary
                                )
                            )
                        }
                        // Status Badge
                        Text(
                            text = if (isAvailable) "Available" else "Sold",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isAvailable) Color.Green else Color.Gray
                            ),
                            modifier = Modifier
                                .background(
                                    if (isAvailable) Color.Green.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                                    RoundedCornerShape(50.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Current Price
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(themeSoftPink.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = themeAqua,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${storeItem.price.toInt()} DT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeText
                        )
                    )
                }

                // New Price (only if available)
                if (isAvailable) {
                OutlinedTextField(
                        value = newPrice,
                        onValueChange = { newPrice = it },
                    modifier = Modifier.fillMaxWidth(),
                        label = { Text("New Price") },
                        placeholder = { Text("Ex: 55") },
                        keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = themeCard,
                            unfocusedContainerColor = themeCard
                        )
                    )
                }
            }
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Update Price (only if available)
                if (isAvailable) {
                    androidx.compose.material3.Button(
                onClick = {
                            val price = newPrice.toDoubleOrNull() ?: storeItem.price
                            viewModel.updateStorePrice(token, storeItem.id, price)
                            onDismiss()
                        },
                        enabled = newPrice.isNotBlank() && newPrice.toDoubleOrNull() != null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = themePrimary
                        )
                    ) {
                        Text("Update Price", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Mark as Sold
                androidx.compose.material3.Button(
                    onClick = {
                        if (isAvailable) {
                            viewModel.markAsSold(token, storeItem.id)
                            onDismiss()
                        }
                    },
                    enabled = isAvailable,
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (isAvailable) Color.Red.copy(alpha = 0.9f) else Color.Gray.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = if (isAvailable) "Mark as Sold" else "Already Sold",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Delete Button
                androidx.compose.material3.Button(
                    onClick = {
                        onDeleteClick(storeItem)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.9f)
                    )
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = themePrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}
