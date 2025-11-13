package tn.esprit.labasniandroid.ui.screen.dressing

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.PinkSecondary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.utils.findActivity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DressingTab(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    viewModel: DressingViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        scope.launch {
            if (bitmap != null) {
                snackbarHostState.showSnackbar("Photo capturée (non enregistrée).")
            } else {
                snackbarHostState.showSnackbar("Capture annulée.")
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        scope.launch {
            if (granted) {
                takePictureLauncher.launch(null)
            } else {
                snackbarHostState.showSnackbar("Autorisez la caméra pour prendre une photo.")
            }
        }
    }

    fun openCamera() {
        val permission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                takePictureLauncher.launch(null)
            }
            context.findActivity()?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permission) } == true -> {
                scope.launch {
                    snackbarHostState.showSnackbar("La caméra est requise pour prendre une photo.")
                }
            }
            else -> {
                cameraPermissionLauncher.launch(permission)
            }
        }
    }

    val clothes by viewModel.clothes.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val deletingIds by viewModel.deletingIds.collectAsState()

    var authToken by remember { mutableStateOf<String?>(null) }
    var userId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authToken = TokenManager.getToken(context)
        userId = TokenManager.getUserId(context)
        val token = authToken
        if (token != null) {
            viewModel.loadClothes(token)
        } else {
            snackbarHostState.showSnackbar("Session expirée. Veuillez vous reconnecter.")
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("Tous") }

    val availableTypes = remember(clothes) {
        listOf("Tous") + clothes.map { it.type }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
    }

    val filteredClothes by remember(clothes, searchQuery, selectedType) {
        derivedStateOf {
            clothes.filter { cloth ->
                val matchesType = selectedType == "Tous" || cloth.type.equals(selectedType, ignoreCase = true)
                val matchesQuery = searchQuery.isBlank() ||
                    cloth.name.contains(searchQuery, ignoreCase = true) ||
                    cloth.type.contains(searchQuery, ignoreCase = true)
                matchesType && matchesQuery
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PinkGradientTop.copy(alpha = 0.18f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mon Dressing",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = PinkPrimary
                        )
                    )
                    IconButton(
                        onClick = { openCamera() },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(PinkPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Ajouter un vêtement",
                            tint = Color.White
                        )
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Rechercher un vêtement...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = PinkSecondary
                        )
                    },
                    singleLine = true
                )

                if (availableTypes.size > 1) {
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        availableTypes.forEach { type ->
                            DressingCategoryChip(
                                label = type,
                                selected = selectedType.equals(type, ignoreCase = true),
                                onClick = { selectedType = type }
                            )
                        }
                    }
                }

                if (filteredClothes.isEmpty() && !(loading || isLoading)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun vêtement pour le moment.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TealAccent.copy(alpha = 0.7f)
                            )
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(
                            items = filteredClothes,
                            key = { it.id }
                        ) { cloth ->
                            DressingCard(
                                cloth = cloth,
                                isDeleting = deletingIds.contains(cloth.id),
                                onDelete = {
                                    val token = authToken
                                    when {
                                        token.isNullOrBlank() ->
                                            scope.launch { snackbarHostState.showSnackbar("Session expirée. Veuillez vous reconnecter.") }
                                        else -> viewModel.deleteCloth(token, cloth.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            val showLoading = isLoading || loading
            if (showLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PinkPrimary)
                }
            }
        }
    }
}

@Composable
private fun DressingCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) PinkPrimary else PinkGradientTop.copy(alpha = 0.45f)
    val textColor = if (selected) Color.White else TealAccent

    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = textColor),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 10.dp)
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

@Composable
private fun DressingCard(
    cloth: Cloth,
    isDeleting: Boolean,
    onDelete: () -> Unit
) {
    val backgroundColor = remember(cloth.colorHex) { parseColorToCompose(cloth.colorHex) }
    val contentColor = remember(backgroundColor) { contentColorForBackground(backgroundColor) }
    val emoji = remember(cloth.type) { emojiForType(cloth.type) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(20.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 46.sp
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = cloth.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                )
                Text(
                    text = cloth.type,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = contentColor.copy(alpha = 0.85f)
                    )
                )
            }
        }

        IconButton(
            onClick = { if (!isDeleting) onDelete() },
            enabled = !isDeleting,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))
        ) {
            if (isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = PinkPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.RemoveCircle,
                    contentDescription = "Supprimer",
                    tint = PinkPrimary
                )
            }
        }
    }
}

private fun emojiForType(type: String): String {
    return when (type.lowercase()) {
        "t-shirt", "tshirt", "haut" -> "👕"
        "chemise" -> "👔"
        "pull", "sweat" -> "🧥"
        "pantalon" -> "👖"
        "jean" -> "👖"
        "short" -> "🩳"
        "robe" -> "👗"
        "chaussures", "chaussure", "baskets" -> "👟"
        "accessoire", "accessoires" -> "👜"
        else -> "✨"
    }
}

private fun parseColorToCompose(colorHex: String): Color {
    return try {
        val normalized = normalizeColorForInput(colorHex)
        Color(AndroidColor.parseColor(normalized))
    } catch (_: Exception) {
        Color(0xFFF6D4E3)
    }
}

private fun contentColorForBackground(background: Color): Color {
    return if (background.luminance() > 0.5f) Color(0xFF24313C) else Color.White
}

private fun normalizeColorForInput(input: String): String {
    val fallback = "#F6D4E3"
    if (input.isBlank()) return fallback
    val candidate = input.trim()
    val withoutHash = candidate.removePrefix("#")
    val hex = when (withoutHash.length) {
        3 -> withoutHash.flatMap { listOf(it, it) }.joinToString(separator = "")
        6 -> withoutHash
        8 -> withoutHash.substring(2)
        else -> return fallback
    }
    return "#${hex.uppercase()}"
}

