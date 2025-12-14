package tn.esprit.labasniandroid.ui.screen.mirror

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.labasniandroid.models.entities.Cloth
import tn.esprit.labasniandroid.models.repositories.DressingRepository
import tn.esprit.labasniandroid.ui.screen.dressing.DressingViewModel
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.utils.VTOWebSocketManager

/**
 * ViewModel pour l'Avatar/VTO (comme iOS AvatarViewModel)
 * Gère la caméra, le WebSocket, et l'état de l'application
 */
class AvatarViewModel : ViewModel() {

    private val dressingViewModel = DressingViewModel()
    private val dressingRepository = DressingRepository() // ✅ Pour charger directement comme iOS
    private var wsManager: VTOWebSocketManager? = null

    // États publiés (comme iOS @Published)
    private val _clothes = MutableStateFlow<List<Cloth>>(emptyList())
    val clothes: StateFlow<List<Cloth>> = _clothes.asStateFlow()

    private val _selectedCloth = MutableStateFlow<Cloth?>(null)
    val selectedCloth: StateFlow<Cloth?> = _selectedCloth.asStateFlow()

    private val _isCameraActive = MutableStateFlow(false)
    val isCameraActive: StateFlow<Boolean> = _isCameraActive.asStateFlow()

    private val _processedImage = MutableStateFlow<Bitmap?>(null)
    val processedImage: StateFlow<Bitmap?> = _processedImage.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _fps = MutableStateFlow(0)
    val fps: StateFlow<Int> = _fps.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // Compteur FPS
    private var fpsCounter = 0
    private var lastFpsUpdate = System.currentTimeMillis()

    /**
     * Initialise le ViewModel avec le Context (appelé depuis la vue)
     */
    fun initialize(context: Context) {
        if (wsManager == null) {
            wsManager = VTOWebSocketManager(context)
            setupSocket(context)
            fetchClothes(context)
        }
    }

    /**
     * Démarre la caméra (comme iOS startCamera())
     */
    fun startCamera() {
        if (_isCameraActive.value) return
        _isCameraActive.value = true
        Log.d("AvatarViewModel", "🎥 Caméra démarrée")
    }

    /**
     * Arrête la caméra (comme iOS stopCamera())
     */
    fun stopCamera() {
        _isCameraActive.value = false
        wsManager?.disconnect()
        _processedImage.value = null
        Log.d("AvatarViewModel", "🛑 Caméra arrêtée")
    }

    /**
     * Configure le WebSocket (comme iOS setupSocket())
     */
    private fun setupSocket(context: Context) {
        val token = TokenManager.getToken(context)
        if (token.isNullOrBlank()) {
            Log.e("AvatarViewModel", "❌ Impossible de configurer le socket: pas de token")
            return
        }

        val manager = wsManager ?: return
        manager.onConnected = {
            Log.d("AvatarViewModel", "✅ WebSocket VTO connecté")
            _isConnected.value = true
            _errorMessage.value = null
        }

        Log.d("AvatarViewModel", "🔧 Configuration callback onProcessedFrame")
        manager.onProcessedFrame = { base64 ->
            Log.d("AvatarViewModel", "📥 onProcessedFrame appelé (${base64.length / 1024}KB)")
            viewModelScope.launch {
                try {
                    if (base64.isBlank()) {
                        Log.e("AvatarViewModel", "❌ Base64 vide")
                        _isProcessing.value = false
                        _errorMessage.value = "Frame vide reçue"
                        return@launch
                    }

                    // ✅ Décoder sur IO dispatcher (opération lourde)
                    val bitmap = withContext(Dispatchers.IO) {
                        val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                        Log.d("AvatarViewModel", "✅ Base64 décodé: ${bytes.size} bytes")
                        
                        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                    
                    if (bitmap == null) {
                        Log.e("AvatarViewModel", "❌ Bitmap null après décodage")
                        _isProcessing.value = false
                        _errorMessage.value = "Impossible de décoder l'image"
                        return@launch
                    }

                    // ✅ Mettre à jour sur Main dispatcher
                    withContext(Dispatchers.Main) {
                        Log.d("AvatarViewModel", "✅ Bitmap créé: ${bitmap.width}x${bitmap.height}")
                        _processedImage.value = bitmap
                        _isProcessing.value = false
                        _errorMessage.value = null // ✅ Clear error on success

                        // Calculer FPS
                        fpsCounter++
                        val now = System.currentTimeMillis()
                        if (now - lastFpsUpdate >= 1000) {
                            _fps.value = fpsCounter
                            fpsCounter = 0
                            lastFpsUpdate = now
                            Log.d("AvatarViewModel", "📊 FPS: ${_fps.value}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AvatarViewModel", "❌ Erreur décodage frame", e)
                    e.printStackTrace()
                    _isProcessing.value = false
                    _errorMessage.value = "Erreur décodage: ${e.message}"
                }
            }
        }

        manager.onError = { error ->
            Log.e("AvatarViewModel", "❌ Erreur VTO: $error")
            _errorMessage.value = error
            _isProcessing.value = false
        }

        // Connecter après un court délai
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            manager.connect()
            Log.d("AvatarViewModel", "🔌 Connexion WebSocket VTO...")
        }
    }

    /**
     * Charge les vêtements (comme iOS fetchClothes() - utilise fetchMyClothes directement)
     */
    private fun fetchClothes(context: Context) {
        viewModelScope.launch {
            val token = TokenManager.getToken(context)
            if (token.isNullOrBlank()) {
                Log.e("AvatarViewModel", "❌ Pas de token pour charger les vêtements")
                return@launch
            }

            // ✅ CORRECTION : Appeler directement le repository comme iOS (tous les vêtements)
            val result = dressingRepository.fetchClothes(token)
            
            when {
                result.isSuccess -> {
                    val clothesList = result.getOrNull() ?: emptyList()
                    _clothes.value = clothesList
                    Log.d("AvatarViewModel", "✅ ${clothesList.size} vêtements chargés")
                }
                result.isFailure -> {
                    val error = result.exceptionOrNull()
                    Log.e("AvatarViewModel", "❌ Erreur chargement: ${error?.message}")
                    _errorMessage.value = error?.message ?: "Impossible de charger les vêtements"
                }
            }
        }
    }

    /**
     * Sélectionne un vêtement (comme iOS selectedClothe = clothe)
     */
    fun selectCloth(cloth: Cloth?) {
        _selectedCloth.value = cloth
        _processedImage.value = null // Réinitialiser l'image traitée
        Log.d("AvatarViewModel", "👕 Vêtement sélectionné: ${cloth?.type}")
    }

    /**
     * Envoie une frame au serveur (comme iOS sendFrameToServer())
     */
    fun sendFrame(frameBase64: String) {
        if (_selectedCloth.value == null) {
            Log.w("AvatarViewModel", "⚠️ Aucun vêtement sélectionné")
            return
        }

        if (!_isConnected.value) {
            Log.w("AvatarViewModel", "⚠️ WebSocket non connecté")
            return
        }

        val manager = wsManager
        if (manager == null) {
            Log.e("AvatarViewModel", "❌ WebSocketManager non initialisé")
            return
        }

        _isProcessing.value = true
        _errorMessage.value = null

        val sizeKB = frameBase64.length / 1024
        Log.d("AvatarViewModel", "📤 Frame envoyée ($sizeKB KB) - ${_selectedCloth.value?.type}")

        manager.sendFrame(frameBase64, _selectedCloth.value)

        // Timeout de 5 secondes (comme iOS)
        viewModelScope.launch {
            kotlinx.coroutines.delay(5000)
            if (_isProcessing.value) {
                _isProcessing.value = false
                _errorMessage.value = "Timeout (>5s)"
                Log.w("AvatarViewModel", "⏱️ Timeout détecté")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wsManager?.disconnect()
        wsManager = null
        Log.d("AvatarViewModel", "🧹 ViewModel nettoyé")
    }
}

