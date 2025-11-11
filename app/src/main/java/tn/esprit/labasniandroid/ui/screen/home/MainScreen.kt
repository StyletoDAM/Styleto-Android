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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import tn.esprit.labasniandroid.ui.screen.home.tabs.DressingTab
import tn.esprit.labasniandroid.ui.screen.home.tabs.SettingsTab
import tn.esprit.labasniandroid.ui.screen.home.tabs.StoreTab
import tn.esprit.labasniandroid.ui.screen.home.tabs.TenuesTab
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.ui.screen.profile.ProfileViewModel

enum class LabasniHomeTab {
    Dressing, Tenues, Avatar, Store, Settings
}

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var selectedTab by rememberSaveable { mutableStateOf(LabasniHomeTab.Dressing) }
    var tokenMissing by remember { mutableStateOf(false) }

    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val themeMode by ThemeController.themeMode.collectAsState()

    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context)
        if (token.isNullOrEmpty()) {
            tokenMissing = true
        } else {
            viewModel.loadProfile(token)
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
                LabasniHomeTab.Tenues -> TenuesTab()
                LabasniHomeTab.Avatar -> AvatarTab()
                LabasniHomeTab.Store -> StoreTab()
                LabasniHomeTab.Settings -> SettingsTab(
                    user = user,
                    themeMode = themeMode,
                    onThemeChange = ThemeController::setThemeMode,
                    onLogout = {
                        TokenManager.clearToken(context)
                        onLogout()
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
                .background(PinkGradientTop.copy(alpha = 0.3f))
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
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

                // Bouton central Avatar (plus grand et distinctif - comme iOS)
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
                            tint = Color.White,
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
    val iconColor = if (selected) PinkPrimary else TealAccent.copy(alpha = 0.7f)
    val textColor = if (selected) PinkPrimary else TealAccent.copy(alpha = 0.7f)
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
