package tn.esprit.labasniandroid.ui.screen.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import coil.request.ImageRequest
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.StoreItem
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent

@Composable
fun StoreTab(
    token: String,
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel = viewModel()
) {
    val storeItems by viewModel.storeItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val availableClothes by viewModel.availableClothes.collectAsState()
    val isLoadingClothes by viewModel.isLoadingClothes.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()

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
    val filteredItems by remember(storeItems, searchQuery) {
        derivedStateOf {
            storeItems.filter { item ->
                val name = item.cloth?.name.orEmpty()
                val type = item.cloth?.type.orEmpty()
                searchQuery.isBlank() ||
                    name.contains(searchQuery, ignoreCase = true) ||
                    type.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Store",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PinkPrimary
                    )
                )
                FloatingActionButton(
                    onClick = {
                        showAddDialog = true
                        if (availableClothes.isEmpty() && !isLoadingClothes) {
                            viewModel.loadAvailableClothes(token)
                        }
                    },
                    containerColor = PinkPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    Text(text = "+", fontWeight = FontWeight.Bold)
                }
            }

            StoreSearchField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Rechercher un article..."
            )

            Text(
                text = "Articles populaires",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TealAccent
                )
            )

            if (isLoading && filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PinkPrimary)
                }
            } else if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun article pour le moment. Ajoutez-en avec le bouton +.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TealAccent.copy(alpha = 0.7f)
                        )
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        ProductCard(
                            storeItem = item,
                            isDeleting = deletingIds.contains(item.id),
                            onDelete = {
                                viewModel.deleteStoreItem(token, item.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddStoreItemDialog(
            isSubmitting = isSubmitting,
            isLoadingClothes = isLoadingClothes,
            clothes = availableClothes,
            onDismiss = {
                if (!isSubmitting) showAddDialog = false
            },
            onConfirm = { cloth, price ->
                viewModel.addStoreItem(token = token, selectedCloth = cloth, price = price)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun StoreSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PinkGradientTop.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalMall,
                    contentDescription = null,
                    tint = TealAccent
                )
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                singleLine = true
            )
        }
    }
}

@Composable
private fun ProductCard(
    storeItem: StoreItem,
    isDeleting: Boolean,
    onDelete: () -> Unit
) {
    val cloth = storeItem.cloth
    val priceText = "${String.format("%.2f", storeItem.price)} DT"
    val statusLabel = when (storeItem.status?.lowercase()) {
        "sold" -> "Vendu"
        else -> "Disponible"
    }
    val emoji = emojiForType(cloth?.type.orEmpty())
    val imageUrl = cloth?.imageUrl.orEmpty()
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                // Delete button top-right
                IconButton(
                    onClick = { if (!isDeleting) onDelete() },
                    enabled = !isDeleting,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .zIndex(1f)
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

                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = cloth?.name ?: "Article",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp) // image plus petite que le cadre
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                            .zIndex(0f),
                        contentScale = ContentScale.Fit // montre l'article entier dans la carte
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        PinkGradientTop.copy(alpha = 0.35f),
                                        AquaSoft.copy(alpha = 0.45f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 48.sp)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = cloth?.name ?: "Vêtement",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TealAccent
                    )
                )
                Text(
                    text = priceText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PinkPrimary
                    )
                )
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TealAccent.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
private fun AddStoreItemDialog(
    isSubmitting: Boolean,
    isLoadingClothes: Boolean,
    clothes: List<Cloth>,
    onDismiss: () -> Unit,
    onConfirm: (Cloth, Double) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCloth by remember { mutableStateOf<Cloth?>(null) }
    var priceInput by rememberSaveable { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Text(
                text = "Ajouter un article",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Choisissez un vêtement",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
                if (isLoadingClothes) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PinkPrimary)
                    }
                } else if (clothes.isEmpty()) {
                    Text(
                        text = "Votre dressing est vide pour le moment.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TealAccent.copy(alpha = 0.7f)
                        )
                    )
                } else {
                    val selectionLabel = selectedCloth?.name ?: "Sélectionnez un vêtement"
                    Box {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            tonalElevation = 2.dp,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isSubmitting) { expanded = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (selectedCloth?.imageUrl?.isNotBlank() == true) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(selectedCloth!!.imageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = selectedCloth!!.name,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                    Text(
                                        text = selectionLabel,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (selectedCloth == null) TealAccent.copy(alpha = 0.6f) else TealAccent,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TealAccent
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            clothes.forEach { cloth ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            if (cloth.imageUrl.isNotBlank()) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(cloth.imageUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = cloth.name,
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                )
                                            }
                                            Text(cloth.name)
                                        }
                                    },
                                    onClick = {
                                        selectedCloth = cloth
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Prix (DT)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

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
                    val cloth = selectedCloth
                    val price = priceInput.toDoubleOrNull()
                    formError = when {
                        cloth == null -> "Sélectionnez un vêtement."
                        price == null || price <= 0.0 -> "Saisissez un prix valide."
                        else -> null
                    }
                    if (formError == null && cloth != null && price != null) {
                        onConfirm(cloth, price)
                    }
                },
                enabled = !isSubmitting
            ) {
                Text(text = if (isSubmitting) "Ajout..." else "Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isSubmitting) onDismiss() }) {
                Text(text = "Annuler")
            }
        }
    )
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

