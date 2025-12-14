package tn.esprit.labasniandroid.models.repositories

import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.JsonElement
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import tn.esprit.labasniandroid.api.ClothResponse
import tn.esprit.labasniandroid.api.ClothesApi
import tn.esprit.labasniandroid.api.CreateClothRequest
import tn.esprit.labasniandroid.api.DetectionApiResponse
import tn.esprit.labasniandroid.api.RetrofitClient
import tn.esprit.labasniandroid.api.UpdateFeedbackRequest
import tn.esprit.labasniandroid.models.DetectionResult
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.Cloth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tn.esprit.labasniandroid.utils.APIConstants

class DressingRepository(
    private val clothesApi: ClothesApi = RetrofitClient.clothesApi
) {
    suspend fun fetchClothes(token: String): Result<List<Cloth>> {
        return try {
            val response = clothesApi.getClothes("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val clothes = response.body()!!.map { it.toEntity() }
                Result.success(clothes)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer le dressing."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun deleteCloth(token: String, clothId: String): Result<Unit> {
        return try {
            val response = clothesApi.deleteCloth("Bearer $token", clothId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Vêtement introuvable."
                    else -> errorBody ?: "Suppression impossible pour le moment."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    /**
     * Détecte un vêtement à partir d'une image
     * Envoie directement le bitmap en multipart (comme iOS) - PAS de fichier local
     * @param bitmap Image à analyser
     * @param token Token JWT pour l'authentification
     * @return Result avec DetectionResult et imageUrl
     */
    suspend fun detectCloth(bitmap: Bitmap, token: String): Result<Pair<DetectionResult, String>> {
        return try {
            if (bitmap.isRecycled) {
                return Result.failure(NetworkError.ServerMessage("L'image a été recyclée"))
            }

            // Vérifier que le bitmap est valide
            if (bitmap.width <= 0 || bitmap.height <= 0) {
                return Result.failure(NetworkError.ServerMessage("Dimensions d'image invalides"))
            }

            // Redimensionner l'image si trop grande (max 1920x1920) pour éviter les erreurs
            val maxDimension = 1920
            val resizedBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val scale = minOf(
                    maxDimension.toFloat() / bitmap.width,
                    maxDimension.toFloat() / bitmap.height
                )
                val newWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
                val newHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }

            // Vérifier que le redimensionnement a réussi
            if (resizedBitmap == null || resizedBitmap.isRecycled) {
                if (resizedBitmap != bitmap && resizedBitmap != null) {
                    resizedBitmap.recycle()
                }
                return Result.failure(NetworkError.ServerMessage("Échec du redimensionnement de l'image"))
            }

            // Convertir Bitmap directement en ByteArray (comme iOS - pas de fichier local)
            val outputStream = java.io.ByteArrayOutputStream()
            if (!resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)) {
                if (resizedBitmap != bitmap) {
                    resizedBitmap.recycle()
                }
                return Result.failure(NetworkError.ServerMessage("Échec de la compression de l'image"))
            }
            val imageBytes = outputStream.toByteArray()
            outputStream.close()

            // Nettoyer le bitmap redimensionné si créé
            if (resizedBitmap != bitmap) {
                resizedBitmap.recycle()
            }

            if (imageBytes.isEmpty()) {
                return Result.failure(NetworkError.ServerMessage("L'image est vide après compression"))
            }

            // Vérifier la taille minimale (au moins 100 bytes) et maximale (max 10MB)
            if (imageBytes.size < 100) {
                return Result.failure(NetworkError.ServerMessage("L'image est trop petite (${imageBytes.size} bytes)"))
            }
            if (imageBytes.size > 10 * 1024 * 1024) {
                return Result.failure(NetworkError.ServerMessage("L'image est trop grande (${imageBytes.size / 1024 / 1024}MB)"))
            }

            // Utiliser OkHttpClient directement (comme iOS utilise URLSession) pour avoir exactement le même format
            val baseUrl = APIConstants.BASE_URL
            val detectUrl = "$baseUrl/detect"

            android.util.Log.d("DressingRepository", "=== DÉBUT DÉTECTION ===")
            android.util.Log.d("DressingRepository", "Base URL: $baseUrl")
            android.util.Log.d("DressingRepository", "URL détection: $detectUrl")
            android.util.Log.d("DressingRepository", "Taille image: ${imageBytes.size} bytes")
            android.util.Log.d("DressingRepository", "Dimensions: ${resizedBitmap.width}x${resizedBitmap.height}")

            // Vérifier que l'URL est valide
            val url = try {
                java.net.URL(detectUrl)
            } catch (e: Exception) {
                android.util.Log.e("DressingRepository", "URL invalide: $detectUrl", e)
                return Result.failure(NetworkError.ServerMessage("URL invalide: $detectUrl"))
            }

            // MultipartBody.Builder gère automatiquement le boundary et le Content-Type
            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "photo",
                    "photo.jpg",
                    imageBytes.toRequestBody("image/jpeg".toMediaType())
                )
                .build()

            val request = Request.Builder()
                .url(url)
                .post(multipartBody) // Ne pas ajouter Content-Type manuellement - MultipartBody le gère automatiquement avec le boundary
                .header("Authorization", "Bearer $token") // ✅ AJOUT : Header d'authentification (comme iOS)
                .build()
            
            android.util.Log.d("DressingRepository", "Token ajouté dans Authorization header")

            val client = OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // Plus de temps pour la détection Python
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            android.util.Log.d("DressingRepository", "Exécution de la requête HTTP...")

            // Exécuter la requête dans le contexte IO (comme iOS utilise DispatchQueue.main.async)
            val httpResponse = try {
                withContext(Dispatchers.IO) {
                    android.util.Log.d("DressingRepository", "Appel HTTP en cours...")
                    val result = client.newCall(request).execute()
                    android.util.Log.d("DressingRepository", "Appel HTTP terminé: code=${result.code}")
                    result
                }
            } catch (e: java.net.UnknownHostException) {
                android.util.Log.e("DressingRepository", "Host inconnu: ${e.message}", e)
                return Result.failure(NetworkError.ServerMessage("Impossible de se connecter au serveur. Vérifiez l'URL: $detectUrl"))
            } catch (e: java.net.ConnectException) {
                android.util.Log.e("DressingRepository", "Connexion refusée: ${e.message}", e)
                return Result.failure(NetworkError.ServerMessage("Connexion refusée. Vérifiez que le serveur est démarré sur $baseUrl"))
            } catch (e: java.net.SocketTimeoutException) {
                android.util.Log.e("DressingRepository", "Timeout: ${e.message}", e)
                return Result.failure(NetworkError.ServerMessage("Timeout de connexion. Le serveur met trop de temps à répondre."))
            } catch (e: Exception) {
                android.util.Log.e("DressingRepository", "Erreur réseau lors de l'envoi", e)
                e.printStackTrace()
                return Result.failure(NetworkError.Transport(e))
            }

            android.util.Log.d("DressingRepository", "Réponse reçue: code=${httpResponse.code}, success=${httpResponse.isSuccessful}, hasBody=${httpResponse.body != null}")

            if (httpResponse.isSuccessful && httpResponse.body != null) {
                val responseBody = httpResponse.body!!.string()
                try {
                    val gson = Gson()
                    val apiResponse = gson.fromJson(responseBody, DetectionApiResponse::class.java)
                    if (apiResponse == null) {
                        return Result.failure(NetworkError.ServerMessage("Réponse invalide du serveur"))
                    }

                    // Vérifier que les données sont valides
                    if (apiResponse.imageUrl.isBlank()) {
                        return Result.failure(NetworkError.ServerMessage("URL d'image manquante dans la réponse"))
                    }
                    if (apiResponse.detection == null) {
                        return Result.failure(NetworkError.ServerMessage("Données de détection manquantes"))
                    }

                    // ✅ NOUVEAU : Parser directement depuis le JSON (comme iOS)
                    val detection = apiResponse.detection
                    val detectionResult = DetectionResult(
                        type = normalizeCategory(detection.type),
                        colorHex = normalizeColorHex(detection.color),
                        colorName = extractColorName(detection.color),
                        style = normalizeStyle(detection.style),
                        season = normalizeSeason(detection.season),
                        // ✅ Stocker les valeurs originales brutes pour originalDetection
                        originalType = detection.type,
                        originalColor = detection.color,
                        originalStyle = detection.style,
                        originalSeason = detection.season
                    )

                    android.util.Log.d("DressingRepository", "✅ Détection parsée: ${detectionResult.type} | ${detectionResult.colorHex}")

                    Result.success(Pair(detectionResult, apiResponse.imageUrl))
                } catch (e: Exception) {
                    android.util.Log.e("DressingRepository", "Erreur parsing JSON: $responseBody", e)
                    return Result.failure(NetworkError.ServerMessage("Erreur lors du parsing de la réponse: ${e.message}"))
                }
            } else {
                val errorBody = try {
                    httpResponse.body?.string() ?: "Aucun message d'erreur"
                } catch (e: Exception) {
                    "Impossible de lire le message d'erreur"
                }
                android.util.Log.e("DressingRepository", "Erreur serveur (${httpResponse.code}): $errorBody")
                val message = when (httpResponse.code) {
                    400 -> "Requête invalide: $errorBody"
                    500 -> "Erreur serveur: $errorBody"
                    else -> "Erreur lors de la détection (code: ${httpResponse.code}): $errorBody"
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure(NetworkError.Transport(exception))
        }
    }

    /**
     * Ajoute un vêtement au dressing (avec originalDetection comme iOS)
     */
    suspend fun addCloth(
        token: String,
        imageURL: String,
        category: String,
        color: String,
        style: String,
        season: String,
        originalDetection: Map<String, String>? = null
    ): Result<Cloth> {
        return try {
            val request = CreateClothRequest(
                imageUrl = imageURL,
                category = category,
                color = color,
                style = style,
                season = season,
                originalDetection = originalDetection
            )
            val response = clothesApi.createCloth("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible d'ajouter le vêtement."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    /**
     * Met à jour le feedback d'un vêtement (acceptedCount ou rejectedCount)
     * Comme iOS: PATCH /cloth/:id/feedback avec { "accepted": true/false }
     */
    suspend fun updateFeedback(
        token: String,
        clotheId: String,
        accepted: Boolean
    ): Result<Unit> {
        return try {
            val request = UpdateFeedbackRequest(accepted = accepted)
            val response = clothesApi.updateFeedback("Bearer $token", clotheId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    404 -> "Vêtement introuvable."
                    else -> errorBody ?: "Impossible de mettre à jour le feedback."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    /**
     * Récupère les suggestions de vente (vêtements rejetés plusieurs fois)
     * Comme iOS: fetchSellSuggestions()
     */
    suspend fun fetchSellSuggestions(token: String): Result<List<Cloth>> {
        return try {
            val response = clothesApi.getSellSuggestions("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val clothes = response.body()!!.map { it.toEntity() }
                Result.success(clothes)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    401 -> "Session expirée. Veuillez vous reconnecter."
                    else -> errorBody ?: "Impossible de récupérer les suggestions de vente."
                }
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    suspend fun fetchVTOReadyClothes(token: String): Result<Map<String, List<Cloth>>> {
        return try {
            val response = clothesApi.getVTOReadyClothes("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val grouped = response.body()!!.mapValues { entry ->
                    entry.value.map { it.toEntity() }
                }
                Result.success(grouped)
            } else {
                val message = response.errorBody()?.string() ?: "Impossible de récupérer les vêtements VTO."
                Result.failure(NetworkError.ServerMessage(message))
            }
        } catch (exception: Exception) {
            Result.failure(NetworkError.Transport(exception))
        }
    }

    private fun ClothResponse.toEntity(): Cloth {
        val name = extractCategory(category)
        val type = name.ifBlank { style?.takeIf { it.isNotBlank() } ?: "Autre" }
        val normalizedColor = normalizeColor(color)
        val image = imageUrl?.takeIf { it.isNotBlank() } ?: buildPlaceholder(normalizedColor)
        return Cloth(
            id = id,
            name = name.ifBlank { "Vêtement" },
            type = type.ifBlank { "Autre" },
            colorHex = normalizedColor,
            imageUrl = image,
            processedImageUrl = processedImageUrl,
            createdAt = createdAt,
            season = season,
            style = style,
            color = color,
            acceptedCount = acceptedCount,
            rejectedCount = rejectedCount,
            isProcessed = isProcessed ?: false,
            processingStatus = processingStatus
        )
    }

    private fun extractCategory(categoryElement: JsonElement?): String {
        if (categoryElement == null || categoryElement.isJsonNull) return ""
        return when {
            categoryElement.isJsonArray -> categoryElement.asJsonArray
                .mapNotNull { element -> element.takeIf { !it.isJsonNull }?.asString }
                .joinToString(separator = ", ")
            categoryElement.isJsonPrimitive -> categoryElement.asString
            else -> ""
        }.trim()
    }

    private fun normalizeColor(rawColor: String?): String {
        val defaultColor = "#F6D4E3"
        if (rawColor.isNullOrBlank()) return defaultColor
        val candidate = rawColor.trim()
        val withoutHash = candidate.removePrefix("#")
        val hex = when (withoutHash.length) {
            3 -> withoutHash.flatMap { listOf(it, it) }.joinToString(separator = "")
            6 -> withoutHash
            8 -> withoutHash.substring(2) // ignore alpha if present
            else -> return defaultColor
        }
        return "#${hex.uppercase()}"
    }

    private fun buildPlaceholder(colorHex: String): String {
        val sanitized = colorHex.removePrefix("#")
        return "https://singlecolorimage.com/get/${sanitized.uppercase()}/400x400"
    }

    // ✅ NOUVEAU : Fonctions de normalisation pour parser le JSON (comme iOS)
    private fun normalizeCategory(type: String): String {
        val v = type.lowercase()
        return when {
            v.contains("footwear") || v.contains("shoe") || v.contains("chaussure") -> "Shoes"
            v.contains("tshirt") || v.contains("shirt") || v.contains("haut") || v.contains("top") -> "Tshirt"
            v.contains("pant") || v.contains("jean") || v.contains("pantalon") || v.contains("bottom") -> "Pants"
            v.contains("dress") || v.contains("robe") -> "Dress"
            v.contains("jacket") || v.contains("veste") || v.contains("outerwear") -> "Jacket"
            v.contains("accessory") || v.contains("accessoire") -> "Accessory"
            else -> "Other"
        }
    }

    private fun normalizeColorHex(color: String): String {
        val defaultColor = "#808080"
        if (color.isBlank()) return defaultColor
        
        // Si c'est déjà un hex (commence par #)
        if (color.startsWith("#")) {
            val withoutHash = color.removePrefix("#")
            val hex = when (withoutHash.length) {
                3 -> withoutHash.flatMap { listOf(it, it) }.joinToString(separator = "")
                6 -> withoutHash
                8 -> withoutHash.substring(2) // ignore alpha if present
                else -> return defaultColor
            }
            return "#${hex.uppercase()}"
        }
        
        // Sinon, essayer de convertir le nom de couleur en hex
        return colorNameToHex(color) ?: defaultColor
    }

    private fun extractColorName(color: String): String {
        // Si c'est un hex, convertir en nom
        if (color.startsWith("#")) {
            return hexToColorName(color) ?: "Unknown"
        }
        // Sinon, utiliser directement le nom
        return color.capitalize()
    }

    private fun normalizeStyle(style: String): String {
        val v = style.lowercase()
        return when {
            v.contains("casual") -> "casual"
            v.contains("formal") || v.contains("elegant") || v.contains("chic") -> "formal"
            v.contains("sport") -> "sport"
            v.contains("vintage") -> "vintage"
            v.contains("modern") -> "modern"
            v.contains("boho") || v.contains("bohemian") -> "bohemian"
            else -> "casual"
        }
    }

    private fun normalizeSeason(season: String): String {
        val v = season.lowercase()
        return when {
            v.contains("summer") || v.contains("été") -> "summer"
            v.contains("winter") || v.contains("hiver") -> "winter"
            v.contains("fall") || v.contains("autumn") || v.contains("automne") -> "fall"
            v.contains("spring") || v.contains("printemps") -> "spring"
            else -> "all"
        }
    }

    private fun colorNameToHex(colorName: String): String? {
        val name = colorName.lowercase()
        return when {
            name.contains("pink") -> "#FFC0CB"
            name.contains("red") -> "#FF0000"
            name.contains("blue") -> "#0000FF"
            name.contains("black") -> "#000000"
            name.contains("white") -> "#FFFFFF"
            name.contains("green") -> "#008000"
            name.contains("yellow") -> "#FFFF00"
            name.contains("purple") -> "#800080"
            name.contains("orange") -> "#FFA500"
            name.contains("brown") -> "#A52A2A"
            name.contains("gray") || name.contains("grey") -> "#808080"
            else -> null
        }
    }

    private fun hexToColorName(hex: String): String? {
        val normalized = hex.uppercase()
        return when (normalized) {
            "#FFC0CB", "#FFB6C1" -> "Pink"
            "#FF0000", "#DC143C" -> "Red"
            "#0000FF", "#4169E1" -> "Blue"
            "#000000" -> "Black"
            "#FFFFFF" -> "White"
            "#008000", "#00FF00" -> "Green"
            "#FFFF00", "#FFD700" -> "Yellow"
            "#800080", "#9370DB" -> "Purple"
            "#FFA500" -> "Orange"
            "#A52A2A", "#8B4513" -> "Brown"
            "#808080", "#A9A9A9" -> "Gray"
            else -> null
        }
    }
}