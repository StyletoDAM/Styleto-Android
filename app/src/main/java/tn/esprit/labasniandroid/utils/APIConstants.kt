package tn.esprit.labasniandroid.utils

object APIConstants {
    // Pour l'émulateur Android, utilisez 10.0.2.2 pour accéder à localhost de la machine hôte
    // Pour un appareil physique, remplacez par l'adresse IP locale de votre machine (ex: 192.168.1.xxx)
    // Exemple pour appareil physique: const val BASE_URL = "http://192.168.1.100:3000"
    const val BASE_URL = "http://10.0.2.2:3000"
    
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
}

