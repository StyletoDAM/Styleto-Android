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

    private lateinit var preferences: SharedPreferences

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun initialize(context: Context) {
        if (::preferences.isInitialized) return
        preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = preferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        val initialMode = saved?.let {
            runCatching { ThemeMode.valueOf(it) }.getOrDefault(ThemeMode.SYSTEM)
        } ?: ThemeMode.SYSTEM
        _themeMode.value = initialMode
    }

    fun setThemeMode(mode: ThemeMode) {
        if (!::preferences.isInitialized) return
        _themeMode.value = mode
        preferences.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }
}
