package tn.esprit.labasniandroid.ui.screen.mirror

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.animation.core.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.border
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.ui.screen.dressing.DressingViewModel
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.utils.VTOWebSocketManager
import java.io.ByteArrayOutputStream

@Composable
fun MirrorView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val avatarViewModel = remember { AvatarViewModel() }
    
    // Initialiser le ViewModel avec le Context
    LaunchedEffect(Unit) {
        avatarViewModel.initialize(context)
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    // États depuis AvatarViewModel (comme iOS)
    val clothes by avatarViewModel.clothes.collectAsState()
    val selectedCloth by avatarViewModel.selectedCloth.collectAsState()
    val processedImage by avatarViewModel.processedImage.collectAsState()
    val isProcessing by avatarViewModel.isProcessing.collectAsState()
    val errorMessage by avatarViewModel.errorMessage.collectAsState()
    val isConnected by avatarViewModel.isConnected.collectAsState()
    val isCameraActive by avatarViewModel.isCameraActive.collectAsState()
    
    // ✅ Log pour vérifier les changements d'état
    LaunchedEffect(processedImage) {
        android.util.Log.d("MirrorView", "🔄 processedImage StateFlow changé: ${processedImage != null}")
    }

    // ✅ FIX CRITIQUE 1 : Mutex pour empêcher captures simultanées
    val captureMutex = remember { Mutex() }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) // ✅ Mode rapide
            .build()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                avatarViewModel.startCamera()
            }
        }
    )

    // Démarrage de la caméra (comme iOS onAppear)
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            avatarViewModel.startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Arrêt de la caméra (comme iOS onDisappear)
    DisposableEffect(Unit) {
        onDispose {
            avatarViewModel.stopCamera()
        }
    }

    // ✅ FIX CRITIQUE 3 : Throttling optimisé avec Mutex (comme iOS throttleInterval = 0.25s = 4 FPS)
    LaunchedEffect(selectedCloth, isConnected, isCameraActive) {
        if (!isCameraActive || !isConnected || selectedCloth == null) {
            if (!isCameraActive) {
                android.util.Log.w("MirrorView", "⏸️ Caméra inactive")
            } else if (!isConnected) {
                android.util.Log.w("MirrorView", "⏳ En attente de connexion WebSocket...")
            } else {
                android.util.Log.d("MirrorView", "⏸️ Aucun vêtement - pause capture")
            }
            return@LaunchedEffect
        }

        android.util.Log.d("MirrorView", "🎥 Démarrage capture pour: ${selectedCloth?.type}")

        while (isCameraActive && isConnected && selectedCloth != null) {
            // ✅ 250ms = 4 FPS (comme iOS throttleInterval)
            delay(250)

            // ✅ Vérifier si une capture est déjà en cours
            if (!captureMutex.tryLock()) {
                android.util.Log.w("MirrorView", "⏭️ Capture ignorée (précédente en cours)")
                continue
            }

            try {
                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            try {
                                // ✅ Redimensionner AVANT compression (comme iOS maxWidth: 480)
                                val bitmap = image.toBitmap()
                                val resizedBitmap = resizeBitmap(bitmap, 480, 640) // Portrait

                                val baos = ByteArrayOutputStream()
                                // ✅ Compression adaptative (40% comme iOS currentQuality = 0.4)
                                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)

                                val base64 = Base64.encodeToString(
                                    baos.toByteArray(),
                                    Base64.NO_WRAP
                                )

                                // Envoyer via ViewModel (comme iOS sendFrameToServer)
                                avatarViewModel.sendFrame(base64)

                                android.util.Log.d(
                                    "MirrorView",
                                    "📤 Frame: ${base64.length / 1024}KB (${resizedBitmap.width}x${resizedBitmap.height})"
                                )

                                // Nettoyer bitmaps
                                if (resizedBitmap != bitmap) {
                                    resizedBitmap.recycle()
                                }
                                bitmap.recycle()
                            } catch (e: Exception) {
                                android.util.Log.e("MirrorView", "Erreur conversion", e)
                            } finally {
                                image.close()
                                captureMutex.unlock()
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            android.util.Log.e("MirrorView", "Erreur capture: ${exception.message}")
                            captureMutex.unlock()
                        }
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("MirrorView", "Erreur takePicture", e)
                captureMutex.unlock()
                break
            }
        }
    }

    // ✅ UI améliorée (comme iOS CameraOverlayView)
    Box(modifier = modifier.fillMaxSize()) {
        // Afficher l'image traitée OU la caméra brute (comme iOS)
        val currentProcessedImage = processedImage
        LaunchedEffect(currentProcessedImage) {
            if (currentProcessedImage != null) {
                android.util.Log.d("MirrorView", "🖼️ Image traitée disponible: ${currentProcessedImage.width}x${currentProcessedImage.height}")
            } else {
                android.util.Log.d("MirrorView", "🖼️ Aucune image traitée (affichage caméra)")
            }
        }
        
        // ✅ Afficher l'image traitée si disponible (comme iOS)
        if (currentProcessedImage != null) {
            // ✅ Convertir le bitmap en ImageBitmap une seule fois
            val imageBitmap = remember(currentProcessedImage) { 
                android.util.Log.d("MirrorView", "🎨 Conversion bitmap en ImageBitmap: ${currentProcessedImage.width}x${currentProcessedImage.height}")
                currentProcessedImage.asImageBitmap() 
            }
            
            Image(
                bitmap = imageBitmap,
                contentDescription = "VTO Overlay",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            
            // ✅ Log pour confirmer l'affichage
            LaunchedEffect(imageBitmap) {
                android.util.Log.d("MirrorView", "✅ Image traitée affichée dans le composable")
            }
        } else if (isCameraActive) {
            // ✅ Afficher la caméra seulement si pas d'image traitée
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().apply {
                            setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val selector = CameraSelector.DEFAULT_FRONT_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageCapture
                            )
                        } catch (e: Exception) {
                            android.util.Log.e("MirrorView", "Erreur bindToLifecycle", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ✨ Badge Experimental (comme iOS)
        ExperimentalBadge(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )

        // Indicateur de traitement
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 60.dp, end = 20.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Processing...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        if (!isConnected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp)
                    .background(Color(0xFFFF9800), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Connecting to server...",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        errorMessage?.let { error ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .background(Color.Red, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = error,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Boutons en haut (comme iOS)
        if (isCameraActive) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 50.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Bouton fermer (comme iOS)
                IconButton(
                    onClick = { avatarViewModel.stopCamera() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Barre de vêtements en bas (comme iOS CameraOverlayView)
        if (isCameraActive) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                if (clothes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                            .padding(bottom = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun vêtement disponible",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .background(Color.Red.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                    ) {
                        // Instructions (comme iOS)
                        Text(
                            text = "Reculez de 1.5m et sélectionnez un vêtement",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(clothes, key = { it.id }) { cloth ->
                                MirrorClothChip(
                                    cloth = cloth,
                                    isSelected = selectedCloth?.id == cloth.id
                                ) {
                                    avatarViewModel.selectCloth(cloth)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Écran de démarrage (comme iOS AvatarView quand !isCameraActive)
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = PinkPrimary.copy(alpha = 0.6f)
                )
                Text(
                    text = "Real Time Try-On",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = androidx.compose.ui.graphics.Color(0xFF4D5F8F) // Teal
                )
                Text(
                    text = "Press the central button to start",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }
}

// ✅ NOUVELLE FONCTION : Redimensionner bitmap de manière optimale
private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    // Si déjà assez petit, retourner tel quel
    if (width <= maxWidth && height <= maxHeight) {
        return bitmap
    }

    // Calculer le ratio pour garder les proportions
    val ratioBitmap = width.toFloat() / height.toFloat()
    val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

    var finalWidth = maxWidth
    var finalHeight = maxHeight

    if (ratioMax > ratioBitmap) {
        finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
    } else {
        finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
}

@Composable
private fun MirrorClothChip(
    cloth: Cloth,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .widthIn(min = 72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PinkPrimary.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(cloth.processedImageUrl ?: cloth.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = cloth.name,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentScale = ContentScale.Crop
        )

        Text(
            text = cloth.name,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) PinkPrimary else Color.Black,
            maxLines = 1
        )
    }
}

// ✨ Badge Experimental (comme iOS CameraOverlayView)
@Composable
fun ExperimentalBadge(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Row(
        modifier = modifier
            .background(
                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.4f),
                        Color.Black.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color(0xFFFF6B9D).copy(alpha = 0.3f) // PinkPrimary
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône flask animée
        Text(
            text = "⚗️",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .alpha(alpha)
        )

        // Texte EXPERIMENTAL
        Text(
            text = "EXPERIMENTAL",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            ),
            color = Color.White
        )

        // Badge BETA
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFFF6B9D).copy(alpha = 0.8f), // PinkPrimary
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "BETA",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                ),
                color = Color.White
            )
        }
    }
}

// Extension ImageProxy to Bitmap (inchangée)
fun ImageProxy.toBitmap(): Bitmap {
    val plane = planes[0]
    val buffer = plane.buffer
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    val matrix = Matrix().apply { postRotate(90f) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}