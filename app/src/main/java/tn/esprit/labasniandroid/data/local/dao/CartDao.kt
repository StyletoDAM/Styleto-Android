package tn.esprit.labasniandroid.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tn.esprit.labasniandroid.data.local.entities.CartItem

/**
 * DAO pour les opérations sur CartItem (équivalent CoreData fetchRequest iOS)
 */
@Dao
interface CartDao {
    /**
     * Récupère tous les articles du panier pour un utilisateur
     * Triés par date d'ajout décroissante (comme iOS)
     */
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getCartItemsByUserId(userId: String): Flow<List<CartItem>>

    /**
     * Récupère un article du panier par ID
     */
    @Query("SELECT * FROM cart_items WHERE id = :id")
    suspend fun getCartItemById(id: String): CartItem?

    /**
     * Vérifie si un article existe déjà dans le panier
     */
    @Query("SELECT * FROM cart_items WHERE storeItemID = :storeItemID AND userId = :userId LIMIT 1")
    suspend fun getCartItemByStoreItemId(storeItemID: String, userId: String): CartItem?

    /**
     * Ajoute un article au panier (comme iOS addToCart)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCartItem(cartItem: CartItem)

    /**
     * Supprime un article du panier (comme iOS removeFromCart)
     */
    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    /**
     * Vide tout le panier pour un utilisateur (comme iOS clearCart)
     */
    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCartForUser(userId: String)

    /**
     * Compte les articles dans le panier pour un utilisateur
     */
    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    fun getItemCountByUserId(userId: String): Flow<Int>

    /**
     * Calcule le prix total du panier pour un utilisateur
     */
    @Query("SELECT SUM(price) FROM cart_items WHERE userId = :userId")
    fun getTotalPriceByUserId(userId: String): Flow<Double?>
}

