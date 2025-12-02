package tn.esprit.labasniandroid.ui.screen.store

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage
import coil.request.ImageRequest
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant

@Composable
fun SellSuggestionCard(
    clothe: Cloth,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onDismiss: () -> Unit, // ✨ NOUVEAU : Bouton X pour cacher
    modifier: Modifier = Modifier
) {
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeSecondary = DynamicThemeColors.secondary(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()
    val themeSoftPink = DynamicThemeColors.softPink(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)

    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = themeCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header avec icône et bouton X
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Tag,
                    contentDescription = null,
                    tint = Color(0xFFFF9800), // Orange
                    modifier = Modifier.size(24.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Sell Suggestion",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = themePrimary
                    )
                    Text(
                        text = "Not wearing this much?",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp
                        ),
                        color = themeSecondaryText
                    )
                }
                // ✨ Bouton X pour cacher (comme iOS)
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss",
                        tint = themeSecondaryText.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Message
            Text(
                text = "You haven't worn this item much. Would you like to sell it in the store?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp
                ),
                color = themeText,
                lineHeight = 20.sp
            )

            // Image du vêtement
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(clothe.imageUrl)
                        .crossfade(true)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(android.R.drawable.ic_menu_gallery)
                        .build(),
                    contentDescription = clothe.name,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Informations du vêtement
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                InfoRow(
                    icon = Icons.Filled.Done,
                    text = clothe.type.ifBlank { "Unknown" },
                    themeTeal = themeTeal,
                    themeText = themeText
                )
                InfoRow(
                    icon = Icons.Filled.Palette,
                    text = clothe.color ?: "Unknown",
                    themeTeal = themeTeal,
                    themeText = themeText
                )
                InfoRow(
                    icon = Icons.Filled.Star,
                    text = clothe.style ?: "Unknown",
                    themeTeal = themeTeal,
                    themeText = themeText
                )

                // Stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatBadge(
                        icon = Icons.Filled.CheckCircle,
                        value = "${clothe.acceptedCount ?: 0}",
                        color = Color.Green,
                        themeText = themeText
                    )
                    StatBadge(
                        icon = Icons.Filled.Close,
                        value = "${clothe.rejectedCount ?: 0}",
                        color = Color.Red,
                        themeText = themeText
                    )
                }
            }

            // Boutons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Gray.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Not Now",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.Gray
                        )
                    }
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(themePrimary, themeSecondary),
                                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, 0f)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Sell It",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    themeTeal: Color,
    themeText: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = themeTeal,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text.replaceFirstChar { it.uppercaseChar() },
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp
            ),
            color = themeText
        )
    }
}

@Composable
private fun StatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    color: Color,
    themeText: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(11.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = themeText
        )
    }
}

