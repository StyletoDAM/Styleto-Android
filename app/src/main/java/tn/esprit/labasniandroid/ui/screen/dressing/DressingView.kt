package tn.esprit.labasniandroid.ui.screen.dressing

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.ui.theme.CategoryColors
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.utils.findActivity

@Composable
fun DressingTab(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    viewModel: DressingViewModel = viewModel()
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
    val themeText = DynamicThemeColors.text(isMale)

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        scope.launch {
            if (bitmap != null) {
                snackbarHostState.showSnackbar("Photo capturée (non enregistrée).")
            } else {
                snackbarHostState.showSnackbar("Capture annulée.")
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        scope.launch {
            if (granted) {
                takePictureLauncher.launch(null)
            } else {
                snackbarHostState.showSnackbar("Autorisez la caméra pour prendre une photo.")
            }
        }
    }

    fun openCamera() {
        val permission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                takePictureLauncher.launch(null)
            }
            context.findActivity()?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permission) } == true -> {
                scope.launch {
                    snackbarHostState.showSnackbar("La caméra est requise pour prendre une photo.")
                }
            }
            else -> {
                cameraPermissionLauncher.launch(permission)
            }
        }
    }

    val clothes by viewModel.clothes.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()

    var authToken by remember { mutableStateOf<String?>(null) }
    var userId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authToken = TokenManager.getToken(context)
        userId = TokenManager.getUserId(context)
        val token = authToken
        if (token != null) {
            viewModel.loadClothes(token)
        } else {
            snackbarHostState.showSnackbar("Session expirée. Veuillez vous reconnecter.")
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
    var selectedCategory by rememberSaveable { mutableStateOf("All") }

    // Catégories comme iOS
    val categories = listOf("All", "Tshirt", "Pants", "Dress", "Shoes", "Accessory")

    val filteredClothes by remember(clothes, searchQuery, selectedCategory) {
        derivedStateOf {
            clothes.filter { cloth ->
                // Filtre par catégorie
                val matchesCategory = selectedCategory == "All" || 
                    cloth.type.equals(selectedCategory, ignoreCase = true)
                
                // Filtre par recherche textuelle (comme iOS)
                val matchesQuery = if (searchQuery.isBlank()) {
                    true
                } else {
                    val query = searchQuery.lowercase()
                    val categoryMatch = cloth.type.lowercase().contains(query)
                    val nameMatch = cloth.name.lowercase().contains(query)
                    categoryMatch || nameMatch
                }
                
                matchesCategory && matchesQuery
            }
        }
    }

    var showDeleteDialog by remember { mutableStateOf<Cloth?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
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
                    .padding(top = 12.dp)
                    .padding(bottom = 80.dp), // Espace pour le bouton flottant
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header (comme iOS)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Dressing",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            color = themePrimary
                        )
                    )
                    Spacer(modifier = Modifier)
                }

                // Search & Filter (comme iOS)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Barre de recherche
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Rechercher...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = themeSecondary
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeSecondary.copy(alpha = 0.6f),
                            unfocusedBorderColor = themeSecondary.copy(alpha = 0.6f),
                            cursorColor = themeSecondary,
                            focusedContainerColor = themeSoftPink.copy(alpha = 0.25f),
                            unfocusedContainerColor = themeSoftPink.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )

                    // Bouton filtre circulaire (comme iOS)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(themeAqua)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                spotColor = Color.Black.copy(alpha = 0.1f)
                            )
                            .clickable { /* TODO: Ouvrir filtre */ },
                        contentAlignment = Alignment.Center
                    ) {
                Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filtre",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Category Chips (comme iOS)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    categories.forEach { category ->
                        CategoryChip(
                            label = category,
                            selected = selectedCategory == category,
                            themePrimary = themePrimary,
                            themeSoftPink = themeSoftPink,
                            themeTeal = themeTeal,
                            onClick = {
                                selectedCategory = category
                                searchQuery = "" // Réinitialiser la recherche comme iOS
                            }
                        )
                    }
                }

                // Clothes Grid (comme iOS)
                if (loading || isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = themePrimary)
                    }
                } else if (filteredClothes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No clothes found",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 22.dp)
                    ) {
                        items(
                            items = filteredClothes,
                            key = { it.id }
                        ) { cloth ->
                            ClothingCard(
                                cloth = cloth,
                                isDeleting = deletingIds.contains(cloth.id),
                                themePrimary = themePrimary,
                                themeCard = themeCard,
                                themeTeal = themeTeal,
                                onDelete = {
                                    showDeleteDialog = cloth
                                }
                            )
                        }
                    }
                }
            }

            // Bouton flottant EN BAS À DROITE (comme iOS)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 20.dp, bottom = 20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingAddButton(
                    onClick = { openCamera() },
                    themePrimary = themePrimary
                )
            }
        }
    }

    // Dialog de confirmation de suppression (comme iOS)
    showDeleteDialog?.let { cloth ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete this item?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val token = authToken
                        if (token.isNullOrBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Session expirée.") }
                        } else {
                            viewModel.deleteCloth(token, cloth.id)
                            showDeleteDialog = null
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Category Chip (comme iOS)
@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    themePrimary: Color,
    themeSoftPink: Color,
    themeTeal: Color,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) themePrimary else themeSoftPink.copy(alpha = 0.6f)
    val textColor = if (selected) Color.White else themeTeal

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp)) // Capsule
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = textColor
            )
        )
    }
}

// Clothing Card (comme iOS)
@Composable
private fun ClothingCard(
    cloth: Cloth,
    isDeleting: Boolean,
    themePrimary: Color,
    themeCard: Color,
    themeTeal: Color,
    onDelete: () -> Unit
) {
    val categoryColor = CategoryColors.colorForCategory(cloth.type)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeCard),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
    ) {
        Column {
            // Image (comme iOS - 140dp de hauteur)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(categoryColor.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (cloth.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(cloth.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = cloth.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                }
            }

            // Infos + Trash (comme iOS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeCard)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                        text = cloth.type.replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = themeTeal
                        ),
                        maxLines = 1
                    )
                    // Note: iOS affiche aussi la saison si disponible, mais Cloth n'a pas ce champ
                    // On peut afficher le nom si nécessaire
                    if (cloth.name.isNotBlank() && cloth.name != cloth.type) {
                Text(
                            text = cloth.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                color = themeTeal.copy(alpha = 0.7f)
                            ),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Trash Button (comme iOS - 32dp)
                IconButton(
                    onClick = { if (!isDeleting) onDelete() },
                    enabled = !isDeleting,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(themePrimary.copy(alpha = 0.15f))
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = themePrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Supprimer",
                            tint = themePrimary,
                            modifier = Modifier.size(16.dp)
                )
            }
        }
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
            .size(60.dp)
            .clip(CircleShape)
            .background(themePrimary)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Ajouter un vêtement",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
