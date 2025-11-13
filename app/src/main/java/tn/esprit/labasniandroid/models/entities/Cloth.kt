package tn.esprit.labasniandroid.models.entities

data class Cloth(
    val id: String,
    val name: String,
    val type: String,
    val colorHex: String,
    val imageUrl: String,
    val createdAt: String? = null
)


