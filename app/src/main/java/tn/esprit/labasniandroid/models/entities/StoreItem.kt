package tn.esprit.labasniandroid.models.entities

data class StoreItem(
    val id: String,
    val cloth: Cloth?,
    val price: Double,
    val size: String?,
    val status: String?,
    val createdAt: String?,
    val updatedAt: String?
)

