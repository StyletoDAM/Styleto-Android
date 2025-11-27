package tn.esprit.labasniandroid.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.labasniandroid.ui.components.ViewPackages
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant

@Composable
fun PackProfileCard(
    modifier: Modifier = Modifier
) {
    var showPlans by remember { mutableStateOf(false) }
    
    // Couleurs dynamiques
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()
    val themeSoftPink = DynamicThemeColors.softPink(isMale)

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // MARK: - Card 1: Free Pack + Usage
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.05f)
                ),
            colors = CardDefaults.cardColors(containerColor = themeCard),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Current pack",
                        tint = themePrimary.copy(alpha = 0.8f),
                        modifier = Modifier.size(28.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Your current pack",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = themeSecondaryText
                        )
                        
                        Text(
                            text = "Free Pack",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = themePrimary
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "View details",
                        tint = themeSecondaryText,
                        modifier = Modifier.size(14.dp)
                    )
                }
                
                Text(
                    text = "Your usage this month",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = themeText.copy(alpha = 0.9f)
                )
                
                // Progress bars
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProgressRow(
                        icon = "👕",
                        title = "Clothing scans",
                        current = 3,
                        max = 5,
                        themeTeal = themeTeal,
                        themeText = themeText,
                        themePrimary = themePrimary,
                        themeSoftPink = themeSoftPink
                    )
                    ProgressRow(
                        icon = "✨",
                        title = "Outfit suggestions",
                        current = 2,
                        max = 3,
                        themeTeal = themeTeal,
                        themeText = themeText,
                        themePrimary = themePrimary,
                        themeSoftPink = themeSoftPink
                    )
                    ProgressRow(
                        icon = "🛍️",
                        title = "Items for sale",
                        current = 1,
                        max = 3,
                        themeTeal = themeTeal,
                        themeText = themeText,
                        themePrimary = themePrimary,
                        themeSoftPink = themeSoftPink
                    )
                }
            }
        }
        
        // MARK: - Card 2: Upgrade to Premium
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 15.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = themePrimary.copy(alpha = 0.4f),
                    spotColor = themePrimary.copy(alpha = 0.4f)
                ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                themePrimary.copy(alpha = 0.85f),
                                themePrimary.copy(alpha = 0.7f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = "Upgrade",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        
                        Text(
                            text = "Upgrade to Premium",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                    
                    Text(
                        text = "Unlock all AI features\nand create your personalized 3D avatar.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        ),
                        color = Color.White.copy(alpha = 0.95f)
                    )
                    
                    Button(
                        onClick = { showPlans = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themePrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("👑")
                            Text(
                                text = "View packs",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Sheet pour les plans
    if (showPlans) {
        ViewPackages(
            onDismiss = { showPlans = false }
        )
    }
}

@Composable
private fun ProgressRow(
    icon: String,
    title: String,
    current: Int,
    max: Int,
    themeTeal: Color,
    themeText: Color,
    themePrimary: Color,
    themeSoftPink: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.width(28.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                color = themeText,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "$current/$max",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = themePrimary
            )
        }
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(themeSoftPink.copy(alpha = 0.25f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = current.toFloat() / max.toFloat())
                    .clip(RoundedCornerShape(6.dp))
                    .background(themeTeal)
            )
        }
    }
}
