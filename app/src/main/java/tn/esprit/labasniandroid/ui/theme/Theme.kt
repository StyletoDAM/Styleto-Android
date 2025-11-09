package tn.esprit.labasniandroid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = AquaSoft,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    background = androidx.compose.ui.graphics.Color.White,
    onBackground = NeutralDark,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = NeutralDark
)

private val DarkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = AquaSoft,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    tertiary = PinkGradientTop,
    background = Color(0xFF121212), // Fond plus élégant et moderne
    onBackground = Color(0xFFE0E0E0), // Texte clair et lisible
    surface = Color(0xFF1E1E1E), // Surface légèrement plus claire que le fond
    onSurface = Color(0xFFE0E0E0), // Texte sur surface
    surfaceVariant = Color(0xFF2C2C2C), // Variante de surface pour les cards
    onSurfaceVariant = Color(0xFFB0B0B0), // Texte secondaire
    outline = Color(0xFF3A3A3A), // Bordures subtiles
    outlineVariant = Color(0xFF2A2A2A) // Variante de bordure
)

@Composable
fun LabasniTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LabasniTypography,
        content = content
    )
}
