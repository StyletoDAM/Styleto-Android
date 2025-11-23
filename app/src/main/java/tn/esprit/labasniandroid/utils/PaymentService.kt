package tn.esprit.labasniandroid.utils

import android.util.Log
import androidx.activity.ComponentActivity
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

/**
 * Service pour gérer les paiements Stripe
 * PaymentSheet est créé dans MainActivity.onCreate() pour éviter les problèmes de lifecycle
 */
object PaymentService {
    private var isInitialized = false
    private var paymentSheet: PaymentSheet? = null
    private var currentCallback: ((PaymentSheetResult) -> Unit)? = null
    
    /**
     * Initialise Stripe dans onCreate de l'Activity
     * DOIT être appelé depuis MainActivity.onCreate()
     */
    fun initialize(activity: ComponentActivity, publishableKey: String) {
        try {
            PaymentConfiguration.init(activity.applicationContext, publishableKey)
            isInitialized = true
            Log.d("PaymentService", "Stripe initialisé avec succès")
        } catch (e: Exception) {
            Log.e("PaymentService", "Erreur lors de l'initialisation: ${e.message}", e)
            isInitialized = false
        }
    }
    
    /**
     * Définit le PaymentSheet créé dans MainActivity.onCreate()
     * DOIT être appelé depuis MainActivity.onCreate()
     */
    fun setPaymentSheet(sheet: PaymentSheet) {
        paymentSheet = sheet
        Log.d("PaymentService", "PaymentSheet défini")
    }
    
    /**
     * Gère le résultat du paiement depuis MainActivity
     */
    fun handlePaymentResult(result: PaymentSheetResult) {
        currentCallback?.invoke(result)
        currentCallback = null
    }
    
    /**
     * Vérifie si Stripe est initialisé
     */
    private fun isInitialized(): Boolean {
        return isInitialized
    }

    /**
     * Lance le processus de paiement avec Stripe PaymentSheet
     * Utilise le PaymentSheet créé dans MainActivity.onCreate()
     * @param activity L'activité courante (non utilisée mais gardée pour compatibilité)
     * @param clientSecret Le client secret du Payment Intent
     * @param onResult Callback avec le résultat du paiement
     */
    fun presentPaymentSheet(
        activity: ComponentActivity,
        clientSecret: String,
        onResult: (PaymentSheetResult) -> Unit
    ) {
        try {
            if (!isInitialized()) {
                Log.e("PaymentService", "Stripe n'est pas initialisé.")
                onResult(PaymentSheetResult.Failed(
                    Exception("Stripe n'est pas initialisé")
                ))
                return
            }

            if (clientSecret.isBlank()) {
                Log.e("PaymentService", "ClientSecret est vide")
                onResult(PaymentSheetResult.Failed(
                    Exception("ClientSecret invalide")
                ))
                return
            }

            val sheet = paymentSheet
            if (sheet == null) {
                Log.e("PaymentService", "PaymentSheet n'est pas initialisé. Appelez setPaymentSheet() dans MainActivity.onCreate()")
                onResult(PaymentSheetResult.Failed(
                    Exception("PaymentSheet non initialisé")
                ))
                return
            }

            // Stocker le callback pour qu'il soit appelé depuis MainActivity
            currentCallback = onResult

            // Vérifier que nous sommes sur le thread principal
            if (android.os.Looper.myLooper() != android.os.Looper.getMainLooper()) {
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    presentPaymentSheet(activity, clientSecret, onResult)
                }
                return
            }

            val paymentSheetConfiguration = PaymentSheet.Configuration(
                merchantDisplayName = "Labasni"
            )

            // Utiliser le PaymentSheet créé dans onCreate
            sheet.presentWithPaymentIntent(
                paymentIntentClientSecret = clientSecret,
                configuration = paymentSheetConfiguration
            )
        } catch (e: Exception) {
            Log.e("PaymentService", "Erreur lors du lancement de PaymentSheet: ${e.message}", e)
            Log.e("PaymentService", "Stack trace: ${e.stackTraceToString()}")
            currentCallback = null
            onResult(PaymentSheetResult.Failed(e))
        }
    }
}

