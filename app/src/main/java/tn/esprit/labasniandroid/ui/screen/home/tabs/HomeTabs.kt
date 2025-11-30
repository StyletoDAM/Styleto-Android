package tn.esprit.labasniandroid.ui.screen.home.tabs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import android.widget.Toast
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.ui.components.LabasniOutlinedField
import tn.esprit.labasniandroid.ui.components.LabasniPillButton
import tn.esprit.labasniandroid.ui.components.GenderChip
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.PinkSecondary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.ui.theme.ThemeMode
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.ui.screen.profile.ProfileViewModel
import tn.esprit.labasniandroid.utils.TokenManager
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SnackbarHostState
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
 
// region Dressing

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun LegacyDressingTab(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Tous", "Hauts", "Bas", "Robes", "Chaussures", "Accessoires")
    var selectedCategory by rememberSaveable { mutableStateOf("Tous") }
    val clothes = remember { ClothingItem.samples }

    Box(modifier = modifier.fillMaxSize()) {
        // Background (comme iOS - rose clair)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PinkGradientTop.copy(alpha = 0.18f))
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header (comme iOS - très grand titre)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mon Dressing",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                    color = PinkPrimary
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Search and Filter (comme iOS)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search bar (comme iOS - fond rose clair avec bordure)
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(PinkGradientTop.copy(alpha = 0.25f))
                            .border(2.dp, PinkSecondary.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                Icon(
                                imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                                tint = PinkSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Rechercher...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TealAccent.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.weight(1f)
                )
            }
                    }
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AquaSoft)
                            .shadow(6.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "Filter",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Category chips (comme iOS - horizontal scrollable)
            item {
                val horizontalScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                    categories.forEachIndexed { index, category ->
                        LegacyCategoryChip(
                            label = category,
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                    }
                }
            }

            // Clothes Grid (comme iOS)
            item {
                val rows = (clothes.size + 1) / 2
            LazyVerticalGrid(
                modifier = Modifier
                        .fillMaxWidth()
                        .height((rows * 220).dp),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 22.dp)
            ) {
                items(clothes) { item ->
                    LegacyClothingCard(item = item)
                    }
                }
            }
        }

        // Floating Add Button (comme iOS - cercle rose en haut à droite)
        androidx.compose.material3.IconButton(
            onClick = { /* TODO Ajouter un vêtement */ },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 20.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(PinkPrimary)
                    .shadow(10.dp, CircleShape),
                contentAlignment = Alignment.Center
        ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 22.sp
                    )
                )
            }
        }

        if (isLoading) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.05f)),
                color = Color.Transparent
            ) {}
        }
    }
}

private data class ClothingItem(
    val title: String,
    val category: String,
    val color: Color,
    val emoji: String
) {
    companion object {
        val samples = listOf(
            ClothingItem("T-shirt blanc", "Hauts", Color.White, "👕"),
            ClothingItem("Jean bleu", "Bas", Color(0xFF4D5F8F), "👖"),
            ClothingItem("Robe rose", "Robes", Color(0xFFDB6A8F), "👗"),
            ClothingItem("Baskets", "Chaussures", Color.DarkGray, "👟"),
            ClothingItem("Chemise", "Hauts", Color(0xFFA7E0E0), "👔"),
            ClothingItem("Short", "Bas", Color(0xFFE8AABE), "🩳")
        )
    }
}

@Composable
private fun LegacyCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) PinkPrimary else PinkGradientTop.copy(alpha = 0.6f)
    val textColor = if (selected) Color.White else TealAccent

    androidx.compose.material3.TextButton(
        onClick = onClick,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(all = 0.dp),
        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
            contentColor = Color.Transparent
        ),
        shape = RoundedCornerShape(50.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(background)
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
}

@Composable
private fun LegacyClothingCard(item: ClothingItem) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(20.dp))
    ) {
        // Partie supérieure avec fond coloré et emoji centré (comme iOS)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(item.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.emoji,
                fontSize = 50.sp
            )
        }
        // Partie inférieure avec texte aligné à gauche (comme iOS - même fond que partie supérieure)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(item.color)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TealAccent
                )
            )
            Text(
                text = item.category,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = TealAccent.copy(alpha = 0.7f)
                )
            )
        }
    }
}

// endregion

// region Tenues

// (Legacy Tenues UI removed)

// endregion

// region Store

// endregion

// region Avatar

@Composable
fun AvatarTab(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PinkGradientTop, AquaSoft)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Avatar",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )
            Text(
                text = "Coming soon",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.85f)
                )
            )
        }
    }
}

// endregion

// region Settings

private data class SettingsSection(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val options: List<SettingsOption>
)

private data class SettingsOption(
    val label: String,
    val hasToggle: Boolean = false,
    val initialValue: Boolean = false,
    val hasChevron: Boolean = true,
    val isThemePicker: Boolean = false,
    val isStylePicker: Boolean = false,
    val action: SettingsOptionAction? = null
)

private enum class SettingsOptionAction {
    CHANGE_PASSWORD,
    DELETE_ACCOUNT
}

@Composable
fun SettingsTab(
    user: User?,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onLogout: () -> Unit,
    onUserUpdated: (User) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profileViewModel: ProfileViewModel = viewModel()
    val viewModelUser by profileViewModel.user.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val errorMessage by profileViewModel.errorMessage.collectAsState()
    val successMessage by profileViewModel.successMessage.collectAsState()
    val accountDeleted by profileViewModel.accountDeleted.collectAsState()
    val isPhotoUpdating by profileViewModel.isPhotoUpdating.collectAsState()

    // Theme variant (PINKTheme / BLEUTheme)
    val themeVariant by ThemeController.themeVariant.collectAsState()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val token = TokenManager.getToken(context)
        if (token.isNullOrEmpty()) {
            Toast.makeText(
                context,
                "Session expirée. Veuillez vous reconnecter.",
                Toast.LENGTH_SHORT
            ).show()
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            val imageBytes = loadImageBytes(context, uri)
            if (imageBytes == null) {
                Toast.makeText(
                    context,
                    "Impossible de charger l'image sélectionnée.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                profileViewModel.uploadProfilePhoto(token, imageBytes)
            }
        }
    }

    // Options/confirmation pour la photo (comme iOS)
    var pendingImageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var pendingImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var showPhotoConfirmation by remember { mutableStateOf(false) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bm: android.graphics.Bitmap? ->
        if (bm == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val baos = java.io.ByteArrayOutputStream()
            val ok = bm.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, baos)
            val bytes = if (ok) baos.toByteArray() else null
            baos.close()
            withContext(Dispatchers.Main) {
                if (bytes == null) {
                    Toast.makeText(context, "Capture échouée.", Toast.LENGTH_SHORT).show()
                    return@withContext
                }
                val token = TokenManager.getToken(context)
                if (token.isNullOrEmpty()) {
                    Toast.makeText(context, "Session expirée.", Toast.LENGTH_SHORT).show()
                    return@withContext
                }
                profileViewModel.uploadProfilePhoto(token, bytes)
            }
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        if (granted) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(context, "Autorisez la caméra pour prendre une photo.", Toast.LENGTH_SHORT).show()
        }
    }

    val activeUser = viewModelUser ?: user

    LaunchedEffect(user) {
        if (user != null) {
            profileViewModel.setInitialUser(user)
        }
    }

    LaunchedEffect(Unit) {
        TokenManager.getToken(context)?.let { token ->
            profileViewModel.loadProfile(token)
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            profileViewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            if (accountDeleted) {
                TokenManager.clearToken(context)
                // Vider le panier lors de la suppression de compte (comme iOS)
                tn.esprit.labasniandroid.utils.CartManager.handleLogout()
                profileViewModel.acknowledgeAccountDeleted()
                onLogout()
            } else {
                viewModelUser?.let(onUserUpdated)
            }

            profileViewModel.clearMessages()
        }
    }

    LaunchedEffect(accountDeleted) {
        if (accountDeleted) {
            TokenManager.clearToken(context)
            // Vider le panier lors de la suppression de compte (comme iOS)
            tn.esprit.labasniandroid.utils.CartManager.handleLogout()
            onLogout()
            profileViewModel.acknowledgeAccountDeleted()
        }
    }

    val expandedSections = remember { mutableStateListOf<String>() }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showStyleDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val sections = remember {
        listOf(
            SettingsSection(
                title = "App Settings",
                icon = Icons.Rounded.Settings,
                options = listOf(
                    SettingsOption("Notifications", hasToggle = true, initialValue = true),
                    SettingsOption("Langue", hasChevron = true),
                    SettingsOption("Taille du texte", hasChevron = true)
                )
            ),
            SettingsSection(
                title = "Préférences",
                icon = Icons.Rounded.Palette,
                options = listOf(
                    SettingsOption("Thème", hasChevron = true, isThemePicker = true),
                    SettingsOption("Style préféré", hasChevron = true, isStylePicker = true),
                    SettingsOption("Animations", hasToggle = true)
                )
            ),
            SettingsSection(
                title = "Sécurité",
                icon = Icons.Rounded.Lock,
                options = listOf(
                    SettingsOption(
                        label = "Modifier le mot de passe",
                        hasChevron = true,
                        action = SettingsOptionAction.CHANGE_PASSWORD
                    ),
                    SettingsOption(
                        label = "Supprimer le compte",
                        hasChevron = false,
                        action = SettingsOptionAction.DELETE_ACCOUNT
                    )
                )
            ),
            SettingsSection(
                title = "Aide & Support",
                icon = Icons.Rounded.Mood,
                options = listOf(
                    SettingsOption("Contact", hasChevron = true),
                    SettingsOption("FAQ", hasChevron = true),
                    SettingsOption("Version 1.0.0", hasChevron = false)
                )
            )
        )
    }

    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title avec icône de déconnexion en haut à droite (comme iOS)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Settings",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
            
            // Icône de déconnexion (comme iOS)
            androidx.compose.material3.IconButton(
                onClick = { showLogoutDialog = true }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                    contentDescription = "Déconnexion",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .clickable(enabled = !isPhotoUpdating) { showPhotoOptions = true },
                contentAlignment = Alignment.Center
            ) {
                when {
                    isPhotoUpdating -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                    !activeUser?.profilePicture.isNullOrBlank() -> {
                        AsyncImage(
                            model = activeUser?.profilePicture,
                            contentDescription = "Photo de profil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                    else -> {
                        Text(
                            text = initials(activeUser?.fullName ?: "User"),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PinkPrimary,
                                fontSize = 28.sp
                            )
                        )
                    }
                }
            }
            TextButton(onClick = { if (!isPhotoUpdating) showPhotoOptions = true }, enabled = !isPhotoUpdating) {
                Text(
                    text = if (isPhotoUpdating) "Chargement..." else "Changer la photo",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PinkPrimary
                    )
                )
            }
        }

        EditProfileCard(
            user = activeUser,
            isLoading = isLoading,
            onCancel = {
                profileViewModel.clearMessages()
                profileViewModel.setInitialUser(activeUser)
            },
            onSave = { fullName, phoneNumber, selectedGender ->
                val token = TokenManager.getToken(context)
                if (token == null) {
                    Toast.makeText(
                        context,
                        "Session expirée. Veuillez vous reconnecter.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@EditProfileCard
                }

                profileViewModel.updateProfile(
                    token = token,
                    fullName = fullName,
                    phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
                    gender = selectedGender
                )
            }
        )

        // Dialog: options photo
        if (showPhotoOptions) {
            val hasPhoto = !activeUser?.profilePicture.isNullOrBlank()
            AlertDialog(
                onDismissRequest = { showPhotoOptions = false },
                title = { Text("Changer la photo de profil", style = MaterialTheme.typography.titleLarge) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = {
                            showPhotoOptions = false
                            val granted = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) takePictureLauncher.launch(null)
                            else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }) {
                            Text("Prendre une photo", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                        TextButton(onClick = { showPhotoOptions = false; pickImageLauncher.launch("image/*") }) {
                            Text("Choisir une photo", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                        if (hasPhoto) {
                            TextButton(onClick = {
                                showPhotoOptions = false
                                val token = TokenManager.getToken(context)
                                if (token != null) {
                                    profileViewModel.setProfilePictureFromUrl(token, "")
                                } else {
                                    Toast.makeText(context, "Session expirée.", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text("Supprimer la photo", color = Color(0xFFD23F57), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = { TextButton(onClick = { showPhotoOptions = false }) { Text("Fermer") } }
            )
        }

        // Dialog: confirmation upload
        if (showPhotoConfirmation) {
            AlertDialog(
                onDismissRequest = {
                    showPhotoConfirmation = false
                    pendingImageBitmap = null
                    pendingImageBytes = null
                },
                title = { Text("Confirmer le changement de photo", style = MaterialTheme.typography.titleLarge) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        pendingImageBitmap?.let { bm ->
                            Image(bitmap = bm.asImageBitmap(), contentDescription = null, modifier = Modifier.size(140.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                        }
                        Text("Voulez-vous vraiment changer votre photo de profil ?", textAlign = TextAlign.Center)
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = !isPhotoUpdating && pendingImageBytes != null,
                        onClick = {
                            val token = TokenManager.getToken(context)
                            val bytes = pendingImageBytes
                            if (token != null && bytes != null) {
                                showPhotoConfirmation = false
                                profileViewModel.uploadProfilePhoto(token, bytes)
                                pendingImageBitmap = null
                                pendingImageBytes = null
                            }
                        }
                    ) { Text(if (isPhotoUpdating) "En cours..." else "Confirmer", color = MaterialTheme.colorScheme.primary) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPhotoConfirmation = false
                        pendingImageBitmap = null
                        pendingImageBytes = null
                    }) { Text("Annuler") }
                }
            )
        }

        sections.forEach { section ->
            val isExpanded = expandedSections.contains(section.title)
            SettingsSectionCard(
                section = section,
                isExpanded = isExpanded,
                themeMode = themeMode,
                onToggleExpand = {
                    if (isExpanded) expandedSections.remove(section.title) else expandedSections.add(section.title)
                },
                onThemeClick = { showThemeDialog = true },
                onStyleClick = { showStyleDialog = true },
                onOptionAction = { action ->
                    when (action) {
                        SettingsOptionAction.CHANGE_PASSWORD -> showPasswordDialog = true
                        SettingsOptionAction.DELETE_ACCOUNT -> showDeleteDialog = true
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LabasniPillButton(
            text = "Sign out",
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "You will be redirected to the sign-in screen.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TealAccent.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textAlign = TextAlign.Center
        )
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            selected = themeMode,
            onSelect = {
                onThemeChange(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showStyleDialog) {
        StyleThemeDialog(
            selected = themeVariant,
            onSelect = { variant ->
                ThemeController.setThemeVariant(variant)
                showStyleDialog = false
            },
            onDismiss = { showStyleDialog = false }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Confirm sign out",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            },
            text = {
                Text(
                    text = "Do you really want to sign out of your Styleto account?",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Déconnexion", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Annuler", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            isProcessing = isLoading,
            onConfirm = { newPassword ->
                val token = TokenManager.getToken(context)
                if (token == null) {
                    Toast.makeText(
                        context,
                        "Session expirée. Veuillez vous reconnecter.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@ChangePasswordDialog
                }

                profileViewModel.updateProfile(
                    token = token,
                    password = newPassword
                )
                showPasswordDialog = false
            },
            onDismiss = { showPasswordDialog = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showDeleteDialog = false
                }
            },
            title = {
                Text(
                    text = "Supprimer le compte",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PinkPrimary
                    )
                )
            },
            text = {
                Text(
                    text = "This action is permanent. Do you really want to delete your Styleto account?",
                    color = TealAccent
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val token = TokenManager.getToken(context)
                        if (token == null) {
                            Toast.makeText(
                                context,
                                "Session expirée. Veuillez vous reconnecter.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        }
                        profileViewModel.deleteAccount(token)
                        showDeleteDialog = false
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = PinkPrimary
                        )
                    } else {
                        Text(
                            text = "Supprimer",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = PinkPrimary
                            )
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (!isLoading) {
                            showDeleteDialog = false
                        }
                    }
                ) {
                    Text(
                        text = "Annuler",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TealAccent
                        )
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditProfileCard(
    user: User?,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onSave: (fullName: String, phone: String, gender: String) -> Unit
) {
    val context = LocalContext.current
    var expanded by rememberSaveable { mutableStateOf(true) }
    var fullName by rememberSaveable(user?.fullName) { mutableStateOf(user?.fullName.orEmpty()) }
    var phone by rememberSaveable(user?.phoneNumber) { mutableStateOf(user?.phoneNumber.orEmpty()) }
    var gender by rememberSaveable(user?.gender?.name) {
        mutableStateOf(user?.gender?.name ?: "FEMALE")
    }

    LaunchedEffect(user?.fullName, user?.phoneNumber, user?.gender) {
        fullName = user?.fullName.orEmpty()
        phone = user?.phoneNumber.orEmpty()
        gender = user?.gender?.name ?: "FEMALE"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Header (comme iOS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon circle (comme iOS)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PinkPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = PinkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                    Text(
                    text = "Edit Profile",
                        style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                            color = TealAccent
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = TealAccent.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Full Name",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TealAccent.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            )
                            CustomTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                placeholder = "Full Name"
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Email",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TealAccent.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            )
                            CustomTextField(
                                value = user?.email ?: "",
                                onValueChange = {},
                                placeholder = "Email",
                                enabled = false
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Phone",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TealAccent.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            )
                            CustomTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                placeholder = "Phone",
                                keyboardType = KeyboardType.Phone
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Gender",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TealAccent.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                GenderChip(
                                    title = "Female",
                                    selected = gender == "FEMALE",
                                    onClick = { gender = "FEMALE" }
                                )
                                GenderChip(
                                    title = "Male",
                                    selected = gender == "MALE",
                                    onClick = { gender = "MALE" }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                fullName = user?.fullName.orEmpty()
                                phone = user?.phoneNumber.orEmpty()
                                gender = user?.gender?.name ?: "FEMALE"
                                onCancel()
                            },
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                contentColor = TealAccent
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(vertical = 14.dp)
                            )
                        }
                        androidx.compose.material3.Button(
                            onClick = {
                                val trimmedName = fullName.trim()
                                val trimmedPhone = phone.trim()

                                if (trimmedName.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Le nom complet est requis.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                onSave(
                                    trimmedName,
                                    trimmedPhone,
                                    gender.lowercase()
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = PinkPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            enabled = fullName.isNotEmpty() && !isLoading
                        ) {
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(vertical = 14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipTag(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun SettingsSectionCard(
    section: SettingsSection,
    isExpanded: Boolean,
    themeMode: ThemeMode,
    onToggleExpand: () -> Unit,
    onThemeClick: () -> Unit,
    onStyleClick: () -> Unit,
    onOptionAction: (SettingsOptionAction) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Header (comme iOS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon circle (comme iOS)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                        imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                    )
                }

            // Options (comme iOS)
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    section.options.forEachIndexed { index, option ->
                    SettingsOptionRow(
                        option = option,
                        themeMode = themeMode,
                        onThemeClick = onThemeClick,
                        onStyleClick = onStyleClick,
                        onOptionAction = onOptionAction
                    )
                        if (index < section.options.size - 1) {
                            androidx.compose.material3.Divider(
                                modifier = Modifier.padding(start = 80.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsOptionRow(
    option: SettingsOption,
    themeMode: ThemeMode,
    onThemeClick: () -> Unit,
    onStyleClick: () -> Unit,
    onOptionAction: (SettingsOptionAction) -> Unit
) {
    var isChecked by rememberSaveable(option.label) { mutableStateOf(option.initialValue) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                when {
                    option.isThemePicker -> onThemeClick()
                    option.isStylePicker -> onStyleClick()
                    option.action != null -> onOptionAction(option.action)
                }
            }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon (comme iOS)
        Icon(
            imageVector = when {
                option.action == SettingsOptionAction.DELETE_ACCOUNT -> Icons.Rounded.DeleteForever
                option.action == SettingsOptionAction.CHANGE_PASSWORD -> Icons.Rounded.Lock
                option.label == "Notifications" -> Icons.Rounded.Notifications
                option.label == "Langue" || option.label == "Language" -> Icons.Rounded.Language
                option.label == "Taille du texte" || option.label == "Font Size" -> Icons.Rounded.TextFields
                option.label == "Thème" || option.label == "Theme" -> Icons.Rounded.DarkMode
                option.label == "Style préféré" || option.label == "Color Theme" -> Icons.Rounded.Palette
                option.label == "Animations" || option.label == "Animation Style" -> Icons.Rounded.AutoAwesome
                option.label == "Contact" || option.label == "Contact Us" -> Icons.Rounded.Email
                option.label == "FAQ" -> Icons.Rounded.Help
                option.label == "Version" -> Icons.Rounded.Info
                else -> Icons.Rounded.Settings
            },
            contentDescription = null,
            tint = if (option.action == SettingsOptionAction.DELETE_ACCOUNT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        
            Text(
                text = option.label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (option.action == SettingsOptionAction.DELETE_ACCOUNT) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 16.sp,
                color = if (option.action == SettingsOptionAction.DELETE_ACCOUNT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        when {
            option.hasToggle -> {
                androidx.compose.material3.Switch(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            option.isThemePicker -> {
                val label = when (themeMode) {
                    ThemeMode.LIGHT -> "Light"
                    ThemeMode.DARK -> "Dark"
                    ThemeMode.SYSTEM -> "System"
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 15.sp
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TealAccent.copy(alpha = 0.7f),
                    modifier = Modifier.size(12.dp)
                )
            }
            option.isStylePicker -> {
                val label = when (ThemeController.themeVariant.value) {
                    ThemeVariant.PINK -> "PINKTheme"
                    ThemeVariant.BLUE -> "BLEUTheme"
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 15.sp
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TealAccent.copy(alpha = 0.7f),
                    modifier = Modifier.size(12.dp)
                )
            }
            option.hasChevron -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TealAccent.copy(alpha = 0.7f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    isProcessing: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = {
            if (!isProcessing) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = "Modifier le mot de passe",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        localError = null
                    },
                    placeholder = "Nouveau mot de passe",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    enabled = !isProcessing
                )
                CustomTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        localError = null
                    },
                    placeholder = "Confirmer le mot de passe",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    enabled = !isProcessing
                )
                localError?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PinkPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmedPassword = newPassword.trim()
                    val trimmedConfirm = confirmPassword.trim()
                    when {
                        trimmedPassword.length < 6 -> {
                            localError = "Le mot de passe doit contenir au moins 6 caractères."
                        }
                        trimmedPassword != trimmedConfirm -> {
                            localError = "Les mots de passe ne correspondent pas."
                        }
                        else -> {
                            localError = null
                            onConfirm(trimmedPassword)
                        }
                    }
                },
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = PinkPrimary
                    )
                } else {
                    Text(
                        text = "Confirmer",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PinkPrimary
                        )
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (!isProcessing) {
                        onDismiss()
                    }
                }
            ) {
                Text(
                    text = "Annuler",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TealAccent
                    )
                )
            }
        }
    )
}

@Composable
private fun ThemePickerDialog(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose a theme",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ThemeMode.values().forEach { mode ->
                    val label = when (mode) {
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                        ThemeMode.SYSTEM -> "System"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (mode == selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        if (mode == selected) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        } else {
                            TextButton(onClick = { onSelect(mode) }) {
                                Text(text = "Choisir")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Fermer", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
private fun StyleThemeDialog(
    selected: ThemeVariant,
    onSelect: (ThemeVariant) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Preferred style",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                listOf(
                    ThemeVariant.PINK to "PINKTheme",
                    ThemeVariant.BLUE to "BLEUTheme"
                ).forEach { (variant, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (variant == selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        if (variant == selected) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        } else {
                            TextButton(onClick = { onSelect(variant) }) {
                                Text(text = "Choisir")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Fermer",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun LogoutCard(onLogout: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign out",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            )
            Text(
                text = "You can safely sign out of your Styleto account.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TealAccent.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            )
            androidx.compose.material3.Button(
                onClick = onLogout,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = PinkPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sign out", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun initials(name: String): String {
    return name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifEmpty { "LB" }
}

private suspend fun loadImageBytes(context: Context, uri: Uri): ByteArray? =
    withContext(Dispatchers.IO) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            val output = ByteArrayOutputStream()
            val compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
            if (!compressed) {
                output.close()
                return@withContext null
            }

            val bytes = output.toByteArray()
            output.close()
            bytes
        } catch (e: Exception) {
            null
        }
    }

// endregion

// region Shared components

@Composable
private fun SearchField(
    placeholder: String,
    leading: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            leading()
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TealAccent.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
private fun LabasniFilterChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) PinkPrimary else PinkGradientTop.copy(alpha = 0.5f)
    val content = if (selected) Color.White else TealAccent
    val interactionSource = remember { MutableInteractionSource() }

    Text(
        text = title,
        color = content,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

@Composable
private fun GenderChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) PinkPrimary else PinkGradientTop.copy(alpha = 0.3f)
    val content = if (selected) Color.White else TealAccent
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = content
        )
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = TealAccent.copy(alpha = 0.5f)
            )
        },
        enabled = enabled,
        readOnly = !enabled,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = TealAccent.copy(alpha = 0.2f),
            unfocusedBorderColor = TealAccent.copy(alpha = 0.2f),
            disabledBorderColor = TealAccent.copy(alpha = 0.1f),
            cursorColor = PinkPrimary,
            focusedTextColor = TealAccent,
            unfocusedTextColor = TealAccent,
            disabledTextColor = TealAccent.copy(alpha = 0.5f)
        )
    )
}

// endregion
