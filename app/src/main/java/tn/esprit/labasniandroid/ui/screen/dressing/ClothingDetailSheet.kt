package tn.esprit.labasniandroid.ui.screen.dressing

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import tn.esprit.labasniandroid.models.entities.Cloth

/**
 * ClothingDetailSheet - Sheet détaillée pour afficher les détails d'un vêtement (comme iOS)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothingDetailSheet(
    cloth: Cloth?,
    isShowing: Boolean,
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    themePrimary: Color,
    themeSecondary: Color,
    themeSoftPink: Color,
    themeAqua: Color,
    themeTeal: Color,
    themeCard: Color,
    themeBackground: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    if (!isShowing || cloth == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteAlert by remember { mutableStateOf(false) }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeBackground,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header avec bouton Close (comme iOS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Item Details",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = themePrimary
                    )
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = themePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // MARK: - Image Section (comme iOS)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image du vêtement avec fond damier (comme iOS)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Gray.copy(alpha = 0.05f),
                                    Color.Gray.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (cloth.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(cloth.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = cloth.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tag,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = themeTeal.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Image unavailable",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray
                                )
                            )
                        }
                    }
                }

                // Badge AI Analyzed (comme iOS)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(themePrimary.copy(alpha = 0.9f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = themePrimary.copy(alpha = 0.3f)
                        ),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "AI Analyzed",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            // MARK: - Clothing Information (comme iOS)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = themeCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Section Title
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themePrimary
                        )
                    )

                    // Type/Category
                    DetailRow(
                        icon = Icons.Filled.Tag,
                        label = "Type",
                        value = cloth.type.replaceFirstChar { it.uppercaseChar() },
                        valueColor = themePrimary,
                        backgroundColor = themePrimary.copy(alpha = 0.15f),
                        iconTint = themeTeal
                    )

                    // Color
                    if (!cloth.color.isNullOrBlank() || !cloth.colorHex.isBlank()) {
                        val colorName = cloth.color ?: "Unknown"
                        val colorHex = if (cloth.colorHex.isNotBlank()) {
                            cloth.colorHex
                        } else {
                            // Tentative de parsing depuis le nom de couleur
                            parseColorHex(colorName) ?: "#D3D3D3"
                        }

                        DetailRowWithColor(
                            icon = Icons.Filled.Palette,
                            label = "Color",
                            colorName = colorName,
                            colorHex = colorHex,
                            iconTint = themeTeal
                        )
                    }

                    // Style
                    if (!cloth.style.isNullOrBlank()) {
                        DetailRow(
                            icon = Icons.Filled.Star,
                            label = "Style",
                            value = cloth.style!!.replaceFirstChar { it.uppercaseChar() },
                            valueColor = themeTeal,
                            backgroundColor = themeTeal.copy(alpha = 0.15f),
                            iconTint = themeTeal
                        )
                    }

                    // Season
                    if (!cloth.season.isNullOrBlank()) {
                        val seasonIcon = getSeasonIcon(cloth.season!!)
                        DetailRow(
                            icon = seasonIcon,
                            label = "Season",
                            value = cloth.season!!.replaceFirstChar { it.uppercaseChar() },
                            valueColor = themeAqua,
                            backgroundColor = themeAqua.copy(alpha = 0.15f),
                            iconTint = themeTeal
                        )
                    }
                }
            }

            // MARK: - Delete Button (comme iOS - rouge avec gradient)
            Button(
                onClick = { showDeleteAlert = true },
                enabled = !isDeleting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF0000), // Red
                                    Color(0xCCFF0000)  // Red with opacity
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Delete Item",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Alert Dialog pour confirmation de suppression (comme iOS)
    if (showDeleteAlert) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteAlert = false },
            title = {
                Text(
                    text = "Delete this item?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeText
                    )
                )
            },
            text = {
                Text(
                    text = "This action cannot be undone. The item will be permanently removed from your wardrobe.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeSecondaryText
                    )
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDeleteAlert = false
                        onDelete()
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
                androidx.compose.material3.TextButton(onClick = { showDeleteAlert = false }) {
                    Text(
                        text = "Cancel",
                        color = themeSecondaryText
                    )
                }
            }
        )
    }
}

/**
 * Ligne de détail avec icône et valeur (comme iOS)
 */
@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color,
    backgroundColor: Color,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = iconTint
                )
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp)) // Capsule
                .background(backgroundColor)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
            )
        }
    }
}

/**
 * Ligne de détail avec couleur (comme iOS - Circle + nom de couleur)
 */
@Composable
private fun DetailRowWithColor(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    colorName: String,
    colorHex: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = iconTint
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (e: Exception) {
                            Color.Gray
                        },
                        shape = CircleShape
                    )
                    .clip(CircleShape)
            )

            Text(
                text = colorName.uppercase(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )
        }
    }
}

/**
 * Helper pour obtenir l'icône selon la saison (comme iOS)
 */
private fun getSeasonIcon(season: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (season.lowercase()) {
        "summer" -> Icons.Filled.WbSunny
        "winter" -> Icons.Filled.AcUnit
        "fall", "autumn" -> Icons.Filled.WbSunny // Fallback: use sun icon for fall
        "spring" -> Icons.Filled.Cloud
        else -> Icons.Filled.WbSunny // Default
    }
}

/**
 * Helper pour parser le hex color depuis un nom de couleur
 */
private fun parseColorHex(colorName: String): String? {
    val normalized = colorName.lowercase().trim()
    return when {
        normalized.contains("pink") -> "#FF69B4"
        normalized.contains("red") -> "#FF0000"
        normalized.contains("blue") -> "#0000FF"
        normalized.contains("black") -> "#000000"
        normalized.contains("white") -> "#FFFFFF"
        normalized.contains("green") -> "#00FF00"
        normalized.contains("yellow") -> "#FFFF00"
        normalized.contains("purple") -> "#800080"
        normalized.contains("orange") -> "#FFA500"
        normalized.contains("gray") || normalized.contains("grey") -> "#808080"
        else -> null
    }
}

