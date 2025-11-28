package tn.esprit.labasniandroid.ui.screen.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.repositories.OrdersRepository
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import tn.esprit.labasniandroid.utils.TokenManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryView(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val ordersRepository = remember { OrdersRepository() }
    
    var orders by remember { mutableStateOf<List<tn.esprit.labasniandroid.api.OrderResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Couleurs dynamiques
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeCard = DynamicThemeColors.card()
    val themeBackground = DynamicThemeColors.background()
    val themeText = DynamicThemeColors.text(isMale)
    val themeSecondaryText = DynamicThemeColors.secondaryText()
    val themeTeal = DynamicThemeColors.teal(isMale)
    
    // Charger les commandes
    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context)
        if (token != null) {
            scope.launch {
                ordersRepository.getMyOrders(token).fold(
                    onSuccess = {
                        orders = it
                        isLoading = false
                    },
                    onFailure = { error ->
                        errorMessage = error.message
                        isLoading = false
                    }
                )
            }
        } else {
            errorMessage = "Vous devez être connecté"
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order History",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = themeText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = themePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBackground
                )
            )
        },
        containerColor = themeBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = themePrimary)
                    }
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = themeSecondaryText,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: "Une erreur est survenue",
                            style = MaterialTheme.typography.bodyLarge,
                            color = themeSecondaryText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                orders.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = null,
                            tint = themeSecondaryText.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No orders yet",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = themeText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your order history will appear here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeSecondaryText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            OrderCard(
                                order = order,
                                themeCard = themeCard,
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText,
                                themePrimary = themePrimary,
                                themeTeal = themeTeal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: tn.esprit.labasniandroid.api.OrderResponse,
    themeCard: Color,
    themeText: Color,
    themeSecondaryText: Color,
    themePrimary: Color,
    themeTeal: Color
) {
    // Parser les données du clothesId (peut être String ou ClothInfo)
    val clothInfo: tn.esprit.labasniandroid.api.ClothInfo? = try {
        when (val clothesId = order.clothesId) {
            is Map<*, *> -> {
                val idValue = clothesId["_id"]
                val id = when (idValue) {
                    is Map<*, *> -> (idValue["\$oid"] as? String) ?: ""
                    is String -> idValue
                    else -> ""
                }
                tn.esprit.labasniandroid.api.ClothInfo(
                    id = id,
                    name = clothesId["name"] as? String,
                    category = clothesId["category"] as? String,
                    type = clothesId["type"] as? String,
                    imageURL = (clothesId["imageURL"] as? String) ?: (clothesId["imageUrl"] as? String),
                    imageUrl = clothesId["imageUrl"] as? String
                )
            }
            is JsonObject -> {
                val idElement = clothesId.get("_id")
                val id = when {
                    idElement?.isJsonObject == true -> idElement.asJsonObject.get("\$oid")?.asString ?: ""
                    idElement?.isJsonPrimitive == true -> idElement.asString
                    else -> ""
                }
                tn.esprit.labasniandroid.api.ClothInfo(
                    id = id,
                    name = clothesId.get("name")?.asString,
                    category = clothesId.get("category")?.asString,
                    type = clothesId.get("type")?.asString,
                    imageURL = clothesId.get("imageURL")?.asString ?: clothesId.get("imageUrl")?.asString,
                    imageUrl = clothesId.get("imageUrl")?.asString
                )
            }
            is String -> {
                // Si c'est juste un ID string
                tn.esprit.labasniandroid.api.ClothInfo(
                    id = clothesId,
                    name = null,
                    category = null,
                    type = null,
                    imageURL = null,
                    imageUrl = null
                )
            }
            else -> {
                // Essayer de parser avec Gson
                try {
                    val gson = Gson()
                    gson.fromJson(gson.toJson(clothesId), tn.esprit.labasniandroid.api.ClothInfo::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        }
    } catch (e: Exception) {
        null
    }
    
    // Formater la date
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate = try {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(order.orderDate)
        if (date != null) dateFormat.format(date) else order.orderDate
    } catch (e: Exception) {
        order.orderDate
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = themeCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image du vêtement
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(themeSecondaryText.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = clothInfo?.displayImageUrl
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                            .error(android.R.drawable.ic_menu_gallery)
                            .build(),
                        contentDescription = clothInfo?.name ?: "Order item",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder si pas d'image
                    Icon(
                        imageVector = Icons.Rounded.ShoppingBag,
                        contentDescription = null,
                        tint = themeSecondaryText.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            // Informations
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = clothInfo?.name ?: "Clothes Item",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = themeText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                val typeText = clothInfo?.displayType
                if (!typeText.isNullOrBlank()) {
                    Text(
                        text = typeText.replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = themeSecondaryText
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = null,
                        tint = themeSecondaryText,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = themeSecondaryText
                    )
                }
            }
            
            // Prix
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${String.format("%.2f", order.price)} DT",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = themeTeal
                )
            }
        }
    }
}

