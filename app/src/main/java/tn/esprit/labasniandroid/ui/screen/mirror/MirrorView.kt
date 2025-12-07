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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.ui.screen.dressing.DressingViewModel
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.utils.VTOWebSocketManager
import java.io.ByteArrayOutputStream

@Composable
fun MirrorView(
    modifier: Modifier = Modifier,
    dressingViewModel: DressingViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // États
    val clothes by dressingViewModel.clothes.collectAsState()
    val isLoading by dressingViewModel.isLoading.collectAsState()
    var selectedCloth by remember { mutableStateOf<Cloth?>(null) }
    val processedImage = remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isProcessing = remember { mutableStateOf(false) }

    // WebSocket Manager
    val wsManager = remember { VTOWebSocketManager(context) }

    // ImageCapture
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Permission caméra
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* Permission handled */ }
    )

    // ✅ Setup WebSocket et vêtements
    LaunchedEffect(key1 = "vto_setup") {
        val token = TokenManager.getToken(context)
        if (!token.isNullOrBlank()) {
            // Charger vêtements
            dressingViewModel.loadVTOReadyClothes(token)

            // Configurer WebSocket
            wsManager.onConnected = {
                android.util.Log.d("MirrorView", "✅ WebSocket connecté")
            }

            wsManager.onProcessedFrame = { base64 ->
                try {
                    val bytes = Base64.decode(base64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    processedImage.value = bitmap?.asImageBitmap()
                    isProcessing.value = false
                    android.util.Log.d("MirrorView", "✅ Frame traitée reçue")
                } catch (e: Exception) {
                    android.util.Log.e("MirrorView", "Erreur décodage frame", e)
                    isProcessing.value = false
                }
            }

            wsManager.onError = { error ->
                android.util.Log.e("MirrorView", "❌ Erreur VTO: $error")
                errorMessage.value = error
                isProcessing.value = false
            }

            // Connecter
            wsManager.connect()
        }

        // Vérifier permission caméra
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // ✅ Throttling frames
    LaunchedEffect(selectedCloth) {
        if (selectedCloth != null && wsManager.isConnected()) {
            while (true) {
                delay(500)  // 2 FPS

                try {
                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                try {
                                    val bitmap = image.toBitmap()
                                    val baos = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                                    val base64 = Base64.encodeToString(
                                        baos.toByteArray(),
                                        Base64.NO_WRAP
                                    )

                                    isProcessing.value = true
                                    wsManager.sendFrame(base64, selectedCloth)
                                } catch (e: Exception) {
                                    android.util.Log.e("MirrorView", "Erreur conversion", e)
                                } finally {
                                    image.close()
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                android.util.Log.e("MirrorView", "Erreur capture: ${exception.message}")
                            }
                        }
                    )
                } catch (e: Exception) {
                    android.util.Log.e("MirrorView", "Erreur takePicture", e)
                    break
                }
            }
        }
    }

    // ✅ Cleanup
    DisposableEffect(key1 = "vto_lifecycle") {
        onDispose {
            android.util.Log.d("MirrorView", "🔌 Nettoyage VTO")
            wsManager.disconnect()
        }
    }

    // ✅ UI
    Box(modifier = modifier.fillMaxSize()) {
        // Caméra OU image traitée
        if (processedImage.value != null) {
            Image(
                bitmap = processedImage.value!!,
                contentDescription = "VTO Overlay",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        } else {
            // Vue caméra
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

        // ✅ Indicateur de traitement
        if (isProcessing.value) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // ✅ Affichage erreur
        errorMessage.value?.let { error ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp)
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

        // ✅ Barre de vêtements en bas
        Card(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PinkPrimary)
                }
            } else if (clothes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun vêtement disponible",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Column {
                    // Instructions
                    Text(
                        text = "Reculez de 1.5m et sélectionnez un vêtement",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(clothes, key = { it.id }) { cloth ->
                            MirrorClothChip(
                                cloth = cloth,
                                isSelected = selectedCloth?.id == cloth.id
                            ) {
                                selectedCloth = cloth
                                android.util.Log.d("MirrorView", "👕 Vêtement sélectionné: ${cloth.type}")
                            }
                        }
                    }
                }
            }
        }
    }
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
                .data(cloth.imageUrl)
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

// Extension pour ImageProxy to Bitmap
fun ImageProxy.toBitmap(): Bitmap {
    val plane = planes[0]
    val buffer = plane.buffer
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    // Rotation pour caméra frontale
    val matrix = Matrix().apply { postRotate(90f) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}