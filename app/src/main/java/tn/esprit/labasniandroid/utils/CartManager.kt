package tn.esprit.labasniandroid.utils

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.labasniandroid.data.local.CartDatabase
import tn.esprit.labasniandroid.data.local.dao.CartDao
import tn.esprit.labasniandroid.data.local.entities.CartItem
import tn.esprit.labasniandroid.models.entities.StoreItem
import java.util.UUID

/**
 * CartManager singleton (équivalent CartManager iOS avec CoreData)
 * Gère le panier avec Room (équivalent CoreData)
 */
object CartManager {
    private var database: CartDatabase? = null
    private var cartDao: CartDao? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Cache de l'ID utilisateur (comme iOS cachedUserId)
    private var cachedUserId: String? = null

    // Flow pour observer les changements du panier (comme iOS @Published cartItems)
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: SharedFlow<List<CartItem>> = _cartItems.asSharedFlow()

    // Flow pour le nombre d'articles (comme iOS itemCount)
    private val _itemCount = MutableStateFlow<Int>(0)
    val itemCount: SharedFlow<Int> = _itemCount.asSharedFlow()

    // Flow pour le prix total (comme iOS totalPrice)
    private val _totalPrice = MutableStateFlow<Double>(0.0)
    val totalPrice: SharedFlow<Double> = _totalPrice.asSharedFlow()

    /**
     * Initialise le CartManager (appelé au démarrage de l'app)
     */
    fun initialize(context: Context) {
        database = CartDatabase.getDatabase(context)
        cartDao = database?.cartDao()

        // Charger l'userId et le panier
        loadUserIdAndFetchCart(context)
    }

    /**
     * Charge l'userId et le panier (comme iOS loadUserIdAndFetchCart)
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
            fetchCartItems()
        }
    }

    // Job pour l'observer Flow
    private var observerJob: kotlinx.coroutines.Job? = null

    /**
     * Récupère les articles du panier pour l'utilisateur connecté (comme iOS fetchCartItems)
     * Observe les changements avec Flow (comme iOS observe CoreData)
     */
    fun fetchCartItems() {
        val userId = cachedUserId ?: run {
            android.util.Log.w("CartManager", "Aucun utilisateur connecté – panier vide")
            scope.launch {
                _cartItems.value = emptyList()
                _itemCount.value = 0
                _totalPrice.value = 0.0
            }
            return
        }

        val dao = cartDao ?: run {
            android.util.Log.e("CartManager", "CartDao non initialisé")
            return
        }

        // Annuler l'ancien observer s'il existe
        observerJob?.cancel()

        // Observer les changements du panier (comme iOS observe CoreData)
        observerJob = scope.launch {
            try {
                dao.getCartItemsByUserId(userId).collect { items ->
                    withContext(Dispatchers.Main) {
                        _cartItems.value = items
                        _itemCount.value = items.size

                        // Calculer le prix total
                        val total = items.sumOf { it.price }
                        _totalPrice.value = total

                        android.util.Log.d("CartManager", "${items.size} articles chargés pour l'utilisateur $userId")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CartManager", "Erreur fetch panier: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _cartItems.value = emptyList()
                    _itemCount.value = 0
                    _totalPrice.value = 0.0
                }
            }
        }
    }

    /**
     * Ajoute un article au panier de l'utilisateur connecté (comme iOS addToCart)
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

        val dao = cartDao ?: run {
            android.util.Log.e("CartManager", "CartDao non initialisé")
            return Result.failure(Exception("CartDao non initialisé"))
        }

        return try {
            // Vérifier si l'article existe déjà
            val existing = dao.getCartItemByStoreItemId(storeItem.id, userId)
            if (existing != null) {
                android.util.Log.i("CartManager", "Article déjà dans le panier")
                return Result.success(Unit)
            }

            // Créer un nouvel article
            val newItem = CartItem(
                id = UUID.randomUUID().toString(),
                userId = userId,
                storeItemID = storeItem.id,
                title = storeItem.cloth?.type?.replaceFirstChar { it.uppercaseChar() } ?: "Article",
                size = storeItem.size,
                price = storeItem.price,
                imageURL = storeItem.cloth?.imageUrl,
                addedAt = System.currentTimeMillis()
            )

            // Insérer dans la base de données
            dao.insertCartItem(newItem)

            // Le Flow observera automatiquement le changement et mettra à jour _cartItems
            android.util.Log.d("CartManager", "Article ajouté au panier de $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("CartManager", "Erreur lors de l'ajout: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Supprime un article du panier (comme iOS removeFromCart)
     */
    suspend fun removeFromCart(cartItem: CartItem): Result<Unit> {
        val dao = cartDao ?: run {
            android.util.Log.e("CartManager", "CartDao non initialisé")
            return Result.failure(Exception("CartDao non initialisé"))
        }

        return try {
            dao.deleteCartItem(cartItem)
            // Le Flow observera automatiquement le changement
            android.util.Log.d("CartManager", "Article supprimé du panier")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("CartManager", "Erreur lors de la suppression: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Vide tout le panier de l'utilisateur connecté (comme iOS clearCart)
     */
    suspend fun clearCart(context: Context): Result<Unit> {
        val userId = cachedUserId ?: TokenManager.getUserId(context)

        if (userId == null) {
            android.util.Log.w("CartManager", "Impossible de vider le panier : utilisateur non connecté")
            return Result.failure(Exception("Utilisateur non connecté"))
        }

        val dao = cartDao ?: run {
            android.util.Log.e("CartManager", "CartDao non initialisé")
            return Result.failure(Exception("CartDao non initialisé"))
        }

        return try {
            dao.clearCartForUser(userId)
            // Le Flow observera automatiquement le changement
            android.util.Log.d("CartManager", "Panier vidé pour l'utilisateur $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("CartManager", "Erreur lors du vidage: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Gère le logout en vidant le panier local (comme iOS handleLogout)
     * Note: Ne vide PAS la base de données, seulement le cache en mémoire
     * Les données restent dans Room et seront filtrées par userId lors du prochain login
     */
    fun handleLogout() {
        scope.launch {
            // Annuler l'observer actuel
            observerJob?.cancel()
            
            // Vider le cache en mémoire
            _cartItems.value = emptyList()
            _itemCount.value = 0
            _totalPrice.value = 0.0
            cachedUserId = null
        }
        android.util.Log.d("CartManager", "Panier vidé après logout")
    }

    /**
     * Vérifie si un article est déjà dans le panier de l'utilisateur connecté
     */
    suspend fun isItemInCart(storeItemId: String, context: Context): Boolean {
        val userId = cachedUserId ?: TokenManager.getUserId(context)
        
        if (userId == null) {
            return false
        }
        
        val dao = cartDao ?: return false
        
        return try {
            val existing = dao.getCartItemByStoreItemId(storeItemId, userId)
            existing != null
        } catch (e: Exception) {
            android.util.Log.e("CartManager", "Erreur vérification panier: ${e.message}", e)
            false
        }
    }

    /**
     * Met à jour l'userId (appelé lors du login/user update)
     * Recharge le panier de l'utilisateur connecté depuis la base de données
     */
    fun updateUserId(userId: String?, context: Context) {
        val newUserId = userId ?: TokenManager.getUserId(context)
        
        // Si l'userId change, annuler l'ancien observer et recharger
        if (cachedUserId != newUserId) {
            observerJob?.cancel()
            cachedUserId = newUserId
            
            if (newUserId != null) {
                android.util.Log.d("CartManager", "Utilisateur changé: $newUserId, rechargement du panier")
                fetchCartItems()
            } else {
                android.util.Log.w("CartManager", "Aucun utilisateur, panier vide")
                scope.launch {
                    _cartItems.value = emptyList()
                    _itemCount.value = 0
                    _totalPrice.value = 0.0
                }
            }
        } else if (newUserId != null) {
            // Même utilisateur, juste recharger pour s'assurer que les données sont à jour
            fetchCartItems()
        }
        
        android.util.Log.d("CartManager", "Utilisateur mis à jour: $newUserId, panier rechargé")
    }
}

