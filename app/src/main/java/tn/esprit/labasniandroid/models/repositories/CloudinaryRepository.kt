package tn.esprit.labasniandroid.models.repositories

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.entities.CloudinaryImage
import tn.esprit.labasniandroid.utils.APIConstants
import java.io.IOException

class CloudinaryRepository(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val gson: Gson = Gson()
) {

    private data class CloudinaryListResponse(
        @SerializedName("resources") val resources: List<CloudinaryResourceDto> = emptyList()
    )

    private data class CloudinaryResourceDto(
        @SerializedName("public_id") val publicId: String,
        @SerializedName("secure_url") val secureUrl: String,
        @SerializedName("url") val url: String?,
        @SerializedName("format") val format: String?
    )

    suspend fun fetchGallery(): Result<List<CloudinaryImage>> = withContext(Dispatchers.IO) {
        if (APIConstants.CLOUDINARY_CLOUD_NAME.isBlank()) {
            return@withContext Result.failure(
                NetworkError.ServerMessage("Cloudinary n'est pas configuré. Renseignez CLOUDINARY_CLOUD_NAME.")
            )
        }

        try {
            val request = Request.Builder()
                .url(APIConstants.CLOUDINARY_GALLERY_URL)
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val message = when (response.code) {
                        404 -> "Aucune image trouvée pour la galerie Cloudinary."
                        else -> "Impossible de récupérer la galerie Cloudinary (code ${response.code})."
                    }
                    return@withContext Result.failure(NetworkError.ServerMessage(message))
                }

                val bodyString = response.body?.string()
                    ?: return@withContext Result.failure(NetworkError.ServerMessage("Réponse Cloudinary vide."))

                val listResponse = gson.fromJson(bodyString, CloudinaryListResponse::class.java)
                val images = listResponse.resources.map { resource ->
                    CloudinaryImage(
                        publicId = resource.publicId,
                        secureUrl = resource.secureUrl,
                        thumbnailUrl = resource.url
                    )
                }

                Result.success(images)
            }
        } catch (io: IOException) {
            Result.failure(NetworkError.Transport(io))
        } catch (ex: Exception) {
            Result.failure(NetworkError.ServerMessage(ex.message ?: "Erreur Cloudinary inconnue."))
        }
    }
}

