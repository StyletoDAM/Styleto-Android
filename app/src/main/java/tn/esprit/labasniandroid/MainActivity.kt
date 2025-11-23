package tn.esprit.labasniandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import tn.esprit.labasniandroid.BuildConfig
import tn.esprit.labasniandroid.ui.theme.LabasniTheme
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.utils.CartManager
import tn.esprit.labasniandroid.utils.PaymentService

@androidx.compose.material3.ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    // Créer PaymentSheet dans onCreate pour éviter les problèmes de lifecycle
    private lateinit var paymentSheet: PaymentSheet
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeController.initialize(applicationContext)
        CartManager.initialize(applicationContext)
        
        // Initialiser Stripe
        val stripePublishableKey = BuildConfig.STRIPE_PUBLISHABLE_KEY
        if (stripePublishableKey.isNotBlank()) {
            PaymentService.initialize(this, stripePublishableKey)
        } else {
            android.util.Log.e("MainActivity", "STRIPE_PUBLISHABLE_KEY est vide. Ajoutez-la dans local.properties")
        }
        
        // Créer PaymentSheet dans onCreate (AVANT que l'Activity soit RESUMED)
        paymentSheet = PaymentSheet(
            activity = this,
            callback = ::onPaymentSheetResult
        )
        
        // Stocker le PaymentSheet dans PaymentService pour qu'il soit accessible
        PaymentService.setPaymentSheet(paymentSheet)
        
        setContent {
            val themeMode by ThemeController.themeMode.collectAsState()
            val themeVariant by ThemeController.themeVariant.collectAsState()
            LabasniTheme(themeMode = themeMode, variant = themeVariant) {
                Surface {
                    LabasniApp()
                }
            }
        }
    }
    
    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        // Le callback sera géré par PaymentService
        PaymentService.handlePaymentResult(paymentSheetResult)
    }
}
