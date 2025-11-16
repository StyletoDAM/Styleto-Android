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
}
