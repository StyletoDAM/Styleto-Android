package tn.esprit.labasniandroid.ui.screen.dressing

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import tn.esprit.labasniandroid.models.DetectionResult

/**
 * BottomSheet de résultats avec formulaire éditable (comme iOS DetectionResultView)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionResultBottomSheet(
    image: Bitmap?,
    detectionResult: DetectionResult,
    imageURL: String,
    isShowing: Boolean,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, Map<String, String>?) -> Unit, // imageURL, category, color, style, season, originalDetection
    themePrimary: Color,
    themeSecondary: Color,
    themeTeal: Color,
    themeCard: Color,
    themeBackground: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    if (!isShowing) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    // États éditables
    var selectedCategory by remember { mutableStateOf(detectionResult.type) }
    var selectedColor by remember { mutableStateOf(detectionResult.colorName) }
    var selectedStyle by remember { mutableStateOf(detectionResult.style) }
    var selectedSeason by remember { mutableStateOf(detectionResult.season) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeBackground,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Text(
                text = "Clothing Details",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = themePrimary
                )
            )

            // Image + Badge AI - Utiliser UNIQUEMENT l'URL Cloudinary (comme iOS)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // Toujours utiliser AsyncImage avec l'URL Cloudinary (comme iOS)
                if (imageURL.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageURL)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder si pas d'URL
                    PlaceholderImage(themePrimary, themeTeal)
                }

                // Badge AI Analyzed
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = themePrimary.copy(alpha = 0.95f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Text(
                            text = "AI Analyzed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            // Category
            CategorySection(
                title = "Clothing Type",
                selectedValue = selectedCategory,
                options = listOf("Tshirt", "Pants", "Dress", "Shoes", "Accessory", "Jacket", "Other"),
                onValueChange = { selectedCategory = it },
                themePrimary = themePrimary,
                themeText = themeText
            )

            // Color
            ColorSection(
                title = "Detected Color",
                colorName = selectedColor,
                colorHex = detectionResult.colorHex,
                onColorChange = { selectedColor = it },
                themePrimary = themePrimary,
                themeText = themeText,
                themeSecondaryText = themeSecondaryText
            )

            // Style
            CategorySection(
                title = "Style",
                selectedValue = selectedStyle,
                options = listOf("casual", "formal", "sport", "vintage", "modern", "bohemian"),
                onValueChange = { selectedStyle = it },
                themePrimary = themeTeal,
                themeText = themeText
            )

            // Season
            CategorySection(
                title = "Season",
                selectedValue = selectedSeason,
                options = listOf("summer", "winter", "fall", "spring", "all"),
                onValueChange = { selectedSeason = it },
                themePrimary = themeTeal,
                themeText = themeText
            )

            // AI Suggestion Note
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = themeTeal.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = themePrimary
                    )
                    Text(
                        text = "The information above was pre-filled by our AI. Feel free to edit as you like!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = themeSecondaryText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Button
            Button(
                onClick = {
                    if (!isSaving) {
                        // ✅ Construire originalDetection comme iOS
                        val originalDetection = mapOf(
                            "type" to (detectionResult.originalType ?: detectionResult.type),
                            "color" to (detectionResult.originalColor ?: detectionResult.colorHex),
                            "style" to (detectionResult.originalStyle ?: detectionResult.style),
                            "season" to (detectionResult.originalSeason ?: detectionResult.season).lowercase()
                        )
                        onSave(imageURL, selectedCategory, selectedColor, selectedStyle, selectedSeason, originalDetection)
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themePrimary
                ),
                shape = RoundedCornerShape(22.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Add to Wardrobe",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlaceholderImage(themePrimary: Color, themeTeal: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        themePrimary.copy(alpha = 0.3f),
                        themeTeal.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Demo Item",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun CategorySection(
    title: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    themePrimary: Color,
    themeText: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = themePrimary
            )
        )
        // Utiliser FlowRow ou Row wrap au lieu de LazyVerticalGrid pour éviter les contraintes infinies
        // Diviser en lignes de 3 éléments
        val rows = options.chunked(3)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rows.forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowOptions.forEach { option ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    if (selectedValue.equals(option, ignoreCase = true)) themePrimary
                                    else themePrimary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { onValueChange(option) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option.replaceFirstChar { it.uppercaseChar() },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = if (selectedValue.equals(option, ignoreCase = true)) Color.White else themePrimary
                                )
                            )
                        }
                    }
                    // Remplir les espaces vides si la ligne n'a pas 3 éléments
                    repeat(3 - rowOptions.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSection(
    title: String,
    colorName: String,
    colorHex: String,
    onColorChange: (String) -> Unit,
    themePrimary: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = themePrimary
            )
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = themePrimary.copy(alpha = 0.08f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
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
                
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = colorName,
                        onValueChange = onColorChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeText,
                            unfocusedTextColor = themeText,
                            focusedBorderColor = themePrimary,
                            unfocusedBorderColor = themePrimary.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = themePrimary
                        )
                        Text(
                            text = "Automatically detected by AI",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = themeSecondaryText
                            )
                        )
                    }
                }
            }
        }
    }
}

