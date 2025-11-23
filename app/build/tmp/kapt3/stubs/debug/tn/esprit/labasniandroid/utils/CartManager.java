package tn.esprit.labasniandroid.utils;

/**
 * CartManager singleton (équivalent CartManager iOS avec CoreData)
 * Gère le panier avec Room (équivalent CoreData)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\n\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J,\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b$\u0010%J$\u0010&\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e2\u0006\u0010\"\u001a\u00020#H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\'\u0010(J\u0006\u0010)\u001a\u00020\u001fJ\u0006\u0010*\u001a\u00020\u001fJ\u000e\u0010+\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020#J\u001e\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020\f2\u0006\u0010\"\u001a\u00020#H\u0086@\u00a2\u0006\u0002\u0010/J\u0010\u00100\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020#H\u0002J$\u00101\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e2\u0006\u00102\u001a\u00020\u0006H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b3\u00104J\u0018\u00105\u001a\u00020\u001f2\b\u00106\u001a\u0004\u0018\u00010\f2\u0006\u0010\"\u001a\u00020#R\u001a\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012R\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\n0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0012\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u00067"}, d2 = {"Ltn/esprit/labasniandroid/utils/CartManager;", "", "()V", "_cartItems", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Ltn/esprit/labasniandroid/data/local/entities/CartItem;", "_itemCount", "", "_totalPrice", "", "cachedUserId", "", "cartDao", "Ltn/esprit/labasniandroid/data/local/dao/CartDao;", "cartItems", "Lkotlinx/coroutines/flow/SharedFlow;", "getCartItems", "()Lkotlinx/coroutines/flow/SharedFlow;", "database", "Ltn/esprit/labasniandroid/data/local/CartDatabase;", "itemCount", "getItemCount", "observerJob", "Lkotlinx/coroutines/Job;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "totalPrice", "getTotalPrice", "addToCart", "Lkotlin/Result;", "", "storeItem", "Ltn/esprit/labasniandroid/models/entities/StoreItem;", "context", "Landroid/content/Context;", "addToCart-0E7RQCE", "(Ltn/esprit/labasniandroid/models/entities/StoreItem;Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearCart", "clearCart-gIAlu-s", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "fetchCartItems", "handleLogout", "initialize", "isItemInCart", "", "storeItemId", "(Ljava/lang/String;Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadUserIdAndFetchCart", "removeFromCart", "cartItem", "removeFromCart-gIAlu-s", "(Ltn/esprit/labasniandroid/data/local/entities/CartItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateUserId", "userId", "app_debug"})
public final class CartManager {
    @org.jetbrains.annotations.Nullable()
    private static tn.esprit.labasniandroid.data.local.CartDatabase database;
    @org.jetbrains.annotations.Nullable()
    private static tn.esprit.labasniandroid.data.local.dao.CartDao cartDao;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.Nullable()
    private static java.lang.String cachedUserId;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<tn.esprit.labasniandroid.data.local.entities.CartItem>> _cartItems = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.SharedFlow<java.util.List<tn.esprit.labasniandroid.data.local.entities.CartItem>> cartItems = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _itemCount = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.SharedFlow<java.lang.Integer> itemCount = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Double> _totalPrice = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.SharedFlow<java.lang.Double> totalPrice = null;
    @org.jetbrains.annotations.Nullable()
    private static kotlinx.coroutines.Job observerJob;
    @org.jetbrains.annotations.NotNull()
    public static final tn.esprit.labasniandroid.utils.CartManager INSTANCE = null;
    
    private CartManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<java.util.List<tn.esprit.labasniandroid.data.local.entities.CartItem>> getCartItems() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<java.lang.Integer> getItemCount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<java.lang.Double> getTotalPrice() {
        return null;
    }
    
    /**
     * Initialise le CartManager (appelé au démarrage de l'app)
     */
    public final void initialize(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * Charge l'userId et le panier (comme iOS loadUserIdAndFetchCart)
     */
    private final void loadUserIdAndFetchCart(android.content.Context context) {
    }
    
    /**
     * Récupère les articles du panier pour l'utilisateur connecté (comme iOS fetchCartItems)
     * Observe les changements avec Flow (comme iOS observe CoreData)
     */
    public final void fetchCartItems() {
    }
    
    /**
     * Gère le logout en vidant le panier local (comme iOS handleLogout)
     * Note: Ne vide PAS la base de données, seulement le cache en mémoire
     * Les données restent dans Room et seront filtrées par userId lors du prochain login
     */
    public final void handleLogout() {
    }
    
    /**
     * Vérifie si un article est déjà dans le panier de l'utilisateur connecté
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isItemInCart(@org.jetbrains.annotations.NotNull()
    java.lang.String storeItemId, @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Met à jour l'userId (appelé lors du login/user update)
     * Recharge le panier de l'utilisateur connecté depuis la base de données
     */
    public final void updateUserId(@org.jetbrains.annotations.Nullable()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}