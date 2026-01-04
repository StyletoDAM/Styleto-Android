package tn.esprit.labasniandroid.utils

import android.os.Build
import tn.esprit.labasniandroid.BuildConfig

object APIConstants {
    private const val EMULATOR_BASE_URL = "http://10.0.2.2:3000"
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:3000"

    /**
     * Détecte si l'application s'exécute sur un émulateur Android.
     */
    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("google/sdk_gphone")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu"))
    }

    /**
     * URL du backend.
     *
     * Priorité de sélection :
     * 1. Si une URL est configurée dans `local.properties` (via `labasni.baseUrl`) → l'utiliser
     *    - URL HTTPS (production) : utilisée pour tous les appareils (émulateur et physique)
     *    - URL HTTP (développement local) : utilisée uniquement pour les appareils physiques
     * 2. Si émulateur et pas d'URL configurée → utilise `http://10.0.2.2:3000`
     * 3. Si appareil physique et pas d'URL configurée → utilise `http://10.0.2.2:3000`
     *
     * Configuration dans `local.properties` :
     * - Backend déployé (Render, etc.) : `labasni.baseUrl=https://labasni-backend-mh3j.onrender.com`
     * - Backend local (appareil physique) : `labasni.baseUrl=http://192.168.1.100:3000`
     * - Backend local (émulateur) : laissez vide ou commentez pour utiliser automatiquement `10.0.2.2:3000`
     * 
     * Pour trouver l'IP locale de votre Mac :
     * - Terminal : `ifconfig | grep "inet " | grep -v 127.0.0.1`
     * - Ou : Préférences Système > Réseau > Wi-Fi > Détails > TCP/IP > Adresse IPv4
     */
    val BASE_URL: String
        get() {
            val configuredUrl = BuildConfig.BASE_URL.trim()
            
            // Si une URL est configurée, l'utiliser (priorité absolue)
            if (configuredUrl.isNotBlank()) {
                // Si c'est une URL HTTPS (production), l'utiliser pour tous les appareils
                if (configuredUrl.startsWith("https://")) {
                    return configuredUrl
                }
                // Si c'est une URL HTTP (local) et qu'on est sur un appareil physique, l'utiliser
                if (!isEmulator() && configuredUrl.startsWith("http://")) {
                    return configuredUrl
                }
                // Si on est sur un émulateur avec une URL HTTP configurée, utiliser quand même
                // (au cas où l'utilisateur veuille forcer une IP locale sur l'émulateur)
                if (isEmulator() && configuredUrl.startsWith("http://")) {
                    return configuredUrl
                }
            }
            
            // Pas d'URL configurée : utiliser la détection automatique
            if (isEmulator()) {
                return EMULATOR_BASE_URL
            }
            
            // Appareil physique sans configuration : essayer quand même l'URL de l'émulateur
            // (au cas où l'appareil serait sur le même réseau)
            return DEFAULT_BASE_URL
        }
    
    const val SIGNUP_PATH = "/auth/signup"
    const val SIGNIN_PATH = "/auth/signin"
    const val GOOGLE_AUTH_PATH = "/auth/google"
    const val APPLE_AUTH_PATH = "/auth/apple"
    const val PROFILE_PATH = "/auth/profile"
    const val VERIFY_EMAIL_PATH = "/auth/verify-email"
    const val FORGOT_PASSWORD_PATH = "/auth/forgot-password"
    const val VERIFY_OTP_PATH = "/auth/verify-otp"
    const val RESET_PASSWORD_PATH = "/auth/reset-password"
    const val JSON_CONTENT_TYPE = "application/json"

    // Cloudinary
    // Renseignez votre cloud_name et le tag (ou dossier virtuel) qui regroupe les images visibles dans la galerie.
    const val CLOUDINARY_CLOUD_NAME = "your_cloud_name"
    const val CLOUDINARY_GALLERY_TAG = "profile_gallery"

    val CLOUDINARY_GALLERY_URL: String
        get() = "https://res.cloudinary.com/$CLOUDINARY_CLOUD_NAME/image/list/$CLOUDINARY_GALLERY_TAG.json"
}

