package tn.esprit.labasniandroid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * CartItem entity pour Room (équivalent CartItem CoreData iOS)
 * ✨ NOUVEAU : Inclut le statut de l'article (available/sold)
 */
@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey
    val id: String,
    val userId: String,
    val storeItemID: String,
    val title: String?,
    val size: String?,
    val price: Double,
    val imageURL: String?,
    val addedAt: Long = System.currentTimeMillis(), // Timestamp en millisecondes
    val status: String = "available" // ✨ NOUVEAU : "available" | "sold"
)

