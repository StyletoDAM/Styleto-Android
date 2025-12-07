package tn.esprit.labasniandroid.models.entities

data class Cloth(
    val id: String,
    val name: String,
    val type: String,
    val colorHex: String,
    val imageUrl: String,
    val processedImageUrl: String? = null,  // Ajout : URL de l'image traitée (transparente)
    val createdAt: String? = null,
    val season: String? = null,
    val style: String? = null,
    val color: String? = null,
    val acceptedCount: Int? = null,
    val rejectedCount: Int? = null,
    val isProcessed: Boolean = false,       // Ajout : Flag si prêt pour VTO
    val processingStatus: String? = "pending"  // Ajout : pending/processing/ready/failed
)

