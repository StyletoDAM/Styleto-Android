package tn.esprit.labasniandroid.ui.screen.dressing

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Écran de chargement plein écran avec animation (comme iOS)
 */
@Composable
fun AIAnalysisLoadingScreen(
    themePrimary: Color,
    themeTeal: Color,
    themeBackground: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    var dotCount by remember { mutableStateOf(0) }
    
    // Animation du cercle rotatif
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Animation des points
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotCount = (dotCount + 1) % 4
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Cercle de progression animé avec icône
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                // Cercle avec gradient (70% du cercle)
                Canvas(
                    modifier = Modifier
                        .size(140.dp)
                        .rotate(rotation)
                ) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                themePrimary.copy(alpha = 0.6f),
                                themeTeal
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 252f, // 70% de 360
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx())
                    )
                }

                // Icône vêtement au centre
                Icon(
                    imageVector = Icons.Filled.Checkroom,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = themePrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Texte principal
            Text(
                text = "Analysing...",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = themePrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Texte secondaire avec points animés
            Text(
                text = "AI is analysing your clothe${".".repeat(dotCount)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = themeSecondaryText
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Petites vignettes décoratives en bas
            Row(
                modifier = Modifier.padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                MiniClothItem(color = Color(0x66FF69B4), themePrimary = themePrimary) // Pink
                MiniClothItem(color = Color(0x6600CED1), themePrimary = themePrimary) // Teal
                MiniClothItem(color = Color(0x669370DB), themePrimary = themePrimary) // Purple
                MiniClothItem(color = Color(0x664169E1), themePrimary = themePrimary) // Blue
            }
        }
    }
}

@Composable
private fun MiniClothItem(
    color: Color,
    themePrimary: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Checkroom,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White.copy(alpha = 0.8f)
            )
        }
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(themePrimary, shape = CircleShape)
        )
    }
}

