package tn.esprit.labasniandroid.ui.screen.store.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.data.local.entities.CartItem
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.CartManager
import tn.esprit.labasniandroid.utils.PaymentService
import com.stripe.android.paymentsheet.PaymentSheetResult
import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar

/**
 * CartView Android (comme iOS CartView)
 * Empty state, cart items list, free shipping banner, order summary
 */
@Composable
fun CartView(
    token: String,
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(),
    paymentViewModel: PaymentViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeTeal = DynamicThemeColors.teal(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()
    val themeSoftPink = DynamicThemeColors.softPink(isMale)

    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observer les articles du panier (comme iOS @ObservedObject cartManager)
    val cartItems by CartManager.cartItems.collectAsState(initial = emptyList())
    val totalPrice by CartManager.totalPrice.collectAsState(initial = 0.0)

    // ViewModel states
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val paymentSuccess by viewModel.paymentSuccess.collectAsState()
    val clientSecret by viewModel.clientSecret.collectAsState()
    
    // PaymentViewModel states
    val isProcessingPayment by paymentViewModel.isProcessing.collectAsState()
    val paymentErrorMessage by paymentViewModel.errorMessage.collectAsState()
    val showPaymentSuccess by paymentViewModel.showSuccess.collectAsState()
    val userBalance by paymentViewModel.userBalance.collectAsState()
    val useBalance by paymentViewModel.useBalance.collectAsState()
    val paymentClientSecret by paymentViewModel.clientSecret.collectAsState()

    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Variable pour stocker le clientSecret à présenter (depuis PaymentViewModel)
    var pendingClientSecret by remember { mutableStateOf<String?>(null) }
    
    // Observer le clientSecret du PaymentViewModel
    LaunchedEffect(paymentClientSecret) {
        if (paymentClientSecret != null) {
            pendingClientSecret = paymentClientSecret
        }
    }
    
    // Présenter PaymentSheet quand le clientSecret est disponible
    LaunchedEffect(pendingClientSecret) {
        if (pendingClientSecret != null && activity != null) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                try {
                    PaymentService.presentPaymentSheet(
                        activity = activity,
                        clientSecret = pendingClientSecret!!,
                        onResult = { paymentResult ->
                            when (paymentResult) {
                                is PaymentSheetResult.Completed -> {
                                    scope.launch {
                                        paymentViewModel.confirmStripeOrders(token, cartItems)
                                        showSuccessDialog = true
                                        CartManager.clearCart(context)
                                    }
                                }
                                is PaymentSheetResult.Canceled -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Paiement annulé")
                                    }
                                }
                                is PaymentSheetResult.Failed -> {
                                    scope.launch {
                                        val errorMsg = paymentResult.error?.message ?: "Erreur inconnue"
                                        snackbarHostState.showSnackbar(
                                            "Erreur de paiement: $errorMsg"
                                        )
                                    }
                                }
                            }
                            pendingClientSecret = null
                        }
                    )
                } catch (e: Exception) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Erreur: ${e.message ?: "Impossible d'ouvrir le paiement"}"
                        )
                    }
                    pendingClientSecret = null
                }
            }
        }
    }

    LaunchedEffect(userId) {
        CartManager.fetchCartItems()
        // Rafraîchir le balance au démarrage
        paymentViewModel.refreshBalance(token)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            // Header avec titre et bouton retour (comme iOS NavigationStack)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = themePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "My Cart (${cartItems.size})",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = themePrimary
                    )
                )
                Spacer(modifier = Modifier.width(48.dp)) // Équilibre avec le bouton retour
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeBackground)
                .padding(innerPadding)
        ) {
            if (cartItems.isEmpty()) {
                // Empty State (comme iOS)
                EmptyCartState(
                    themePrimary = themePrimary,
                    themeText = themeText
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Cart Items List (comme iOS)
                    cartItems.forEach { item ->
                        CartItemRow(
                            cartItem = item,
                            themeCard = themeCard,
                            themePrimary = themePrimary,
                            themeSoftPink = themeSoftPink,
                            themeTeal = themeTeal,
                            themeSecondaryText = themeSecondaryText,
                            onDelete = {
                                itemToDelete = item
                                showDeleteAlert = true
                            }
                        )
                    }

                    // Free Shipping Banner (comme iOS)
                    FreeShippingBanner(
                        themeCard = themeCard,
                        themeTeal = themeTeal
                    )

                    // Order Summary avec choix balance/carte (comme iOS)
                    OrderSummary(
                        totalPrice = totalPrice,
                        themeCard = themeCard,
                        themePrimary = themePrimary,
                        themeTeal = themeTeal,
                        themeText = themeText,
                        cartItems = cartItems,
                        isLoading = isLoading || isProcessingPayment,
                        userBalance = userBalance,
                        useBalance = useBalance,
                        canPayWithBalance = paymentViewModel.canPayWithBalance(totalPrice),
                        onUseBalanceChange = { paymentViewModel.setUseBalance(it) },
                        onCheckoutClick = {
                            if (cartItems.isNotEmpty()) {
                                paymentViewModel.startCheckout(token, cartItems, totalPrice)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
            
            // Loading overlay (comme iOS)
            if (isProcessingPayment) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = themePrimary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Processing payment...",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Snackbar pour les messages
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Dialog de succès après paiement
    LaunchedEffect(showPaymentSuccess) {
        if (showPaymentSuccess) {
            showSuccessDialog = true
        }
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                paymentViewModel.resetAfterSuccess()
                viewModel.resetPaymentState()
            },
            title = {
                Text(
                    text = "Purchase Successful! 🎉",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                )
            },
            text = {
                Text(
                    text = "Your items have been purchased successfully!",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        paymentViewModel.resetAfterSuccess()
                        viewModel.resetPaymentState()
                        onNavigateBack()
                    }
                ) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.Bold,
                        color = themePrimary
                    )
                }
            }
        )
    }

    // Afficher les erreurs
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
    }
    
    // Afficher les erreurs de paiement
    LaunchedEffect(paymentErrorMessage) {
        paymentErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            paymentViewModel.clearMessages()
        }
    }

    // Delete Confirmation Alert (comme iOS)
    if (showDeleteAlert && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteAlert = false
                itemToDelete = null
            },
            title = {
                Text(
                    text = "Remove from cart?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "This item will be removed from your cart.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let { item ->
                            scope.launch {
                                CartManager.removeFromCart(item)
                            }
                        }
                        showDeleteAlert = false
                        itemToDelete = null
                    }
                ) {
                    Text(
                        text = "Remove",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteAlert = false
                        itemToDelete = null
                    }
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

/**
 * Empty Cart State (comme iOS)
 */
@Composable
private fun EmptyCartState(
    themePrimary: Color,
    themeText: Color
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add items from the store!",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = themeText.copy(alpha = 0.7f)
            )
        )
    }
}

/**
 * Cart Item Row (comme iOS CartItemRow)
 */
@Composable
private fun CartItemRow(
    cartItem: CartItem,
    themeCard: Color,
    themePrimary: Color,
    themeSoftPink: Color,
    themeTeal: Color,
    themeSecondaryText: Color,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = themeCard),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image (90x90dp comme iOS)
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(cartItem.imageURL ?: "")
                    .crossfade(true)
                    .build(),
                contentDescription = cartItem.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            // Infos
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = cartItem.title ?: "Item",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = themePrimary
                    ),
                    maxLines = 2
                )

                // Size badge (comme iOS Label)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(themeSoftPink.copy(alpha = 0.4f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📏",
                        fontSize = 12.sp
                    )
                    Text(
                        text = cartItem.size ?: "One Size",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = themeSecondaryText
                        )
                    )
                }

                // Price
                Text(
                    text = "${String.format("%.2f", cartItem.price)} DT",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = themePrimary
                    )
                )
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Free Shipping Banner (comme iOS)
 */
@Composable
private fun FreeShippingBanner(
    themeCard: Color,
    themeTeal: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = themeCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🚚",
                fontSize = 20.sp
            )
            Text(
                text = "Free shipping!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = themeTeal
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "🎉",
                fontSize = 20.sp
            )
        }
    }
}

/**
 * Order Summary avec choix balance/carte (comme iOS)
 */
@Composable
private fun OrderSummary(
    totalPrice: Double,
    themeCard: Color,
    themePrimary: Color,
    themeTeal: Color,
    themeText: Color,
    cartItems: List<CartItem>,
    isLoading: Boolean,
    userBalance: Double,
    useBalance: Boolean,
    canPayWithBalance: Boolean,
    onUseBalanceChange: (Boolean) -> Unit,
    onCheckoutClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = themeCard),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Order Summary",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = themePrimary
                )
            )

            // Available Balance (comme iOS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Available Balance",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeText
                    )
                )
                Text(
                    text = "${String.format("%.2f", userBalance)} DT",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (canPayWithBalance) themeTeal else Color.Red
                    )
                )
            }
            
            // Payment method choice (comme iOS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Balance button
                Button(
                    onClick = { onUseBalanceChange(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = if (useBalance) themeTeal else themeText.copy(alpha = 0.6f)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (useBalance) 
                                androidx.compose.material.icons.Icons.Default.RadioButtonChecked 
                            else 
                                androidx.compose.material.icons.Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Balance",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Balance",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                
                // Card button
                Button(
                    onClick = { onUseBalanceChange(false) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = if (!useBalance) themePrimary else themeText.copy(alpha = 0.6f)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (!useBalance) 
                                androidx.compose.material.icons.Icons.Default.RadioButtonChecked 
                            else 
                                androidx.compose.material.icons.Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Card",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Card",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Divider(
                color = themePrimary.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            // Shipping
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Shipping",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeTeal
                    )
                )
                Text(
                    text = "Free",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Green
                    )
                )
            }

            Divider(
                color = themePrimary.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${String.format("%.2f", totalPrice)} DT",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = themePrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pay Now button (comme iOS)
            Button(
                onClick = {
                    onCheckoutClick()
                },
                enabled = !isLoading && cartItems.isNotEmpty() && !(useBalance && !canPayWithBalance),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            if (useBalance && !canPayWithBalance) Color.Gray else themePrimary,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .shadow(10.dp, RoundedCornerShape(20.dp), spotColor = themePrimary.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLoading) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = if (useBalance) "💳" else "💳",
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = if (isLoading) "Processing..." else "Pay Now",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
            
            // Insufficient balance message (comme iOS)
            if (useBalance && !canPayWithBalance) {
                Text(
                    text = "Solde insuffisant. Veuillez recharger votre compte.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

