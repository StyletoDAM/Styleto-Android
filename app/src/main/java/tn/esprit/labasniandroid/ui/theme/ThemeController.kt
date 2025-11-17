package tn.esprit.labasniandroid.ui.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

object ThemeController {
    private const val PREFS_NAME = "labasni_theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_THEME_VARIANT = "theme_variant"

    private lateinit var preferences: SharedPreferences

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _themeVariant = MutableStateFlow(ThemeVariant.PINK)
    val themeVariant: StateFlow<ThemeVariant> = _themeVariant.asStateFlow()

    fun initialize(context: Context) {
        if (::preferences.isInitialized) return
        preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = preferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        val initialMode = saved?.let {
            runCatching { ThemeMode.valueOf(it) }.getOrDefault(ThemeMode.SYSTEM)
        } ?: ThemeMode.SYSTEM
        _themeMode.value = initialMode

        val savedVariant = preferences.getString(KEY_THEME_VARIANT, ThemeVariant.PINK.name)
        val initialVariant = savedVariant?.let {
            runCatching { ThemeVariant.valueOf(it) }.getOrDefault(ThemeVariant.PINK)
        } ?: ThemeVariant.PINK
        _themeVariant.value = initialVariant
    }

    fun setThemeMode(mode: ThemeMode) {
        if (!::preferences.isInitialized) return
        _themeMode.value = mode
        preferences.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun setThemeVariant(variant: ThemeVariant) {
        if (!::preferences.isInitialized) return
        _themeVariant.value = variant
        preferences.edit().putString(KEY_THEME_VARIANT, variant.name).apply()
    }
    
    /**
     * Synchronise automatiquement le themeVariant avec le genre de l'utilisateur
     * (comme iOS: Male = BLUE, Female = PINK)
     * Cette fonction doit être appelée lors du chargement du profil utilisateur
     * et lors de la mise à jour du genre dans les settings
     */
    fun syncThemeVariantWithGender(context: Context, gender: tn.esprit.labasniandroid.models.entities.User.Gender) {
        if (!::preferences.isInitialized) {
            initialize(context)
        }
        val variant = when (gender) {
            tn.esprit.labasniandroid.models.entities.User.Gender.MALE -> ThemeVariant.BLUE
            tn.esprit.labasniandroid.models.entities.User.Gender.FEMALE -> ThemeVariant.PINK
        }
        // Ne mettre à jour que si différent pour éviter les recompositions inutiles
        if (_themeVariant.value != variant) {
            setThemeVariant(variant)
        }
    }
    
    /**
     * Synchronise automatiquement le themeVariant avec le genre depuis TokenManager
     * (utilisé lors de l'initialisation si l'utilisateur est déjà connecté)
     */
    fun syncThemeVariantWithSavedGender(context: Context) {
        if (!::preferences.isInitialized) {
            initialize(context)
        }
        val savedGender = tn.esprit.labasniandroid.utils.TokenManager.getGender(context)
        if (savedGender != null) {
            val gender = when (savedGender.lowercase()) {
                "male" -> tn.esprit.labasniandroid.models.entities.User.Gender.MALE
                "female" -> tn.esprit.labasniandroid.models.entities.User.Gender.FEMALE
                else -> null
            }
            gender?.let { syncThemeVariantWithGender(context, it) }
        }
    }
}
