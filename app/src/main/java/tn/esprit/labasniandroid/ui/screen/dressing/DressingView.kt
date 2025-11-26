package tn.esprit.labasniandroid.ui.screen.dressing

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.File
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
    val themeSecondaryText = DynamicThemeColors.secondaryText()

    // États pour la détection (workflow comme iOS: PhotoGuide → Camera)
    var showPhotoGuide by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedCloth by remember { mutableStateOf<Cloth?>(null) } // Pour ClothingDetailSheet
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showLoadingScreen by remember { mutableStateOf(false) }
    var showDetectionResult by remember { mutableStateOf(false) }
    var detectedImageURL by remember { mutableStateOf<String?>(null) }
    
    val isDetecting by viewModel.isDetecting.collectAsState()
    val detectionResult by viewModel.detectionResult.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    // Helper pour créer un URI via FileProvider
    fun createImageUri(): Uri? {
        return try {
            val imageFile = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Erreur lors de la création du fichier: ${e.message}")
            }
            null
        }
    }

    // Launcher pour la caméra (TakePicture avec URI)
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && selectedImageUri != null) {
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                    if (inputStream != null) {
                        // Options pour décoder l'image avec une taille limitée
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = false
                            inSampleSize = 1 // Pas de downsampling initial
                        }
                        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                        inputStream.close()
                        if (bitmap != null && !bitmap.isRecycled && bitmap.width > 0 && bitmap.height > 0) {
                            // Créer une copie pour éviter le recyclage
                            val config = bitmap.config ?: Bitmap.Config.ARGB_8888
                            val copy = bitmap.copy(config, false)
                            bitmap.recycle()
                            capturedBitmap = copy
                            showImageSourceDialog = false
                            showLoadingScreen = true
                            // Démarrer la détection
                            viewModel.detectCloth(copy)
                        } else {
                            snackbarHostState.showSnackbar("Impossible de décoder l'image ou image invalide")
                        }
                    } else {
                        snackbarHostState.showSnackbar("Impossible d'ouvrir le fichier image")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    snackbarHostState.showSnackbar("Erreur: ${e.localizedMessage ?: e.message}")
                }
            }
        } else if (!success) {
            scope.launch {
                snackbarHostState.showSnackbar("Capture annulée")
            }
        }
    }

    // Launcher pour la galerie
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        // D'abord, lire les dimensions sans décoder complètement
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeStream(inputStream, null, options)
                        inputStream.close()
                        
                        // Vérifier que l'image est valide
                        if (options.outWidth <= 0 || options.outHeight <= 0) {
                            snackbarHostState.showSnackbar("Image invalide ou corrompue")
                            return@launch
                        }
                        
                        // Calculer le sample size pour éviter OutOfMemoryError
                        var sampleSize = 1
                        val maxDimension = 2048
                        if (options.outWidth > maxDimension || options.outHeight > maxDimension) {
                            val widthRatio = options.outWidth / maxDimension
                            val heightRatio = options.outHeight / maxDimension
                            sampleSize = maxOf(widthRatio, heightRatio)
                        }
                        
                        // Maintenant décoder avec le bon sample size
                        val decodeOptions = BitmapFactory.Options().apply {
                            inJustDecodeBounds = false
                            inSampleSize = sampleSize
                            inPreferredConfig = Bitmap.Config.ARGB_8888
                        }
                        
                        val inputStream2 = context.contentResolver.openInputStream(uri)
                        if (inputStream2 != null) {
                            val bitmap = BitmapFactory.decodeStream(inputStream2, null, decodeOptions)
                            inputStream2.close()
                            
                            if (bitmap != null && !bitmap.isRecycled && bitmap.width > 0 && bitmap.height > 0) {
                                val config = bitmap.config ?: Bitmap.Config.ARGB_8888
                                val copy = bitmap.copy(config, false)
                                bitmap.recycle()
                                capturedBitmap = copy
                                showImageSourceDialog = false
                                showLoadingScreen = true
                                viewModel.detectCloth(copy)
                            } else {
                                snackbarHostState.showSnackbar("Impossible de décoder l'image")
                            }
                        } else {
                            snackbarHostState.showSnackbar("Impossible de rouvrir le fichier image")
                        }
                    } else {
                        snackbarHostState.showSnackbar("Impossible d'ouvrir le fichier image")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    snackbarHostState.showSnackbar("Erreur: ${e.localizedMessage ?: e.message}")
                }
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        scope.launch {
            if (granted) {
                val photoUri = createImageUri()
                if (photoUri != null) {
                    selectedImageUri = photoUri
                    takePictureLauncher.launch(photoUri)
                }
            } else {
                snackbarHostState.showSnackbar("Autorisez la caméra pour prendre une photo.")
            }
        }
    }

    fun openCamera() {
        val permission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                val photoUri = createImageUri()
                if (photoUri != null) {
                    selectedImageUri = photoUri
                    takePictureLauncher.launch(photoUri)
                }
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

    fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    val clothes by viewModel.clothes.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()

    var authToken by remember { mutableStateOf<String?>(null) }
    var userId by remember { mutableStateOf<String?>(null) }

    // Observer le résultat de détection (comme iOS)
    LaunchedEffect(detectionResult) {
        if (detectionResult != null && showLoadingScreen) {
            try {
                val (result, imageUrl) = detectionResult!!
                if (imageUrl.isNotBlank()) {
                    detectedImageURL = imageUrl
                    showLoadingScreen = false
                    
                    // Nettoyer le bitmap immédiatement (on utilise l'URL Cloudinary)
                    capturedBitmap?.let {
                        if (!it.isRecycled) {
                            it.recycle()
                        }
                    }
                    capturedBitmap = null
                    
                    // Délai comme iOS (DispatchQueue.main.asyncAfter) pour éviter les conflits de state
                    kotlinx.coroutines.delay(150)
                    showDetectionResult = true
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Erreur: URL d'image manquante dans la réponse")
                    }
                    showLoadingScreen = false
                    // Nettoyer en cas d'erreur aussi
                    capturedBitmap?.let {
                        if (!it.isRecycled) {
                            it.recycle()
                        }
                    }
                    capturedBitmap = null
                    viewModel.clearDetectionResult()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                scope.launch {
                    snackbarHostState.showSnackbar("Erreur lors du traitement: ${e.localizedMessage ?: e.message}")
                }
                showLoadingScreen = false
                // Nettoyer en cas d'exception aussi
                capturedBitmap?.let {
                    if (!it.isRecycled) {
                        it.recycle()
                    }
                }
                capturedBitmap = null
                viewModel.clearDetectionResult()
            }
        }
    }

    // Observer les erreurs de détection
    LaunchedEffect(isDetecting, errorMessage) {
        if (!isDetecting && showLoadingScreen && detectionResult == null) {
            // Erreur lors de la détection
            showLoadingScreen = false
            // Nettoyer le bitmap en cas d'erreur
            capturedBitmap?.let {
                if (!it.isRecycled) {
                    it.recycle()
                }
            }
            capturedBitmap = null
            // L'erreur sera affichée via errorMessage
        }
    }

    // Observer le refresh event
    LaunchedEffect(Unit) {
        viewModel.refreshEvent.collect {
            val token = authToken
            if (token != null) {
                viewModel.loadClothes(token)
            }
        }
    }

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

    // Catégories standardisées (comme iOS)
    val categories = listOf("All", "Top", "Bottom", "Dress", "Shoes", "Accessory", "Jacket")
    
    // Helper pour normaliser les catégories (Tshirt → Top, Pants → Bottom, etc.)
    fun normalizeCategory(category: String): String {
        val normalized = category.lowercase().trim()
        return when {
            normalized.contains("top") || normalized.contains("tshirt") || normalized.contains("shirt") || normalized.contains("haut") -> "Top"
            normalized.contains("bottom") || normalized.contains("pant") || normalized.contains("jean") || normalized.contains("bas") -> "Bottom"
            normalized.contains("dress") || normalized.contains("robe") -> "Dress"
            normalized.contains("shoe") || normalized.contains("chaussure") -> "Shoes"
            normalized.contains("accessory") || normalized.contains("accessoire") -> "Accessory"
            normalized.contains("jacket") || normalized.contains("veste") || normalized.contains("manteau") -> "Jacket"
            else -> category // Garder original si pas reconnu
        }
    }

    // Filtrage amélioré (comme iOS - inclut color, style, season)
    val filteredClothes by remember(clothes, searchQuery, selectedCategory) {
        derivedStateOf {
            clothes.filter { cloth ->
                // Filtre par catégorie (normalisé: Top, Bottom, etc.)
                val clothCategoryNormalized = normalizeCategory(cloth.type)
                val matchesCategory = selectedCategory == "All" || 
                    clothCategoryNormalized.equals(selectedCategory, ignoreCase = true)
                
                // Filtre par recherche textuelle (comme iOS - inclut type, color, style, season)
                val matchesQuery = if (searchQuery.isBlank()) {
                    true
                } else {
                    val query = searchQuery.lowercase()
                    val categoryMatch = cloth.type.lowercase().contains(query)
                    val colorMatch = cloth.color?.lowercase()?.contains(query) ?: false
                    val styleMatch = cloth.style?.lowercase()?.contains(query) ?: false
                    val seasonMatch = cloth.season?.lowercase()?.contains(query) ?: false
                    categoryMatch || colorMatch || styleMatch || seasonMatch
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
                                },
                                onClick = {
                                    selectedCloth = cloth // Ouvrir ClothingDetailSheet (comme iOS)
                                }
                            )
                        }
                    }
                }
            }

            // Bouton flottant EN BAS À DROITE (comme iOS - ouvre PhotoGuidePopup)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 20.dp, bottom = 20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingAddButton(
                    onClick = { showPhotoGuide = true }, // Workflow iOS: bouton → PhotoGuide
                    themePrimary = themePrimary
                )
            }
        }

        // PhotoGuidePopupView (comme iOS - s'affiche avant ImageSourceDialog)
        PhotoGuidePopupView(
            isShowing = showPhotoGuide,
            onDismiss = { showPhotoGuide = false },
            onContinue = { 
                showPhotoGuide = false
                showImageSourceDialog = true // Après PhotoGuide, ouvrir ImageSourceDialog
            },
            themePrimary = themePrimary,
            themeTeal = themeTeal,
            themeCard = themeCard,
            themeBackground = themeBackground,
            themeText = themeText,
            themeSecondaryText = themeSecondaryText
        )

        // Dialog de choix caméra/galerie (après PhotoGuide)
        if (showImageSourceDialog) {
            ImageSourceDialog(
                onDismiss = { showImageSourceDialog = false },
                onCameraClick = { openCamera() },
                onGalleryClick = { openGallery() },
                themePrimary = themePrimary,
                themeCard = themeCard,
                themeText = themeText
            )
        }

        // Écran de chargement plein écran
        if (showLoadingScreen) {
            AIAnalysisLoadingScreen(
                themePrimary = themePrimary,
                themeTeal = themeTeal,
                themeBackground = themeBackground,
                themeText = themeText,
                themeSecondaryText = themeSecondaryText
            )
        }

        // BottomSheet de résultats (comme iOS - utilise uniquement l'URL Cloudinary)
        if (showDetectionResult && detectionResult != null) {
            val (result, imageUrl) = detectionResult!!
            if (imageUrl.isNotBlank()) {
                DetectionResultBottomSheet(
                    image = null, // On n'utilise plus le bitmap local (comme iOS)
                    detectionResult = result,
                    imageURL = imageUrl,
                    isShowing = showDetectionResult,
                    isSaving = isSaving,
                    onDismiss = {
                        showDetectionResult = false
                        detectedImageURL = null
                        viewModel.clearDetectionResult()
                    },
                    onSave = { imgURL, category, color, style, season ->
                        val token = authToken
                        if (token != null) {
                            viewModel.saveDetectedCloth(
                                token = token,
                                imageURL = imgURL,
                                category = category,
                                color = color,
                                style = style,
                                season = season
                            )
                            showDetectionResult = false
                            detectedImageURL = null
                            viewModel.clearDetectionResult()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Session expirée. Veuillez vous reconnecter.")
                            }
                        }
                    },
                    themePrimary = themePrimary,
                    themeSecondary = themeSecondary,
                    themeTeal = themeTeal,
                    themeCard = themeCard,
                    themeBackground = themeBackground,
                    themeText = themeText,
                    themeSecondaryText = themeSecondaryText
                )
            }
        }
    }

    // ClothingDetailSheet (comme iOS - s'affiche quand on clique sur une carte)
    ClothingDetailSheet(
        cloth = selectedCloth,
        isShowing = selectedCloth != null,
        isDeleting = deletingIds.contains(selectedCloth?.id ?: ""),
        onDismiss = { selectedCloth = null },
        onDelete = {
            val token = authToken
            val cloth = selectedCloth
            if (token != null && cloth != null) {
                viewModel.deleteCloth(token, cloth.id)
                selectedCloth = null // Fermer la sheet après suppression
            } else {
                scope.launch { snackbarHostState.showSnackbar("Session expirée.") }
            }
        },
        themePrimary = themePrimary,
        themeSecondary = themeSecondary,
        themeSoftPink = themeSoftPink,
        themeAqua = themeAqua,
        themeTeal = themeTeal,
        themeCard = themeCard,
        themeBackground = themeBackground,
        themeText = themeText,
        themeSecondaryText = themeSecondaryText
    )

    // Dialog de confirmation de suppression (gardé pour le bouton trash sur la carte)
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

// Helper pour obtenir la couleur de bordure selon catégorie (comme iOS)
@Composable
private fun getBorderColorForCategory(category: String): Color {
    val normalized = category.lowercase().trim()
    return when {
        normalized.contains("top") || normalized.contains("tshirt") || normalized.contains("shirt") || normalized.contains("haut") -> 
            Color(0xFFA7E0E0) // Teal clair (Tops)
        normalized.contains("bottom") || normalized.contains("pant") || normalized.contains("jean") || normalized.contains("bas") -> 
            Color(0xFF4D5F8F) // Bleu marine (Pants)
        normalized.contains("dress") || normalized.contains("robe") -> 
            Color(0xFFDB6A8F) // Rose vif (Dress)
        normalized.contains("shoe") || normalized.contains("chaussure") -> 
            Color(0xFF4A4A4A) // Gris foncé (Shoes)
        normalized.contains("accessory") || normalized.contains("accessoire") || normalized.contains("jacket") || normalized.contains("veste") -> 
            Color(0xFFE8AABE) // Rose doux (Accessories & Jacket)
        else -> 
            Color(0xFFD3D3D3) // Gris clair par défaut
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

// Clothing Card (comme iOS - avec bordure colorée, saison, animation)
@Composable
private fun ClothingCard(
    cloth: Cloth,
    isDeleting: Boolean,
    themePrimary: Color,
    themeCard: Color,
    themeTeal: Color,
    onDelete: () -> Unit,
    onClick: () -> Unit // Clic sur la carte → ClothingDetailSheet
) {
    // Couleur de bordure selon catégorie (comme iOS)
    val borderColor = getBorderColorForCategory(cloth.type)
    val categoryColor = CategoryColors.colorForCategory(cloth.type)
    
    // Animation de suppression (comme iOS)
    val alpha by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (isDeleting) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeCard),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Clic sur la carte
            .alpha(alpha)
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            // Bordure colorée fine (comme iOS - 2.5dp)
            .border(
                width = 2.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
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
                    // Afficher la saison si disponible (comme iOS)
                    if (!cloth.season.isNullOrBlank()) {
                        Text(
                            text = cloth.season.replaceFirstChar { it.uppercaseChar() },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                color = themeTeal.copy(alpha = 0.7f)
                            ),
                            maxLines = 1
                        )
                    } else if (cloth.name.isNotBlank() && cloth.name != cloth.type) {
                        // Fallback: afficher le nom si pas de saison
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
