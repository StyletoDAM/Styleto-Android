package tn.esprit.labasniandroid.data.local.dao;

/**
 * DAO pour les opérations sur CartItem (équivalent CoreData fetchRequest iOS)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u000b\u001a\u0004\u0018\u00010\t2\u0006\u0010\f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J \u0010\r\u001a\u0004\u0018\u00010\t2\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00120\u00112\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0016\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00112\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0018\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u00112\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0016\u0010\u0017\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\n\u00a8\u0006\u0018"}, d2 = {"Ltn/esprit/labasniandroid/data/local/dao/CartDao;", "", "clearCartForUser", "", "userId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteCartItem", "cartItem", "Ltn/esprit/labasniandroid/data/local/entities/CartItem;", "(Ltn/esprit/labasniandroid/data/local/entities/CartItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCartItemById", "id", "getCartItemByStoreItemId", "storeItemID", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCartItemsByUserId", "Lkotlinx/coroutines/flow/Flow;", "", "getItemCountByUserId", "", "getTotalPriceByUserId", "", "insertCartItem", "app_debug"})
@androidx.room.Dao()
public abstract interface CartDao {
    
    /**
     * Récupère tous les articles du panier pour un utilisateur
     * Triés par date d'ajout décroissante (comme iOS)
     */
    @androidx.room.Query(value = "SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<tn.esprit.labasniandroid.data.local.entities.CartItem>> getCartItemsByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId);
    
    /**
     * Récupère un article du panier par ID
     */
    @androidx.room.Query(value = "SELECT * FROM cart_items WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCartItemById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super tn.esprit.labasniandroid.data.local.entities.CartItem> $completion);
    
    /**
     * Vérifie si un article existe déjà dans le panier
     */
    @androidx.room.Query(value = "SELECT * FROM cart_items WHERE storeItemID = :storeItemID AND userId = :userId LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCartItemByStoreItemId(@org.jetbrains.annotations.NotNull()
    java.lang.String storeItemID, @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super tn.esprit.labasniandroid.data.local.entities.CartItem> $completion);
    
    /**
     * Ajoute un article au panier (comme iOS addToCart)
     */
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertCartItem(@org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.data.local.entities.CartItem cartItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Supprime un article du panier (comme iOS removeFromCart)
     */
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteCartItem(@org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.data.local.entities.CartItem cartItem, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Vide tout le panier pour un utilisateur (comme iOS clearCart)
     */
    @androidx.room.Query(value = "DELETE FROM cart_items WHERE userId = :userId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearCartForUser(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Compte les articles dans le panier pour un utilisateur
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> getItemCountByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId);
    
    /**
     * Calcule le prix total du panier pour un utilisateur
     */
    @androidx.room.Query(value = "SELECT SUM(price) FROM cart_items WHERE userId = :userId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getTotalPriceByUserId(@org.jetbrains.annotations.NotNull()
    java.lang.String userId);
}