package tn.esprit.labasniandroid

import android.app.Application
import android.util.Log
import com.stripe.android.PaymentConfiguration
import tn.esprit.labasniandroid.utils.PaymentService

class LabasniApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialiser Stripe avec la clé publique depuis BuildConfig
        val stripePublishableKey = BuildConfig.STRIPE_PUBLISHABLE_KEY
        if (stripePublishableKey.isNotBlank()) {
            try {
                PaymentConfiguration.init(applicationContext, stripePublishableKey)
                // PaymentService est maintenant initialisé dans MainActivity.onCreate()
                Log.d("LabasniApplication", "Stripe initialisé avec succès")
            } catch (e: Exception) {
                Log.e("LabasniApplication", "Erreur lors de l'initialisation de Stripe: ${e.message}", e)
            }
        } else {
            Log.e("LabasniApplication", "STRIPE_PUBLISHABLE_KEY est vide. Ajoutez-la dans local.properties")
        }
    }
}

