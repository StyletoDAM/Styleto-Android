package tn.esprit.labasniandroid.models.entities

data class Outfit(
    val id: String,
    val clothes: List<Cloth>,
    val eventType: String?,
    val weatherType: String?,
    val status: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val isFavorite: Boolean = false
)
