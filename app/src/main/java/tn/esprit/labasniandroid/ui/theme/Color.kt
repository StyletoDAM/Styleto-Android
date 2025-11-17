package tn.esprit.labasniandroid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
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

// Couleurs pour mode sombre
val DarkBackground = Color(0xFF1A1A2E)
val DarkCard = Color(0xFF2A2A3E)
val DarkText = Color(0xFFE0E0E0)
val DarkSecondaryText = Color(0xFFB0B0B0)

// Couleurs mode clair
val LightBackground = Color(0xFFF5F5F5) // systemGroupedBackground équivalent
val LightCard = Color.White

/**
 * Système de couleurs dynamique basé sur le genre (comme iOS)
 * S'adapte automatiquement selon le genre (male/female) et le mode (light/dark)
 */
object DynamicThemeColors {
    /**
     * Retourne la couleur primaire selon le genre et le mode
     */
    @Composable
    fun primary(isMale: Boolean): Color {
        val isDark = isSystemInDarkTheme()
        return when {
            isMale && isDark -> Color(0xFF6BC4C3) // Teal clair
            isMale && !isDark -> Color(0xFF4AA3A2) // Teal
            !isMale && isDark -> Color(0xFFE85C8A) // Rose clair
            else -> Color(0xFFCA3C66) // Rose principal
        }
    }

    /**
     * Retourne la couleur secondaire selon le genre et le mode
     */
    @Composable
    fun secondary(isMale: Boolean): Color {
        val isDark = isSystemInDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFB8E8E8) // Aqua clair
            isMale && !isDark -> Color(0xFF6BC4C3) // Aqua plus foncé
            !isMale && isDark -> Color(0xFFF07BA3) // Rose secondaire clair
            else -> Color(0xFFDB6A8F) // Rose secondaire
        }
    }

    /**
     * Retourne la couleur softPink selon le genre et le mode
     */
    @Composable
    fun softPink(isMale: Boolean): Color {
        val isDark = isSystemInDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFB8E8E8) // Aqua clair
            isMale && !isDark -> Color(0xFFA7E0E0) // Aqua
            !isMale && isDark -> Color(0xFFF5B5C8) // Rose très doux
            else -> Color(0xFFE8AABE) // Rose doux
        }
    }

    /**
     * Retourne la couleur aqua selon le genre et le mode
     */
    @Composable
    fun aqua(isMale: Boolean): Color {
        val isDark = isSystemInDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFF5B5C8) // Rose doux (inversé)
            isMale && !isDark -> Color(0xFFE8AABE) // Rose doux (inversé)
            !isMale && isDark -> Color(0xFFB8E8E8) // Aqua clair
            else -> Color(0xFFA7E0E0) // Aqua
        }
    }

    /**
     * Retourne la couleur teal selon le genre et le mode
     */
    @Composable
    fun teal(isMale: Boolean): Color {
        val isDark = isSystemInDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFE85C8A) // Rose clair (inversé)
            isMale && !isDark -> Color(0xFFCA3C66) // Rose principal (inversé)
            !isMale && isDark -> Color(0xFF6BC4C3) // Teal clair
            else -> Color(0xFF4AA3A2) // Teal
        }
    }

    /**
     * Retourne la couleur de fond selon le mode
     */
    @Composable
    fun background(): Color {
        return if (isSystemInDarkTheme()) DarkBackground else LightBackground
    }

    /**
     * Retourne la couleur de carte selon le mode
     */
    @Composable
    fun card(): Color {
        return if (isSystemInDarkTheme()) DarkCard else LightCard
    }

    /**
     * Retourne la couleur de texte selon le genre et le mode
     */
    @Composable
    fun text(isMale: Boolean): Color {
        val isDark = isSystemInDarkTheme()
        return when {
            isMale && isDark -> Color(0xFFF5B5C8) // Rose doux
            isMale && !isDark -> Color(0xFFCA3C66) // Rose principal
            !isMale && isDark -> Color(0xFFE0E0E0) // Texte clair
            else -> Color(0xFF4AA3A2) // Teal
        }
    }

    /**
     * Retourne la couleur de texte secondaire selon le mode
     */
    @Composable
    fun secondaryText(): Color {
        return if (isSystemInDarkTheme()) DarkSecondaryText else Color(0xFF6B7280)
    }
}

/**
 * Couleurs par catégorie de vêtement (comme iOS CategoryColors)
 */
object CategoryColors {
    @Composable
    fun colorForCategory(category: String): Color {
        val isDark = isSystemInDarkTheme()
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
