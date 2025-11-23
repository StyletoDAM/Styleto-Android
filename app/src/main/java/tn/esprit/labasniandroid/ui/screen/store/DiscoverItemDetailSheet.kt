package tn.esprit.labasniandroid.ui.screen.store

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.StoreItem
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.CartManager

/**
 * DiscoverItemDetailSheet Android (comme iOS DiscoverItemDetailSheet)
 * Sheet complète avec image, détails, boutons Add to Cart et Contact Seller
 */
@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun DiscoverItemDetailSheet(
    storeItem: StoreItem,
    token: String,
    userId: String,
    onDismiss: () -> Unit,
    onContactSeller: () -> Unit
) {
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeSecondary = DynamicThemeColors.secondary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeAqua = DynamicThemeColors.aqua(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()
    val themeSoftPink = DynamicThemeColors.softPink(isMale)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isAddingToCart by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf<String?>(null) }
    var isInCart by remember { mutableStateOf(false) }

    val isAvailable = storeItem.status?.lowercase() != "sold"

    // Vérifier si l'article est déjà dans le panier
    LaunchedEffect(storeItem.id) {
        scope.launch {
            isInCart = CartManager.isItemInCart(storeItem.id, context)
        }
    }

    // Observer les changements du panier pour mettre à jour isInCart
    val cartItems by CartManager.cartItems.collectAsState(initial = emptyList())
    LaunchedEffect(cartItems) {
        scope.launch {
            isInCart = CartManager.isItemInCart(storeItem.id, context)
        }
    }

    fun addToCart() {
        if (!isAvailable) return

        scope.launch {
            isAddingToCart = true
            showError = null

            val result = CartManager.addToCart(storeItem, context)
            result.fold(
                onSuccess = {
                    // Success - dismiss sheet
                    isAddingToCart = false
                    onDismiss()
                },
                onFailure = { error ->
                    isAddingToCart = false
                    showError = error.message ?: "Unable to add to cart"
                }
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = themeBackground,
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                // Header avec Close button (comme iOS NavigationView toolbar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Item Details",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = themePrimary
                        )
                    )
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Close",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themePrimary
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image + Main Infos (comme iOS Section)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = themeCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Image (80x80dp comme iOS)
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(storeItem.cloth?.imageUrl ?: "")
                                .crossfade(true)
                                .build(),
                            contentDescription = storeItem.cloth?.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shadow(4.dp, RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // Infos
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = storeItem.cloth?.type?.replaceFirstChar { it.uppercaseChar() } ?: "Item",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeTeal
                                )
                            )

                            // Price
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "•",
                                    fontSize = 12.sp,
                                    color = themePrimary
                                )
                                Text(
                                    text = "${storeItem.price.toInt()} DT",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themePrimary
                                    )
                                )
                            }

                            // Size (si disponible)
                            storeItem.size?.takeIf { it.isNotBlank() }?.let { size ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(themeCard.copy(alpha = 0.8f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Size: $size",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeSecondary
                                        )
                                    )
                                }
                            }

                            // Status Badge
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(
                                        if (isAvailable) Color.Green.copy(alpha = 0.1f)
                                        else Color.Gray.copy(alpha = 0.1f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isAvailable) Color.Green else Color.Gray
                                        )
                                )
                                Text(
                                    text = if (isAvailable) "Available" else "Sold",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAvailable) Color.Green else Color.Gray
                                    )
                                )
                            }
                        }
                    }
                }

                // Error Message (si erreur)
                showError?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800).copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠",
                                fontSize = 20.sp
                            )
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFFF9800)
                                )
                            )
                        }
                    }
                }

                // Tag "Article déjà en panier" (si dans le panier)
                if (isInCart && isAvailable) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = themeTeal.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✓",
                                fontSize = 20.sp,
                                color = themeTeal
                            )
                            Text(
                                text = "Article déjà en panier",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = themeTeal
                                )
                            )
                        }
                    }
                }

                // Sold Item Message (si vendu)
                if (!isAvailable) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ℹ",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "This item has already been sold",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = themeSecondaryText
                                )
                            )
                        }
                    }
                }

                // Action Buttons (comme iOS Section)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add to Cart button (seulement si disponible et pas déjà dans le panier)
                    if (isAvailable) {
                        Button(
                            onClick = { addToCart() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isAddingToCart && !isInCart, // Désactivé si déjà dans le panier
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .then(
                                        if (isInCart) {
                                            // Fond gris si déjà dans le panier
                                            Modifier.background(
                                                Color.Gray.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        } else {
                                            // Gradient normal si pas dans le panier
                                            Modifier.background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(themePrimary, themeSecondary)
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isAddingToCart) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ShoppingCart,
                                            contentDescription = null,
                                            tint = if (isInCart) Color.Gray else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (isInCart) "Already in Cart" else "Add to Cart",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isInCart) Color.Gray else Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Contact Seller button (toujours visible)
                    Button(
                        onClick = {
                            onContactSeller()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
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
                                        colors = listOf(themeTeal, themeAqua)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💬",
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = if (isAvailable) "Contact Seller" else "Ask a Question",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

