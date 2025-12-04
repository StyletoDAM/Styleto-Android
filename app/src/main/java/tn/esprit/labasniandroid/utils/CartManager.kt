package tn.esprit.labasniandroid.utils

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.labasniandroid.data.local.entities.CartItem
import tn.esprit.labasniandroid.models.entities.StoreItem
import tn.esprit.labasniandroid.models.repositories.CartRepository

/**
 * CartManager singleton - ✨ NOUVEAU : Utilise l'API backend au lieu de Room
 * Gère le panier avec des appels API REST
 */
object CartManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Cache de l'ID utilisateur
    private var cachedUserId: String? = null

    // Flow pour observer les changements du panier
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: SharedFlow<List<CartItem>> = _cartItems.asSharedFlow()

    // Flow pour le nombre d'articles
    private val _itemCount = MutableStateFlow<Int>(0)
    val itemCount: SharedFlow<Int> = _itemCount.asSharedFlow()

    // Flow pour le prix total
    private val _totalPrice = MutableStateFlow<Double>(0.0)
    val totalPrice: SharedFlow<Double> = _totalPrice.asSharedFlow()

    /**
     * Initialise le CartManager (appelé au démarrage de l'app)
     */
    fun initialize(context: Context) {
        // Charger l'userId et le panier
        loadUserIdAndFetchCart(context)
    }

    /**
     * Charge l'userId et le panier depuis l'API
     */
    private fun loadUserIdAndFetchCart(context: Context) {
        scope.launch {
            cachedUserId = TokenManager.getUserId(context)

            if (cachedUserId != null) {
                android.util.Log.d("CartManager", "Utilisateur chargé: $cachedUserId")
            } else {
                android.util.Log.w("CartManager", "Aucun utilisateur connecté au démarrage")
            }

            // Charger le panier après avoir récupéré l'userId
            fetchCartItems(context)
        }
    }

    /**
     * ✨ NOUVEAU : Récupère les articles du panier depuis l'API
     */
    fun fetchCartItems(context: Context) {
        val userId = cachedUserId ?: TokenManager.getUserId(context)

        if (userId == null) {
            android.util.Log.w("CartManager", "Aucun utilisateur connecté – panier vide")
            scope.launch {
                withContext(Dispatchers.Main) {
                    _cartItems.value = emptyList()
                    _itemCount.value = 0
                    _totalPrice.value = 0.0
                }
            }
            return
        }

        scope.launch {
            try {
                val result = CartRepository.getCart(context)
                result.onSuccess { items ->
                    withContext(Dispatchers.Main) {
                        // ✨ NOUVEAU : Mettre à jour le userId pour chaque item
                        val itemsWithUserId = items.map { it.copy(userId = userId) }
                        _cartItems.value = itemsWithUserId
                        _itemCount.value = itemsWithUserId.size

                        // Calculer le prix total (seulement pour les articles disponibles)
                        val total = itemsWithUserId
                            .filter { it.status == "available" }
                            .sumOf { it.price }
                        _totalPrice.value = total

                        android.util.Log.d("CartManager", "${itemsWithUserId.size} articles chargés depuis l'API pour l'utilisateur $userId")
                    }
                }.onFailure { error ->
                    android.util.Log.e("CartManager", "Erreur fetch panier: ${error.message}", error)
                    withContext(Dispatchers.Main) {
                        _cartItems.value = emptyList()
                        _itemCount.value = 0
                        _totalPrice.value = 0.0
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CartManager", "Exception fetch panier: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _cartItems.value = emptyList()
                    _itemCount.value = 0
                    _totalPrice.value = 0.0
                }
            }
        }
    }

    /**
     * ✨ NOUVEAU : Ajoute un article au panier via l'API
     */
    suspend fun addToCart(storeItem: StoreItem, context: Context): Result<Unit> {
        val userId = cachedUserId ?: TokenManager.getUserId(context)

        if (userId == null) {
            android.util.Log.w("CartManager", "Impossible d'ajouter : utilisateur non connecté")
            return Result.failure(Exception("Utilisateur non connecté"))
        }

        // Mettre à jour le cache si nécessaire
        if (cachedUserId == null) {
            cachedUserId = userId
        }

        return try {
            val result = CartRepository.addToCart(context, storeItem.id)
            result.onSuccess {
                // Recharger le panier après ajout
                fetchCartItems(context)
                android.util.Log.d("CartManager", "Article ajouté au panier de $userId")
            }
            result
        } catch (e: Exception) {
            android.util.Log.e("CartManager", "Erreur lors de l'ajout: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * ✨ NOUVEAU : Supprime un article du panier via l'API
     */
    suspend fun removeFromCart(cartItem: CartItem, context: Context): Result<Unit> {
        return try {
            val result = CartRepository.removeFromCart(context, cartItem.storeItemID)
            result.onSuccess {
                // Recharger le panier après suppression
                fetchCartItems(context)
                android.util.Log.d("CartManager", "Article supprimé du panier")
            }
            result
        } catch (e: Exception) {
            android.util.Log.e("CartManager", "Erreur lors de la suppression: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * ✨ NOUVEAU : Vide tout le panier via l'API
     */
    suspend fun clearCart(context: Context): Result<Unit> {
        val userId = cachedUserId ?: TokenManager.getUserId(context)

        if (userId == null) {
            android.util.Log.w("CartManager", "Impossible de vider le panier : utilisateur non connecté")
            return Result.failure(Exception("Utilisateur non connecté"))
        }

        return try {
            val result = CartRepository.clearCart(context)
            result.onSuccess {
                // Mettre à jour le cache local
                withContext(Dispatchers.Main) {
                    _cartItems.value = emptyList()
                    _itemCount.value = 0
                    _totalPrice.value = 0.0
                }
                android.util.Log.d("CartManager", "Panier vidé pour l'utilisateur $userId")
            }
            result
        } catch (e: Exception) {
            android.util.Log.e("CartManager", "Erreur lors du vidage: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Gère le logout en vidant le panier local
     */
    fun handleLogout() {
        scope.launch {
            // Vider le cache en mémoire
            withContext(Dispatchers.Main) {
                _cartItems.value = emptyList()
                _itemCount.value = 0
                _totalPrice.value = 0.0
            }
            cachedUserId = null
        }
        android.util.Log.d("CartManager", "Panier vidé après logout")
    }

    /**
     * ✨ NOUVEAU : Vérifie si un article est déjà dans le panier
     */
    suspend fun isItemInCart(storeItemId: String, context: Context): Boolean {
        val userId = cachedUserId ?: TokenManager.getUserId(context)
        
        if (userId == null) {
            return false
        }
        
        return try {
            val result = CartRepository.getCart(context)
            result.getOrNull()?.any { it.storeItemID == storeItemId } ?: false
        } catch (e: Exception) {
            android.util.Log.e("CartManager", "Erreur vérification panier: ${e.message}", e)
            false
        }
    }

    /**
     * ✨ NOUVEAU : Met à jour l'userId et recharge le panier depuis l'API
     */
    fun updateUserId(userId: String?, context: Context) {
        val newUserId = userId ?: TokenManager.getUserId(context)
        
        // Si l'userId change, recharger
        if (cachedUserId != newUserId) {
            cachedUserId = newUserId
            
            if (newUserId != null) {
                android.util.Log.d("CartManager", "Utilisateur changé: $newUserId, rechargement du panier")
                fetchCartItems(context)
            } else {
                android.util.Log.w("CartManager", "Aucun utilisateur, panier vide")
                scope.launch {
                    withContext(Dispatchers.Main) {
                        _cartItems.value = emptyList()
                        _itemCount.value = 0
                        _totalPrice.value = 0.0
                    }
                }
            }
        } else if (newUserId != null) {
            // Même utilisateur, juste recharger pour s'assurer que les données sont à jour
            fetchCartItems(context)
        }
        
        android.util.Log.d("CartManager", "Utilisateur mis à jour: $newUserId, panier rechargé")
    }

    /**
     * ✨ NOUVEAU : Vérifie le statut des articles et met à jour le panier local
     */
    fun refreshCartStatus(context: Context) {
        scope.launch {
            try {
                val statusResult = CartRepository.checkItemsStatus(context)
                statusResult.onSuccess { statusMap ->
                    // Mettre à jour les statuts dans le panier local
                    withContext(Dispatchers.Main) {
                        val updatedItems = _cartItems.value.map { item ->
                            val newStatus = statusMap[item.storeItemID] ?: "available"
                            item.copy(status = newStatus)
                        }
                        _cartItems.value = updatedItems

                        // Recalculer le prix total (seulement pour les articles disponibles)
                        val total = updatedItems
                            .filter { it.status == "available" }
                            .sumOf { it.price }
                        _totalPrice.value = total
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CartManager", "Erreur refresh statut: ${e.message}", e)
            }
        }
    }
}
