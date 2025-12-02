package tn.esprit.labasniandroid.models.entities

data class Cloth(
    val id: String,
    val name: String,
    val type: String,
    val colorHex: String,
    val imageUrl: String,
    val createdAt: String? = null,
    val season: String? = null, // Spring, Summer, Fall, Winter, All
    val style: String? = null, // Casual, Elegant, Sport, Vintage, Modern, Bohemian
    val color: String? = null, // Color name (e.g., "Pink", "Blue")
    val acceptedCount: Int? = null,
    val rejectedCount: Int? = null
)


