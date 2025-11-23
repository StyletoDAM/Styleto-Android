package tn.esprit.labasniandroid.ui.screen.dressing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * PhotoGuidePopupView - Popup avec carousel de tips avant d'ouvrir la caméra (comme iOS)
 */
@Composable
fun PhotoGuidePopupView(
    isShowing: Boolean,
    onDismiss: () -> Unit,
    onContinue: () -> Unit,
    themePrimary: Color,
    themeTeal: Color,
    themeCard: Color,
    themeBackground: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    if (!isShowing) return

    val tips = remember {
        listOf(
            PhotoTip(
                icon = Icons.Filled.LightMode,
                title = "Good Lighting",
                description = "Use natural light or bright room lighting for best results",
                color = Color(0xFFFFD700) // Yellow
            ),
            PhotoTip(
                icon = Icons.Filled.Rectangle,
                title = "Plain Background",
                description = "Place the item on a solid color surface (white, grey, or any plain color)",
                color = themeTeal
            ),
            PhotoTip(
                icon = Icons.Filled.Highlight,
                title = "Center the Item",
                description = "Make sure the clothing item fills most of the frame",
                color = themePrimary
            ),
            PhotoTip(
                icon = Icons.Filled.AutoAwesome,
                title = "Flat & Smooth",
                description = "Lay the item flat and smooth out wrinkles for accurate detection",
                color = Color(0xFFA7E0E0) // Aqua
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { tips.size }, initialPage = 0)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = themeCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = null,
                                tint = themePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Photo Tips",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = themePrimary
                                )
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.Gray.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Carousel de tips
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) { page ->
                        TipCard(
                            tip = tips[page],
                            themePrimary = themePrimary,
                            themeText = themeText,
                            themeSecondaryText = themeSecondaryText
                        )
                    }

                    // Indicateurs de page
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(tips.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.2f else 1f,
                                animationSpec = tween(200),
                                label = "indicator_scale"
                            )

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .scale(scale)
                                    .background(
                                        if (isSelected) themePrimary else Color.Gray.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    ),
                            )
                            if (index < tips.size - 1) {
                                Spacer(modifier = Modifier.size(8.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Bouton Continue
                    TextButton(
                        onClick = {
                            onDismiss()
                            onContinue()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 24.dp),
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(themePrimary, themeTeal)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Got it!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

/**
 * Modèle pour un tip de photo
 */
data class PhotoTip(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val color: Color
)

/**
 * Carte d'un tip (comme iOS TipCard)
 */
@Composable
private fun TipCard(
    tip: PhotoTip,
    themePrimary: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Icône dans un cercle
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    tip.color.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tip.icon,
                contentDescription = null,
                tint = tip.color,
                modifier = Modifier.size(42.dp)
            )
        }

        // Titre
        Text(
            text = tip.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = themePrimary
            ),
            textAlign = TextAlign.Center
        )

        // Description
        Text(
            text = tip.description,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = themeSecondaryText,
                lineHeight = 20.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

