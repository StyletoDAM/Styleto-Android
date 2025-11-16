package tn.esprit.labasniandroid.utils

import tn.esprit.labasniandroid.BuildConfig

object APIConstants {
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:3000"

    /**
     * URL du backend.
     *
     * - Émulateur officiel : laissez `labasni.baseUrl` vide pour utiliser 10.0.2.2.
     * - Téléphone réel : dans `local.properties`, ajoutez `labasni.baseUrl=http://<ip_de_votre_mac>:3000`.
     */
    val BASE_URL: String = BuildConfig.BASE_URL.ifBlank { DEFAULT_BASE_URL }
    
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

