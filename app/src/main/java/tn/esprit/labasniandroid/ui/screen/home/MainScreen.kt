package tn.esprit.labasniandroid.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.esprit.labasniandroid.ui.screen.home.tabs.AvatarTab
import tn.esprit.labasniandroid.ui.screen.dressing.DressingTab
import tn.esprit.labasniandroid.ui.screen.tenues.FavoriteTab
import tn.esprit.labasniandroid.ui.screen.tenues.TenuesTab
import tn.esprit.labasniandroid.ui.screen.tenues.TenuesViewModel
import tn.esprit.labasniandroid.ui.screen.settings.SettingsView
import tn.esprit.labasniandroid.ui.screen.store.StoreTab
import tn.esprit.labasniandroid.ui.screen.store.StoreViewModel
import tn.esprit.labasniandroid.ui.screen.store.cart.CartView
import tn.esprit.labasniandroid.ui.screen.store.messaging.MessagingView
import tn.esprit.labasniandroid.ui.screen.store.messaging.ChatDetailView
import tn.esprit.labasniandroid.models.entities.StoreItem
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.ui.screen.profile.ProfileViewModel

enum class LabasniHomeTab {
    Dressing, Tenues, Avatar, Store, Settings
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var selectedTab by rememberSaveable { mutableStateOf(LabasniHomeTab.Dressing) }
    var tokenMissing by remember { mutableStateOf(false) }
    var showFavorites by rememberSaveable { mutableStateOf(false) }
    var authToken by rememberSaveable { mutableStateOf("") }
    var userId by rememberSaveable { mutableStateOf("") }
    var showCart by rememberSaveable { mutableStateOf(false) }
    var showMessaging by rememberSaveable { mutableStateOf(false) }
    var showChatDetail by rememberSaveable { mutableStateOf(false) }
    var chatOwnerId by rememberSaveable { mutableStateOf<String?>(null) }
    var chatOwnerName by rememberSaveable { mutableStateOf<String?>(null) }
    var chatOwnerAvatar by rememberSaveable { mutableStateOf<String?>(null) }
    var chatStoreItem by rememberSaveable { mutableStateOf<StoreItem?>(null) }

    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val themeMode by ThemeController.themeMode.collectAsState()

    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context)
        val id = TokenManager.getUserId(context)
        
        // Log pour déboguer
        android.util.Log.d("MainScreen", "=== TokenManager Check ===")
        android.util.Log.d("MainScreen", "Token: ${if (token.isNullOrEmpty()) "NULL/EMPTY" else "EXISTS (length=${token.length})"}")
        android.util.Log.d("MainScreen", "UserId: ${if (id.isNullOrEmpty()) "NULL/EMPTY" else "'$id' (length=${id.length})"}")
        android.util.Log.d("MainScreen", "========================")
        
        if (token.isNullOrEmpty() || id.isNullOrEmpty()) {
            tokenMissing = true
        } else {
            authToken = token
            userId = id
            android.util.Log.d("MainScreen", "Setting userId for ChatDetailView: '$userId'")
            viewModel.loadProfile(token)
            // Synchroniser le thème avec le genre sauvegardé (comme iOS)
            ThemeController.syncThemeVariantWithSavedGender(context)
        }
    }
    
    // Synchroniser automatiquement le thème quand l'utilisateur est chargé (comme iOS)
    LaunchedEffect(user) {
        user?.let {
            ThemeController.syncThemeVariantWithGender(context, it.gender)
        }
    }

    LaunchedEffect(tokenMissing) {
        if (tokenMissing) {
            onLogout()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            if (it.isNotBlank()) {
                android.util.Log.e("LabasniMainScreen", it)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            LabasniBottomBar(
                selectedTab = selectedTab,
                onSelectTab = { tab ->
                    selectedTab = tab
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(PinkGradientTop.copy(alpha = 0.1f), Color.White)
                    )
                )
                .padding(padding)
        ) {
            when (selectedTab) {
                LabasniHomeTab.Dressing -> DressingTab(isLoading = isLoading)
                LabasniHomeTab.Tenues -> {
                    val tenuesViewModel: TenuesViewModel = viewModel()
                    if (authToken.isBlank() || userId.isBlank()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PinkPrimary)
                        }
                    } else if (showFavorites) {
                        FavoriteTab(
                            viewModel = tenuesViewModel,
                            onBack = { showFavorites = false }
                        )
                    } else {
                        TenuesTab(
                            token = authToken,
                            userId = userId,
                            viewModel = tenuesViewModel,
                            onBack = { selectedTab = LabasniHomeTab.Dressing },
                            onOpenFavorites = { showFavorites = true }
                        )
                    }
                }
                LabasniHomeTab.Avatar -> tn.esprit.labasniandroid.ui.screen.mirror.MirrorView()
                LabasniHomeTab.Store -> {
                    val storeViewModel: StoreViewModel = viewModel()
                    if (authToken.isBlank() || userId.isBlank()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PinkPrimary)
                        }
                    } else {
                        when {
                            showCart -> CartView(
                                token = authToken,
                                userId = userId,
                                onNavigateBack = { showCart = false }
                            )
                            showMessaging -> MessagingView(
                                token = authToken,
                                userId = userId,
                                onNavigateBack = { showMessaging = false },
                                onNavigateToChat = { ownerId, ownerName, ownerAvatar ->
                                    android.util.Log.d("MainScreen", "=== NAVIGATING TO CHAT FROM MESSAGING ===")
                                    android.util.Log.d("MainScreen", "ownerId: $ownerId")
                                    android.util.Log.d("MainScreen", "ownerName: $ownerName")
                                    android.util.Log.d("MainScreen", "ownerAvatar: $ownerAvatar")
                                    
                                    chatOwnerId = ownerId
                                    chatOwnerName = ownerName ?: "Utilisateur"
                                    chatOwnerAvatar = ownerAvatar
                                    chatStoreItem = null
                                    showMessaging = false
                                    showChatDetail = true
                                    
                                    android.util.Log.d("MainScreen", "✅ Navigation set: showChatDetail=true, chatOwnerId=$chatOwnerId, chatOwnerName=$chatOwnerName")
                                }
                            )
                            showChatDetail && chatOwnerId != null -> {
                                android.util.Log.d("MainScreen", "=== RENDERING ChatDetailView ===")
                                android.util.Log.d("MainScreen", "chatOwnerId: $chatOwnerId")
                                android.util.Log.d("MainScreen", "chatOwnerName: $chatOwnerName")
                                android.util.Log.d("MainScreen", "chatOwnerAvatar: $chatOwnerAvatar")
                                
                                ChatDetailView(
                                    token = authToken,
                                    userId = userId,
                                    ownerId = chatOwnerId!!,
                                    ownerName = chatOwnerName,
                                    ownerAvatar = chatOwnerAvatar,
                                    storeItem = chatStoreItem,
                                    onNavigateBack = {
                                        android.util.Log.d("MainScreen", "ChatDetailView: Navigating back")
                                        showChatDetail = false
                                        chatOwnerId = null
                                        chatOwnerName = null
                                        chatOwnerAvatar = null
                                        chatStoreItem = null
                                    },
                                    onNavigateToConversation = { newOwnerId, newOwnerName, newOwnerAvatar ->
                                        android.util.Log.d("MainScreen", "ChatDetailView: Navigating to new conversation")
                                        chatOwnerId = newOwnerId
                                        chatOwnerName = newOwnerName ?: "Utilisateur"
                                        chatOwnerAvatar = newOwnerAvatar
                                        chatStoreItem = null
                                    }
                                )
                            }
                            else -> StoreTab(
                                token = authToken,
                                userId = userId,
                                viewModel = storeViewModel,
                                onNavigateToCart = { showCart = true },
                                onNavigateToMessaging = { showMessaging = true },
                                onContactOwner = { storeItem ->
                                    chatOwnerId = storeItem.ownerId
                                    chatOwnerName = storeItem.ownerName
                                    chatOwnerAvatar = storeItem.ownerAvatar
                                    chatStoreItem = storeItem
                                    showChatDetail = true
                                }
                            )
                        }
                    }
                }
                LabasniHomeTab.Settings -> SettingsView(
                    user = user,
                    themeMode = themeMode,
                    onThemeChange = ThemeController::setThemeMode,
                    onLogout = {
                        TokenManager.clearToken(context)
                        onLogout()
                    },
                    onUserUpdated = { updatedUser ->
                        // Sauvegarder le genre dans TokenManager pour la synchronisation future
                        TokenManager.saveGender(context, updatedUser.gender.value)
                        // Synchroniser automatiquement le thème avec le genre mis à jour (comme iOS)
                        ThemeController.syncThemeVariantWithGender(context, updatedUser.gender)
                    }
                )
            }
        }
    }
}

@Composable
private fun LabasniBottomBar(
    selectedTab: LabasniHomeTab,
    onSelectTab: (LabasniHomeTab) -> Unit
) {
    val navigationPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Ligne de séparation (comme iOS)
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 12.dp)
                    .padding(bottom = 8.dp + navigationPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Dressing
                TabButton(
                    iconFilled = Icons.Filled.Checkroom,
                    iconOutlined = Icons.Outlined.Checkroom,
                    label = "Dressing",
                    selected = selectedTab == LabasniHomeTab.Dressing,
                    onClick = { onSelectTab(LabasniHomeTab.Dressing) }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Tenues
                TabButton(
                    iconFilled = Icons.Filled.People,
                    iconOutlined = Icons.Outlined.People,
                    label = "Tenues",
                    selected = selectedTab == LabasniHomeTab.Tenues,
                    onClick = { onSelectTab(LabasniHomeTab.Tenues) }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Bouton central - ouvre le mode miroir (caméra)
                androidx.compose.material3.IconButton(
                    onClick = { onSelectTab(LabasniHomeTab.Avatar) },
                    modifier = Modifier.offset(y = (-20).dp)
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PinkPrimary)
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Avatar",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Store
                TabButton(
                    iconFilled = Icons.Filled.ShoppingBag,
                    iconOutlined = Icons.Outlined.ShoppingBag,
                    label = "Store",
                    selected = selectedTab == LabasniHomeTab.Store,
                    onClick = { onSelectTab(LabasniHomeTab.Store) }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Settings
                TabButton(
                    iconFilled = Icons.Filled.Person,
                    iconOutlined = Icons.Outlined.Person,
                    label = "Settings",
                    selected = selectedTab == LabasniHomeTab.Settings,
                    onClick = { onSelectTab(LabasniHomeTab.Settings) }
                )
            }
        }
    }
}

@Composable
private fun TabButton(
    iconFilled: androidx.compose.ui.graphics.vector.ImageVector,
    iconOutlined: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val icon = if (selected) iconFilled else iconOutlined
    val iconColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val textColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val textWeight = if (selected) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Normal

    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = Modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
            contentColor = Color.Transparent
        )
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            androidx.compose.material3.Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = textWeight
                ),
                color = textColor
            )
        }
    }
}
