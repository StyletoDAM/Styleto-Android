package tn.esprit.labasniandroid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color

// Palette actuelle (dominante rose) — utilisée pour les profils féminins
val PinkPrimary = Color(0xFFCA3C66)
val PinkSecondary = Color(0xFFDB6A8F)
val PinkGradientTop = Color(0xFFE8AABE)
val AquaSoft = Color(0xFFA7E0E0)
val TealAccent = Color(0xFF4AA3A2)
val DeepTeal = Color(0xFF2D6C6A)
val NeutralDark = Color(0xFF1F2933)
val NeutralSoft = Color(0xFF6B7280)

// Palette alternative (BLEUTheme)
// Couleurs dominantes (teal / aqua)
val BluePrimary = TealAccent          // #4AA3A2
val BlueSecondary = AquaSoft          // #A7E0E0
// Dégradé haut avec une touche de rose douce pour rester cohérent
val BlueGradientTop = PinkGradientTop // #E8AABE, utilisé plus en fond qu'en accent

// Couleurs pour mode sombre - Identiques à iOS DarkTheme
val DarkBackground = Color(0xFF1A1A2E) // Identique à iOS DarkTheme background
val DarkCard = Color(0xFF2A2A3E) // Identique à iOS DarkTheme card
val DarkText = Color(0xFFE0E0E0)
val DarkSecondaryText = Color(0xFFB0B0B0) // Identique à iOS DarkTheme secondaryText

// Couleurs mode clair
val LightBackground = Color(0xFFF5F5F5) // systemGroupedBackground équivalent
val LightCard = Color.White

/**
 * Système de couleurs dynamique basé sur le genre (comme iOS)
 * S'adapte automatiquement selon le genre (male/female) et le mode (light/dark)
 */
object DynamicThemeColors {
    /**
     * Détermine si le thème sombre est actif (comme iOS ThemeManager.updateTheme)
     */
    @Composable
    private fun isDarkTheme(): Boolean {
        val themeMode = ThemeController.themeMode.collectAsState().value
        return when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
    }
    
    /**
     * Retourne la couleur primaire selon le genre et le mode (comme iOS DarkTheme/LightTheme)
     */
    @Composable
    fun primary(isMale: Boolean): Color {
        val isDark = isDarkTheme()
        return when {
            isMale && isDark -> Color(0xFF6BC4C3) // Teal clair (comme iOS DarkTheme primary pour male)
            isMale && !isDark -> Color(0xFF4AA3A2) // Teal (comme iOS LightTheme primary pour male)
            !isMale && isDark -> Color(0xFFE85C8A) // Rose clair (comme iOS DarkTheme primary pour female)
            else -> Color(0xFFCA3C66) // Rose principal (comme iOS LightTheme primary pour female)
        }
    }

    /**
     * Retourne la couleur secondaire selon le genre et le mode (comme iOS DarkTheme/LightTheme)
     */
    @Composable
    fun secondary(isMale: Boolean): Color {
        val isDark = isDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFB8E8E8) // Aqua clair (comme iOS DarkTheme secondary pour male)
            isMale && !isDark -> Color(0xFF6BC4C3) // Aqua plus foncé (comme iOS LightTheme secondary pour male)
            !isMale && isDark -> Color(0xFFF07BA3) // Rose secondaire clair (comme iOS DarkTheme secondary pour female)
            else -> Color(0xFFDB6A8F) // Rose secondaire (comme iOS LightTheme secondary pour female)
        }
    }

    /**
     * Retourne la couleur softPink selon le genre et le mode (comme iOS DarkTheme/LightTheme)
     */
    @Composable
    fun softPink(isMale: Boolean): Color {
        val isDark = isDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFB8E8E8) // Aqua clair (comme iOS DarkTheme softPink pour male)
            isMale && !isDark -> Color(0xFFA7E0E0) // Aqua (comme iOS LightTheme softPink pour male)
            !isMale && isDark -> Color(0xFFF5B5C8) // Rose très doux (comme iOS DarkTheme softPink pour female)
            else -> Color(0xFFE8AABE) // Rose doux (comme iOS LightTheme softPink pour female)
        }
    }

    /**
     * Retourne la couleur aqua selon le genre et le mode (comme iOS DarkTheme/LightTheme - toujours inversé)
     */
    @Composable
    fun aqua(isMale: Boolean): Color {
        val isDark = isDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFF5B5C8) // Rose doux (inversé, comme iOS DarkTheme aqua pour male)
            isMale && !isDark -> Color(0xFFE8AABE) // Rose doux (inversé, comme iOS LightTheme aqua pour male)
            !isMale && isDark -> Color(0xFFB8E8E8) // Aqua clair (inversé, comme iOS DarkTheme aqua pour female)
            else -> Color(0xFFA7E0E0) // Aqua (inversé, comme iOS LightTheme aqua pour female)
        }
    }

    /**
     * Retourne la couleur teal selon le genre et le mode (comme iOS DarkTheme/LightTheme - toujours inversé)
     */
    @Composable
    fun teal(isMale: Boolean): Color {
        val isDark = isDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFE85C8A) // Rose clair (inversé, comme iOS DarkTheme teal pour male)
            isMale && !isDark -> Color(0xFFCA3C66) // Rose principal (inversé, comme iOS LightTheme teal pour male)
            !isMale && isDark -> Color(0xFF6BC4C3) // Teal clair (inversé, comme iOS DarkTheme teal pour female)
            else -> Color(0xFF4AA3A2) // Teal (inversé, comme iOS LightTheme teal pour female)
        }
    }

    /**
     * Retourne la couleur de fond selon le mode (comme iOS DarkTheme/LightTheme)
     */
    @Composable
    fun background(): Color {
        return if (isDarkTheme()) DarkBackground else LightBackground
    }

    /**
     * Retourne la couleur de carte selon le mode (comme iOS DarkTheme/LightTheme)
     */
    @Composable
    fun card(): Color {
        return if (isDarkTheme()) DarkCard else LightCard
    }

    /**
     * Retourne la couleur de texte selon le genre et le mode (comme iOS DarkTheme/LightTheme)
     */
    @Composable
    fun text(isMale: Boolean): Color {
        val isDark = isDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFF5B5C8) // Rose doux (comme iOS DarkTheme text pour male)
            isMale && !isDark -> Color(0xFFCA3C66) // Rose principal (comme iOS LightTheme text pour male)
            !isMale && isDark -> Color(0xFFE0E0E0) // Texte clair (comme iOS DarkTheme text pour female)
            else -> Color(0xFF4AA3A2) // Teal (comme iOS LightTheme text pour female)
        }
    }

    /**
     * Retourne la couleur de texte secondaire selon le mode (comme iOS DarkTheme/LightTheme)
     */
    @Composable
    fun secondaryText(): Color {
        return if (isDarkTheme()) DarkSecondaryText else Color(0xFF6B7280)
    }
}

/**
 * Couleurs par catégorie de vêtement (comme iOS CategoryColors)
 */
object CategoryColors {
    @Composable
    fun colorForCategory(category: String): Color {
        val themeMode = ThemeController.themeMode.collectAsState().value
        val isDark = when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
        val normalized = category.lowercase().trim().removeSuffix("s")
        
        return when (normalized) {
            "tshirt", "haut", "chemise" -> if (isDark) Color(0xFF6FB8B8) else Color(0xFFA7E0E0)
            "pantalon", "jean", "bas" -> if (isDark) Color(0xFF3C4B70) else Color(0xFF4D5F8F)
            "robe", "dress" -> if (isDark) Color(0xFFB85476) else Color(0xFFDB6A8F)
            "chaussure", "basket" -> if (isDark) Color(0xFFDADADA) else Color(0xFF4A4A4A)
            "accessoire", "sac", "bijou" -> if (isDark) Color(0xFFC9879B) else Color(0xFFE8AABE)
            else -> if (isDark) Color(0xFF8A8A8A) else Color(0xFFD3D3D3)
        }
    }
}
