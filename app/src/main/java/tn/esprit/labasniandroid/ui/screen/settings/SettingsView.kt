package tn.esprit.labasniandroid.ui.screen.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.ui.screen.profile.ProfileViewModel
import tn.esprit.labasniandroid.ui.screen.orders.OrdersHistoryView
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeMode
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.TokenManager
import java.io.ByteArrayOutputStream

// MARK: - Models
enum class StylePreference(val displayName: String) {
    CASUAL("Casual"),
    CHIC("Chic"),
    SPORT("Sport"),
    BOHEME("Bohème"),
    MINIMAL("Minimal")
}

data class SettingsSection(
    val id: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val options: List<SettingsOption>,
    val isEditProfile: Boolean = false
)

data class SettingsOption(
    val id: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val hasToggle: Boolean = false,
    val toggleValue: Boolean = false,
    val hasChevron: Boolean = true
)

// MARK: - Settings View
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    user: User?,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onLogout: () -> Unit,
    onUserUpdated: (User) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val viewModel: ProfileViewModel = viewModel()
    val viewModelUser by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val accountDeleted by viewModel.accountDeleted.collectAsState()
    val isPhotoUpdating by viewModel.isPhotoUpdating.collectAsState()
    
    val activeUser = viewModelUser ?: user
    
    // Déterminer isMale depuis ThemeVariant (BLUE = MALE, PINK = FEMALE)
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    
    // Couleurs dynamiques
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeSecondary = DynamicThemeColors.secondary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()
    val themeSoftPink = DynamicThemeColors.softPink(isMale)
    
    // State
    var expandedSections by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showColorThemePicker by remember { mutableStateOf(false) }
    var showContactUsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showSaveConfirmation by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var showErrorAlert by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    var showOrdersHistory by remember { mutableStateOf(false) }
    var showPhotoConfirmation by remember { mutableStateOf(false) }
    var pendingPhotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var pendingPhotoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showImageSourcePicker by remember { mutableStateOf(false) }
    var showPasswordUpdate by remember { mutableStateOf(false) }
    var showDeletePhotoConfirmation by remember { mutableStateOf(false) }
    var showBalanceTopUp by remember { mutableStateOf(false) }
    
    // Edit Profile State
    var fullName by rememberSaveable { mutableStateOf(activeUser?.fullName ?: "") }
    var email by rememberSaveable { mutableStateOf(activeUser?.email ?: "") }
    var phone by rememberSaveable { mutableStateOf(activeUser?.phoneNumber ?: "") }
    var gender by rememberSaveable { mutableStateOf(activeUser?.gender ?: User.Gender.FEMALE) }
    var selectedStyles by remember { mutableStateOf<Set<StylePreference>>(emptySet()) }
    var password by rememberSaveable { mutableStateOf("") }
    
    // Original values for cancel
    var originalFullName by rememberSaveable { mutableStateOf(activeUser?.fullName ?: "") }
    var originalPhone by rememberSaveable { mutableStateOf(activeUser?.phoneNumber ?: "") }
    var originalGender by rememberSaveable { mutableStateOf(activeUser?.gender ?: User.Gender.FEMALE) }
    var originalStyles by remember { mutableStateOf<Set<StylePreference>>(emptySet()) }
    
    // Initialize styles from user preferences
    LaunchedEffect(activeUser?.preferences) {
        val userStyles = activeUser?.preferences ?: emptyList()
        val mapped = userStyles.mapNotNull { pref ->
            when (pref.lowercase()) {
                "casual" -> StylePreference.CASUAL
                "chic" -> StylePreference.CHIC
                "sport" -> StylePreference.SPORT
                "boheme", "bohème" -> StylePreference.BOHEME
                "minimal" -> StylePreference.MINIMAL
                else -> null
            }
        }.toSet()
        selectedStyles = mapped
        originalStyles = mapped
    }
    
    // Update form when user changes
    LaunchedEffect(activeUser) {
        activeUser?.let {
            fullName = it.fullName
            email = it.email
            phone = it.phoneNumber ?: ""
            gender = it.gender
            originalFullName = it.fullName
            originalPhone = it.phoneNumber ?: ""
            originalGender = it.gender
            // Synchroniser automatiquement le thème avec le genre de l'utilisateur (comme iOS)
            ThemeController.syncThemeVariantWithGender(context, it.gender)
        }
    }
    
    // Load profile on init
    LaunchedEffect(Unit) {
        TokenManager.getToken(context)?.let { token ->
            viewModel.loadProfile(token)
        }
    }
    
    // Handle errors and success
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
            viewModel.clearMessages()
        }
    }
    
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
            
            if (accountDeleted) {
                TokenManager.clearToken(context)
                // Vider le panier lors de la suppression de compte (comme iOS)
                tn.esprit.labasniandroid.utils.CartManager.handleLogout()
                viewModel.acknowledgeAccountDeleted()
                onLogout()
            } else {
                viewModelUser?.let { updatedUser ->
                    // Sauvegarder le genre dans TokenManager pour la synchronisation future
                    TokenManager.saveGender(context, updatedUser.gender.value)
                    // Notifier MainScreen pour synchroniser le thème
                    onUserUpdated(updatedUser)
                }
            }
            
            viewModel.clearMessages()
        }
    }
    
    // Photo pickers
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                
                if (bytes != null) {
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    withContext(Dispatchers.Main) {
                        pendingPhotoBitmap = bitmap
                        pendingPhotoBytes = bytes
                        showPhotoConfirmation = true
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val baos = ByteArrayOutputStream()
            val ok = bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
            val bytes = if (ok) baos.toByteArray() else null
            baos.close()
            withContext(Dispatchers.Main) {
                if (bytes != null) {
                    pendingPhotoBitmap = bitmap
                    pendingPhotoBytes = bytes
                    showPhotoConfirmation = true
                } else {
                    Toast.makeText(context, "Capture échouée", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(context, "Autorisez la caméra pour prendre une photo", Toast.LENGTH_SHORT).show()
        }
    }
    
    val hasChanges = fullName != originalFullName ||
            phone != originalPhone ||
            gender != originalGender ||
            selectedStyles != originalStyles
    
    val hasProfilePhoto = !activeUser?.profilePicture.isNullOrBlank()
    
    // Sections
    val sections = remember {
        listOf(
            SettingsSection(
                id = "edit_profile",
                icon = Icons.Rounded.Person,
                title = "Edit Profile",
                options = emptyList(),
                isEditProfile = true
            ),
            SettingsSection(
                id = "app_settings",
                icon = Icons.Rounded.Settings,
                title = "App Settings",
                options = listOf(
                    SettingsOption("notifications", Icons.Rounded.Notifications, "Notifications", hasToggle = true, toggleValue = true, hasChevron = false),
                    SettingsOption("language", Icons.Rounded.Language, "Language", hasChevron = true)
                )
            ),
            SettingsSection(
                id = "preferences",
                icon = Icons.Rounded.Palette,
                title = "Preferences",
                options = listOf(
                    SettingsOption("theme", Icons.Rounded.DarkMode, "Theme", hasChevron = true),
                    SettingsOption("color_theme", Icons.Rounded.Palette, "Color Theme", hasChevron = true)
                )
            ),
            SettingsSection(
                id = "security",
                icon = Icons.Rounded.Lock,
                title = "Security",
                options = listOf(
                    SettingsOption("change_password", Icons.Rounded.Key, "Change Password", hasChevron = true),
                    SettingsOption("delete_account", Icons.Rounded.Delete, "Delete Account", hasChevron = true)
                )
            ),
            SettingsSection(
                id = "help_support",
                icon = Icons.Rounded.Info,
                title = "Help & Support",
                options = listOf(
                    SettingsOption("contact", Icons.Rounded.Email, "Contact Us", hasChevron = true),
                    SettingsOption("about", Icons.Rounded.Info, "About", hasChevron = true),
                    SettingsOption("version", Icons.Rounded.Info, "Version 1.0.0", hasChevron = false)
                )
            )
        )
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header "Settings" (comme iOS)
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = themePrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            
            // Balance Card - Innovative and Beautiful
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    themePrimary.copy(alpha = 0.8f),
                                    themeTeal.copy(alpha = 0.9f),
                                    themeSecondary.copy(alpha = 0.7f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { showBalanceTopUp = true }
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Mon Solde",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            
                            Text(
                                text = "${String.format("%.2f", activeUser?.balance ?: 0.0)} TND",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                            
                            Text(
                                text = "Disponible pour retrait",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccountBalanceWallet,
                                contentDescription = "Balance",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
            
            // Profile Photo (comme iOS)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(themePrimary.copy(alpha = 0.15f))
                        .clickable { showImageSourcePicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isPhotoUpdating -> {
                            CircularProgressIndicator(
                                color = themePrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        !activeUser?.profilePicture.isNullOrBlank() -> {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(activeUser?.profilePicture)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Text(
                                text = initials(activeUser?.fullName ?: "User"),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = themePrimary
                            )
                        }
                    }
                    
                    // Camera icon overlay (comme iOS)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(themePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CameraAlt,
                                contentDescription = "Edit Photo",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = themeText
                )
            }
            
            // Sections
            sections.forEach { section ->
                if (section.isEditProfile) {
                    EditProfileSectionCard(
                        isExpanded = expandedSections.contains(section.id),
                        onToggle = {
                            expandedSections = if (expandedSections.contains(section.id)) {
                                expandedSections - section.id
                            } else {
                                expandedSections + section.id
                            }
                        },
                        fullName = fullName,
                        onFullNameChange = { newValue -> fullName = newValue },
                        email = email,
                        phone = phone,
                        onPhoneChange = { newValue -> phone = newValue },
                        gender = gender,
                        onGenderChange = { newValue -> gender = newValue },
                        selectedStyles = selectedStyles,
                        onStyleToggle = { style ->
                            selectedStyles = if (selectedStyles.contains(style)) {
                                selectedStyles - style
                            } else {
                                selectedStyles + style
                            }
                        },
                        onSave = { showSaveConfirmation = true },
                        onCancel = {
                            fullName = originalFullName
                            phone = originalPhone
                            gender = originalGender
                            selectedStyles = originalStyles
                        },
                        hasChanges = hasChanges,
                        isLoading = isLoading,
                        themePrimary = themePrimary,
                        themeSecondary = themeSecondary,
                        themeTeal = themeTeal,
                        themeCard = themeCard,
                        themeText = themeText,
                        themeSecondaryText = themeSecondaryText,
                        themeSoftPink = themeSoftPink,
                        themeBackground = themeBackground
                    )
                } else {
                    SettingsSectionCard(
                        section = section,
                        isExpanded = expandedSections.contains(section.id),
                        onToggle = {
                            expandedSections = if (expandedSections.contains(section.id)) {
                                expandedSections - section.id
                            } else {
                                expandedSections + section.id
                            }
                        },
                        themeMode = themeMode,
                        themePrimary = themePrimary,
                        themeSecondary = themeSecondary,
                        themeTeal = themeTeal,
                        themeCard = themeCard,
                        themeText = themeText,
                        themeSecondaryText = themeSecondaryText,
                        onThemeClick = { showThemePicker = true },
                        onColorThemeClick = { showColorThemePicker = true },
                        onContactUsClick = { showContactUsDialog = true },
                        onAboutClick = { showAboutDialog = true },
                        onPasswordChange = { showPasswordUpdate = true },
                        onDeleteAccount = { showDeleteConfirmation = true },
                        viewModel = viewModel,
                        context = context
                    )
                }
            }
            
            // Order History Card - En haut des packages
            OrderHistoryCard(
                themePrimary = themePrimary,
                themeCard = themeCard,
                themeText = themeText,
                themeSecondaryText = themeSecondaryText,
                themeBackground = themeBackground,
                onNavigateToOrders = { showOrdersHistory = true }
            )
            
            // Pack Profile Cards (comme iOS)
            PackProfileCard()
            
            // Logout Button (comme iOS)
            Button(
                onClick = { showLogoutConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themePrimary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    
    // Dialogs and Sheets
    if (showThemePicker) {
        ThemePickerSheet(
            themeMode = themeMode,
            onThemeSelected = { mode ->
                onThemeChange(mode)
                showThemePicker = false
            },
            onDismiss = { showThemePicker = false },
            themePrimary = themePrimary,
            themeCard = themeCard,
            themeText = themeText,
            themeBackground = themeBackground
        )
    }
    
    if (showColorThemePicker) {
        ColorThemePickerSheet(
            currentVariant = ThemeController.themeVariant.collectAsState().value,
            onVariantSelected = { variant ->
                ThemeController.setThemeVariant(variant)
                showColorThemePicker = false
            },
            onDismiss = { showColorThemePicker = false },
            themePrimary = themePrimary,
            themeCard = themeCard,
            themeText = themeText,
            themeBackground = themeBackground
        )
    }
    
    if (showContactUsDialog) {
        ContactUsDialog(
            onDismiss = { showContactUsDialog = false },
            themePrimary = themePrimary,
            themeCard = themeCard,
            themeText = themeText,
            themeBackground = themeBackground
        )
    }
    
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false },
            themePrimary = themePrimary,
            themeCard = themeCard,
            themeText = themeText
        )
    }
    
    if (showContactUsDialog) {
        ContactUsDialog(
            onDismiss = { showContactUsDialog = false },
            themePrimary = themePrimary,
            themeCard = themeCard,
            themeText = themeText,
            themeBackground = themeBackground
        )
    }
    
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false },
            themePrimary = themePrimary,
            themeCard = themeCard,
            themeText = themeText
        )
    }
    
    // Save Confirmation
    if (showSaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showSaveConfirmation = false },
            title = {
                Text(
                    text = "Confirm Modification",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text("Do you really want to save these changes?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSaveConfirmation = false
                        val token = TokenManager.getToken(context)
                        if (token != null) {
                            val genderString = if (gender == User.Gender.MALE) "male" else "female"
                            val styleStrings = selectedStyles.map { it.displayName.lowercase() }
                            viewModel.updateProfile(
                                token = token,
                                fullName = fullName.trim().takeIf { it.isNotEmpty() },
                                phoneNumber = phone.trim().takeIf { it.isNotEmpty() },
                                gender = genderString,
                                preferences = styleStrings.takeIf { it.isNotEmpty() }
                            )
                            // Synchroniser automatiquement le thème avec le nouveau genre (comme iOS)
                            ThemeController.syncThemeVariantWithGender(context, gender)
                            originalFullName = fullName
                            originalPhone = phone
                            originalGender = gender
                            originalStyles = selectedStyles
                        }
                    }
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveConfirmation = false }) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
    
    // Success Alert
    if (showSuccessAlert) {
        AlertDialog(
            onDismissRequest = { showSuccessAlert = false },
            title = { Text("Success", fontWeight = FontWeight.Bold) },
            text = { Text(successMessage ?: "Profile updated successfully.") },
            confirmButton = {
                TextButton(onClick = { showSuccessAlert = false }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
    
    // Error Alert
    if (showErrorAlert) {
        AlertDialog(
            onDismissRequest = { showErrorAlert = false },
            title = { Text("Error", fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage ?: "An error has occurred.") },
            confirmButton = {
                TextButton(onClick = { showErrorAlert = false }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
    
    // Logout Confirmation
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Do you really want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmation = false
                        TokenManager.clearToken(context)
                        // Vider le panier lors du logout (comme iOS)
                        tn.esprit.labasniandroid.utils.CartManager.handleLogout()
                        onLogout()
                    }
                ) {
                    Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
    
    // Photo Source Picker
    if (showImageSourcePicker) {
        AlertDialog(
            onDismissRequest = { showImageSourcePicker = false },
            title = { Text("Choose an option") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(
                        onClick = {
                            showImageSourcePicker = false
                            val granted = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) {
                                takePictureLauncher.launch(null)
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    ) {
                        Text("Take a new photo", fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(
                        onClick = {
                            showImageSourcePicker = false
                            pickImageLauncher.launch("image/*")
                        }
                    ) {
                        Text("Choose from gallery", fontWeight = FontWeight.SemiBold)
                    }
                    if (hasProfilePhoto) {
                        TextButton(
                            onClick = {
                                showImageSourcePicker = false
                                showDeletePhotoConfirmation = true
                            }
                        ) {
                            Text("Delete photo", color = Color.Red, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageSourcePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Photo Confirmation
    if (showPhotoConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showPhotoConfirmation = false
                pendingPhotoBitmap = null
                pendingPhotoBytes = null
            },
            title = { Text("Confirm Photo Change", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    pendingPhotoBitmap?.let { bitmap ->
                        androidx.compose.foundation.Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        text = "Do you really want to change your profile picture?",
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isPhotoUpdating && pendingPhotoBytes != null,
                    onClick = {
                        val token = TokenManager.getToken(context)
                        val bytes = pendingPhotoBytes
                        if (token != null && bytes != null) {
                            showPhotoConfirmation = false
                            viewModel.uploadProfilePhoto(token, bytes)
                            pendingPhotoBitmap = null
                            pendingPhotoBytes = null
                        }
                    }
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPhotoConfirmation = false
                        pendingPhotoBitmap = null
                        pendingPhotoBytes = null
                    }
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
    
    // Delete Photo Confirmation
    if (showDeletePhotoConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeletePhotoConfirmation = false },
            title = { Text("Delete profile photo", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete your profile picture?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeletePhotoConfirmation = false
                        val token = TokenManager.getToken(context)
                        if (token != null) {
                            viewModel.setProfilePictureFromUrl(token, "")
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePhotoConfirmation = false }) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
    
    // Delete Account Confirmation
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete account", fontWeight = FontWeight.Bold) },
            text = { Text("Do you really want to delete your account? This action is irreversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        val token = TokenManager.getToken(context)
                        if (token != null) {
                            viewModel.deleteAccount(token)
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
    
    // Password Update Dialog
    if (showPasswordUpdate) {
        ChangePasswordDialog(
            password = password,
            onPasswordChange = { newValue -> password = newValue },
            onConfirm = {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    viewModel.updateProfile(token = token, password = password)
                    showPasswordUpdate = false
                    password = ""
                }
            },
            onDismiss = {
                showPasswordUpdate = false
                password = ""
            },
            isLoading = isLoading,
            themePrimary = themePrimary,
            themeCard = themeCard,
            themeText = themeText,
            themeSecondaryText = themeSecondaryText,
            themeSoftPink = themeSoftPink,
            themeBackground = themeBackground
        )
    }
    
    // Auto-show alerts
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            showSuccessAlert = true
        }
    }
    
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showErrorAlert = true
        }
    }
    
    // Balance Top Up Sheet
    if (showBalanceTopUp) {
        val token = TokenManager.getToken(context)
        if (token != null) {
            BalanceTopUpSheet(
                onDismiss = { showBalanceTopUp = false },
                viewModel = viewModel,
                token = token
            )
        } else {
            // Si pas de token, fermer le popup
            showBalanceTopUp = false
        }
    }
    
    // Orders History View - Full screen overlay
    if (showOrdersHistory) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeBackground)
        ) {
            OrdersHistoryView(
                onBackClick = { showOrdersHistory = false }
            )
        }
    }
}

// MARK: - Order History Card
@Composable
fun OrderHistoryCard(
    themePrimary: Color,
    themeCard: Color,
    themeText: Color,
    themeSecondaryText: Color,
    themeBackground: Color,
    onNavigateToOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = themePrimary.copy(alpha = 0.3f)
            ),
        colors = CardDefaults.cardColors(containerColor = themeCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToOrders() }
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icône avec gradient
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    themePrimary.copy(alpha = 0.9f),
                                    themePrimary.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = "Order History",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Texte
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Order History",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = themeText
                    )
                    
                    Text(
                        text = "View all your past orders",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = themeSecondaryText
                    )
                }
                
                // Chevron
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "View orders",
                    tint = themePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Helper function
private fun initials(name: String): String {
    return name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
}

