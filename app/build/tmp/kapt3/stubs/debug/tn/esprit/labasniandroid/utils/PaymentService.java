package tn.esprit.labasniandroid.utils;

/**
 * Service pour gérer les paiements Stripe
 * PaymentSheet est créé dans MainActivity.onCreate() pour éviter les problèmes de lifecycle
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u000b\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u0005J\u0016\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\b\u0010\u0007\u001a\u00020\bH\u0002J*\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00112\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004J\u000e\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0016\u001a\u00020\nR\u001c\u0010\u0003\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Ltn/esprit/labasniandroid/utils/PaymentService;", "", "()V", "currentCallback", "Lkotlin/Function1;", "Lcom/stripe/android/paymentsheet/PaymentSheetResult;", "", "isInitialized", "", "paymentSheet", "Lcom/stripe/android/paymentsheet/PaymentSheet;", "handlePaymentResult", "result", "initialize", "activity", "Landroidx/activity/ComponentActivity;", "publishableKey", "", "presentPaymentSheet", "clientSecret", "onResult", "setPaymentSheet", "sheet", "app_debug"})
public final class PaymentService {
    private static boolean isInitialized = false;
    @org.jetbrains.annotations.Nullable()
    private static com.stripe.android.paymentsheet.PaymentSheet paymentSheet;
    @org.jetbrains.annotations.Nullable()
    private static kotlin.jvm.functions.Function1<? super com.stripe.android.paymentsheet.PaymentSheetResult, kotlin.Unit> currentCallback;
    @org.jetbrains.annotations.NotNull()
    public static final tn.esprit.labasniandroid.utils.PaymentService INSTANCE = null;
    
    private PaymentService() {
        super();
    }
    
    /**
     * Initialise Stripe dans onCreate de l'Activity
     * DOIT être appelé depuis MainActivity.onCreate()
     */
    public final void initialize(@org.jetbrains.annotations.NotNull()
    androidx.activity.ComponentActivity activity, @org.jetbrains.annotations.NotNull()
    java.lang.String publishableKey) {
    }
    
    /**
     * Définit le PaymentSheet créé dans MainActivity.onCreate()
     * DOIT être appelé depuis MainActivity.onCreate()
     */
    public final void setPaymentSheet(@org.jetbrains.annotations.NotNull()
    com.stripe.android.paymentsheet.PaymentSheet sheet) {
    }
    
    /**
     * Gère le résultat du paiement depuis MainActivity
     */
    public final void handlePaymentResult(@org.jetbrains.annotations.NotNull()
    com.stripe.android.paymentsheet.PaymentSheetResult result) {
    }
    
    /**
     * Vérifie si Stripe est initialisé
     */
    private final boolean isInitialized() {
        return false;
    }
    
    /**
     * Lance le processus de paiement avec Stripe PaymentSheet
     * Utilise le PaymentSheet créé dans MainActivity.onCreate()
     * @param activity L'activité courante (non utilisée mais gardée pour compatibilité)
     * @param clientSecret Le client secret du Payment Intent
     * @param onResult Callback avec le résultat du paiement
     */
    public final void presentPaymentSheet(@org.jetbrains.annotations.NotNull()
    androidx.activity.ComponentActivity activity, @org.jetbrains.annotations.NotNull()
    java.lang.String clientSecret, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.stripe.android.paymentsheet.PaymentSheetResult, kotlin.Unit> onResult) {
    }
}