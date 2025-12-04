package tn.esprit.labasniandroid.models.services

import android.content.Context
import tn.esprit.labasniandroid.models.NetworkError
import tn.esprit.labasniandroid.models.Responses
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.models.repositories.AuthRepository
import tn.esprit.labasniandroid.utils.TokenManager

class AuthService(
    private val authRepository: AuthRepository = AuthRepository()
) {
    /**
     * Logique métier pour la connexion
     * - Valide les champs
     * - Appelle le repository
     * - Sauvegarde le token et l'ID utilisateur
     */
    suspend fun login(
        context: Context,
        email: String,
        password: String
    ): Result<Responses.SigninResponse> {
        // Validation métier
        if (email.isBlank()) {
            return Result.failure(NetworkError.ServerMessage("L'email est requis."))
        }
        if (password.isBlank()) {
            return Result.failure(NetworkError.ServerMessage("Le mot de passe est requis."))
        }
        if (password.length < 6) {
            return Result.failure(NetworkError.ServerMessage("Le mot de passe doit contenir au moins 6 caractères."))
        }

        // Appel au repository
        val result = authRepository.signin(email, password)

        // Logique métier après succès
        result.onSuccess { response ->
            // Sauvegarder le token
            TokenManager.saveToken(context, response.accessToken)
            // ✨ Sauvegarder le refresh token (obligatoire maintenant)
            TokenManager.saveRefreshToken(context, response.refreshToken)
            // Sauvegarder l'ID utilisateur
            val userId = response.user.userId
            if (userId.isNotEmpty()) {
                TokenManager.saveUserId(context, userId)
            }
            // Sauvegarder le genre pour le thème (male/female)
            TokenManager.saveGender(context, response.user.gender.value)
            // Synchroniser automatiquement le thème avec le genre (comme iOS)
            tn.esprit.labasniandroid.ui.theme.ThemeController.syncThemeVariantWithGender(
                context,
                response.user.gender
            )
        }

        return result
    }

    /**
     * Logique métier pour l'inscription
     * - Valide les champs
     * - Appelle le repository
     */
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        gender: User.Gender,
        phoneNumber: String? = null,
        preferences: List<String>? = null
    ): Result<Responses.SignupResponse> {
        // Validation métier
        if (fullName.isBlank()) {
            return Result.failure(NetworkError.ServerMessage("Le nom complet est requis."))
        }
        if (email.isBlank()) {
            return Result.failure(NetworkError.ServerMessage("L'email est requis."))
        }
        if (password.length < 6) {
            return Result.failure(NetworkError.ServerMessage("Le mot de passe doit contenir au moins 6 caractères."))
        }

        // Appel au repository
        return authRepository.signup(
            fullName = fullName,
            email = email,
            password = password,
            gender = gender,
            phoneNumber = phoneNumber,
            preferences = preferences
        )
    }

    /**
     * Logique métier pour l'authentification Google
     * - Appelle le repository
     * - Sauvegarde le token et l'ID utilisateur
     */
    suspend fun loginWithGoogle(
        context: Context,
        googleId: String,
        fullName: String,
        email: String,
        profilePicture: String? = null,
        gender: String? = null
    ): Result<Responses.SigninResponse> {
        // Appel au repository
        val result = authRepository.authenticateGoogle(
            googleId = googleId,
            fullName = fullName,
            email = email,
            profilePicture = profilePicture,
            gender = gender
        )

        // Logique métier après succès
        result.onSuccess { response ->
            TokenManager.saveToken(context, response.accessToken)
            // ✨ Sauvegarder le refresh token (obligatoire maintenant)
            TokenManager.saveRefreshToken(context, response.refreshToken)
            val userId = response.user.userId
            if (userId.isNotEmpty()) {
                TokenManager.saveUserId(context, userId)
            }
            TokenManager.saveGender(context, response.user.gender.value)
            // Synchroniser automatiquement le thème avec le genre (comme iOS)
            tn.esprit.labasniandroid.ui.theme.ThemeController.syncThemeVariantWithGender(
                context,
                response.user.gender
            )
        }

        return result
    }

    /**
     * Logique métier pour l'authentification Apple
     * - Appelle le repository
     * - Sauvegarde le token et l'ID utilisateur
     */
    suspend fun loginWithApple(
        context: Context,
        appleId: String,
        fullName: String,
        email: String,
        profilePicture: String? = null,
        gender: String? = null
    ): Result<Responses.SigninResponse> {
        // Appel au repository
        val result = authRepository.authenticateApple(
            appleId = appleId,
            fullName = fullName,
            email = email,
            profilePicture = profilePicture,
            gender = gender
        )

        // Logique métier après succès
        result.onSuccess { response ->
            TokenManager.saveToken(context, response.accessToken)
            // ✨ Sauvegarder le refresh token (obligatoire maintenant)
            TokenManager.saveRefreshToken(context, response.refreshToken)
            val userId = response.user.userId
            if (userId.isNotEmpty()) {
                TokenManager.saveUserId(context, userId)
            }
            TokenManager.saveGender(context, response.user.gender.value)
            // Synchroniser automatiquement le thème avec le genre (comme iOS)
            tn.esprit.labasniandroid.ui.theme.ThemeController.syncThemeVariantWithGender(
                context,
                response.user.gender
            )
        }

        return result
    }

    /**
     * Logique métier pour la réinitialisation du mot de passe
     * - Valide l'email
     * - Appelle le repository
     */
    suspend fun requestPasswordReset(email: String): Result<Responses.ForgotPasswordResponse> {
        // Validation métier
        if (email.isBlank()) {
            return Result.failure(NetworkError.ServerMessage("L'email est requis."))
        }

        // Appel au repository
        return authRepository.forgotPassword(email)
    }

    /**
     * Logique métier pour la vérification OTP
     * - Valide le code
     * - Appelle le repository
     */
    suspend fun verifyOtpCode(
        email: String,
        code: String
    ): Result<Responses.VerifyOtpResponse> {
        // Validation métier
        if (code.length != 6) {
            return Result.failure(NetworkError.ServerMessage("Le code doit contenir 6 chiffres."))
        }

        // Appel au repository
        return authRepository.verifyOtp(email, code)
    }

    /**
     * Logique métier pour la réinitialisation du mot de passe
     * - Valide le nouveau mot de passe
     * - Appelle le repository
     */
    suspend fun resetPassword(
        resetToken: String,
        newPassword: String
    ): Result<Responses.ResetPasswordResponse> {
        // Validation métier
        if (newPassword.length < 6) {
            return Result.failure(NetworkError.ServerMessage("Le mot de passe doit contenir au moins 6 caractères."))
        }

        // Appel au repository
        return authRepository.resetPassword(resetToken, newPassword)
    }

    /**
     * Logique métier pour la vérification d'email
     * - Valide le code
     * - Appelle le repository
     */
    suspend fun verifyEmailCode(
        tempToken: String,
        code: String
    ): Result<Responses.VerifyEmailResponse> {
        // Validation métier
        if (code.length != 6) {
            return Result.failure(NetworkError.ServerMessage("Le code doit contenir 6 chiffres."))
        }

        // Appel au repository
        return authRepository.verifyEmail(tempToken, code)
    }

    /**
     * Logique métier pour la déconnexion
     * - Supprime le token et l'ID utilisateur
     * - Vide le panier (comme iOS)
     */
    fun logout(context: Context) {
        TokenManager.clearToken(context)
        tn.esprit.labasniandroid.utils.CartManager.handleLogout()
    }
}

