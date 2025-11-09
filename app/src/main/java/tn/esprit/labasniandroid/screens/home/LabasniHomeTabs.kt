package tn.esprit.labasniandroid.screens.home.tabs

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.labasniandroid.models.User
import tn.esprit.labasniandroid.screens.LabasniOutlinedField
import tn.esprit.labasniandroid.screens.LabasniPillButton
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.PinkSecondary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.ui.theme.ThemeMode
 
// region Dressing

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun DressingTab(
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
                        val selected = index == 0 || category == selectedCategory
                        CategoryChip(
                            label = category,
                        selected = selected,
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
                    ClothingCard(item = item)
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
private fun CategoryChip(
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
private fun ClothingCard(item: ClothingItem) {
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

@Composable
fun TenuesTab(modifier: Modifier = Modifier) {
    val outfits = remember { StaticTenue.samples }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = "Mes Tenues",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = PinkPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        SuggestionCard()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tenues récentes",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TealAccent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(outfits.size) { index ->
                TenueCard(tenue = outfits[index])
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        androidx.compose.material3.FloatingActionButton(
            onClick = { /* TODO Ajouter tenue */ },
            containerColor = TealAccent,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "+", fontWeight = FontWeight.Bold)
        }
    }
}

private data class StaticTenue(
    val title: String,
    val itemsCount: Int,
    val dateLabel: String,
    val isFavorite: Boolean,
    val emojis: List<String>
) {
    companion object {
        val samples = listOf(
            StaticTenue("Look Casual", 3, "Aujourd'hui", true, listOf("👕", "👖", "👟")),
            StaticTenue("Tenue Bureau", 4, "Hier", false, listOf("👔", "👖", "🥿")),
            StaticTenue("Sport", 2, "Mar.", true, listOf("👕", "👟"))
        )
    }
}

@Composable
private fun SuggestionCard() {
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
        OutlinedButton(
            onClick = { /* TODO action suggestion */ },
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Text(text = "Voir la suggestion")
        }
    }
}

@Composable
private fun TenueCard(tenue: StaticTenue) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = tenue.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TealAccent
                        )
                    )
                    Text(
                        text = "${tenue.itemsCount} articles",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TealAccent.copy(alpha = 0.7f))
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = if (tenue.isFavorite) PinkPrimary else TealAccent.copy(alpha = 0.4f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                tenue.emojis.forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (emoji == "👖") AquaSoft.copy(alpha = 0.3f) else PinkGradientTop.copy(alpha = 0.4f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    tint = TealAccent.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tenue.dateLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TealAccent.copy(alpha = 0.7f))
                )
            }
        }
    }
}

// endregion

// region Store

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreTab(modifier: Modifier = Modifier) {
    val products = remember { StoreProduct.samples }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AquaSoft.copy(alpha = 0.18f))
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
            androidx.compose.material3.FloatingActionButton(
                onClick = { /* TODO add product */ },
                containerColor = PinkPrimary,
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                Text(text = "+", fontWeight = FontWeight.Bold)
            }
        }

        SearchField(placeholder = "Rechercher un article...") {
            Icon(
                imageVector = Icons.Rounded.LocalMall,
                contentDescription = null,
                tint = TealAccent
            )
        }

        Text(
            text = "Articles populaires",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TealAccent
            )
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductCard(product = product)
            }
        }
    }
}

private data class StoreProduct(
    val title: String,
    val price: String,
    val rating: Double,
    val emoji: String
) {
    companion object {
        val samples = listOf(
            StoreProduct("Pull tricoté", "45 DT", 4.5, "🧶"),
            StoreProduct("Jean slim", "65 DT", 4.8, "👖"),
            StoreProduct("Chemise", "59 DT", 4.2, "👔"),
            StoreProduct("Veste été", "120 DT", 4.7, "🧥"),
            StoreProduct("T-shirt coton", "35 DT", 4.1, "👕"),
            StoreProduct("Parka", "210 DT", 4.9, "🧥")
        )
    }
}

@Composable
private fun ProductCard(product: StoreProduct) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
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
                Text(text = product.emoji, fontSize = 48.sp)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TealAccent
                    )
                )
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PinkPrimary
                    )
                )
                Text(
                    text = "⭐ ${product.rating}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TealAccent.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

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
                text = "Bientôt disponible",
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
    val isThemePicker: Boolean = false
)

@Composable
fun SettingsTab(
    user: User?,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedSections = remember { mutableStateListOf<String>() }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                    SettingsOption("Style préféré", hasChevron = true),
                    SettingsOption("Animations", hasToggle = true)
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
            .background(Color.White)
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
                color = PinkPrimary
            )
        )
            
            // Icône de déconnexion (comme iOS)
            androidx.compose.material3.IconButton(
                onClick = { showLogoutDialog = true }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                    contentDescription = "Déconnexion",
                    tint = PinkPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        EditProfileCard(user = user)

        sections.forEach { section ->
            val isExpanded = expandedSections.contains(section.title)
            SettingsSectionCard(
                section = section,
                isExpanded = isExpanded,
                themeMode = themeMode,
                onToggleExpand = {
                    if (isExpanded) expandedSections.remove(section.title) else expandedSections.add(section.title)
                },
                onThemeClick = { showThemeDialog = true }
            )
        }
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

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Confirmer la déconnexion",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PinkPrimary
                    )
                )
            },
            text = {
                Text(
                    text = "Voulez-vous vraiment vous déconnecter de votre compte Labasni ?",
                    color = TealAccent
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Déconnexion", color = PinkPrimary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Annuler", color = TealAccent, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditProfileCard(user: User?) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    var fullName by rememberSaveable(user?.fullName) { mutableStateOf(user?.fullName.orEmpty()) }
    var phone by rememberSaveable(user?.phoneNumber) { mutableStateOf(user?.phoneNumber.orEmpty()) }
    var password by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable(user?.gender?.name) {
        mutableStateOf(user?.gender?.name ?: "FEMALE")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    // Avatar section (comme iOS)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(PinkPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials(user?.fullName ?: "User"),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PinkPrimary
                                )
                            )
                        }
                        TextButton(onClick = { /* TODO: Change photo */ }) {
                            Text(
                                text = "Change photo",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = PinkPrimary
                                )
                            )
                        }
                    }

                    androidx.compose.material3.Divider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = TealAccent.copy(alpha = 0.2f)
                    )

                    // Editable fields (comme iOS)
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        // Full Name
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

                        // Email (non modifiable)
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

                        // Phone
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
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                            )
                        }

                        // Gender
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

                        // Password
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (password.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "New Password",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = TealAccent.copy(alpha = 0.7f),
                                            fontSize = 14.sp
                                        )
                                    )
                                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                                        placeholder = "New Password",
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                                        isPassword = true
                                    )
                                }
                            }
                            TextButton(onClick = { password = if (password.isEmpty()) " " else "" }) {
                                Text(
                                    text = if (password.isEmpty()) "Update password" else "Cancel password update",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = PinkPrimary,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }
                    }

                    androidx.compose.material3.Divider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = TealAccent.copy(alpha = 0.2f)
                    )

                    // Action buttons (comme iOS)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* TODO: Cancel */ },
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
                            onClick = { /* TODO: Save */ },
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = PinkPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            enabled = fullName.isNotEmpty()
                    ) {
                            Text(
                                text = "Save changes",
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
            .background(PinkGradientTop.copy(alpha = 0.4f))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TealAccent,
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
    onThemeClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        .background(PinkPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = PinkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                    Text(
                        text = section.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TealAccent
                    ),
                    modifier = Modifier.weight(1f)
                )
                    Icon(
                        imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                    tint = TealAccent.copy(alpha = 0.7f),
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
                        onThemeClick = onThemeClick
                        )
                        if (index < section.options.size - 1) {
                            androidx.compose.material3.Divider(
                                modifier = Modifier.padding(start = 80.dp),
                                color = TealAccent.copy(alpha = 0.2f)
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
    onThemeClick: () -> Unit
) {
    var isChecked by rememberSaveable(option.label) { mutableStateOf(option.initialValue) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (option.isThemePicker) {
                    onThemeClick()
                }
            }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon (comme iOS)
        Icon(
            imageVector = when (option.label) {
                "Notifications" -> Icons.Rounded.Notifications
                "Langue", "Language" -> Icons.Rounded.Language
                "Taille du texte", "Font Size" -> Icons.Rounded.TextFields
                "Thème", "Theme" -> Icons.Rounded.DarkMode
                "Style préféré", "Color Theme" -> Icons.Rounded.Palette
                "Animations", "Animation Style" -> Icons.Rounded.AutoAwesome
                "Contact", "Contact Us" -> Icons.Rounded.Email
                "FAQ" -> Icons.Rounded.Help
                "Version" -> Icons.Rounded.Info
                else -> Icons.Rounded.Settings
            },
            contentDescription = null,
            tint = TealAccent.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        
            Text(
                text = option.label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = TealAccent
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
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PinkPrimary
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
                        color = TealAccent.copy(alpha = 0.7f),
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
private fun ThemePickerDialog(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choisir un thème",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ThemeMode.values().forEach { mode ->
                    val label = when (mode) {
                        ThemeMode.LIGHT -> "Clair"
                        ThemeMode.DARK -> "Sombre"
                        ThemeMode.SYSTEM -> "Système"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (mode == selected) PinkGradientTop.copy(alpha = 0.35f) else Color.Transparent
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = TealAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        if (mode == selected) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = PinkPrimary,
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
                Text(text = "Fermer", color = PinkPrimary, fontWeight = FontWeight.SemiBold)
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
                text = "Déconnexion",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            )
            Text(
                text = "Vous pouvez vous déconnecter de votre compte Labasni en toute sécurité.",
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
                Text(text = "Se déconnecter", fontWeight = FontWeight.SemiBold)
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
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
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
