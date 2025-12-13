package tn.esprit.labasniandroid.models.entities

data class StoreItem(
    val id: String,
    val cloth: Cloth?,
    val price: Double,
    val size: String?,
    val status: String?,
    val condition: String? = null,
    val createdAt: String?,
    val updatedAt: String?,
    val ownerId: String? = null,
    val ownerName: String? = null,
    val ownerAvatar: String? = null
) {
    fun getConditionDisplayName(): String {
        return when (condition) {
            "new" -> "New"
            "used" -> "Used"
            "damaged" -> "Damaged"
            else -> "New"
        }
    }
    
    fun getConditionColor(): androidx.compose.ui.graphics.Color {
        return when (condition) {
            "new" -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Green
            "used" -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Orange
            "damaged" -> androidx.compose.ui.graphics.Color(0xFFF44336) // Red
            else -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        }
    }
}

