package tn.esprit.labasniandroid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Variante de thème (palette globale)
enum class ThemeVariant {
    PINK, BLUE
}

// Thème clair par défaut (PINKTheme)
private val PinkLightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    secondary = AquaSoft,
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = NeutralDark,
    surface = Color.White,
    onSurface = NeutralDark
)

// Thème sombre par défaut (PINKTheme)
private val PinkDarkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    secondary = AquaSoft,
    onSecondary = Color.White,
    tertiary = PinkGradientTop,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF2A2A2A)
)

// Variante claire BLEUTheme
// Couleurs dominantes: #4AA3A2 (TealAccent) et #A7E0E0 (AquaSoft)
// Couleurs moins dominantes en background: #E8AABE (PinkGradientTop) et #DB6A8F (PinkSecondary)
private val BlueLightColorScheme = lightColorScheme(
    primary = BluePrimary, // #4AA3A2 - DOMINANT
    onPrimary = Color.White,
    secondary = BlueSecondary, // #A7E0E0 - DOMINANT
    onSecondary = Color.Black,
    tertiary = PinkSecondary.copy(alpha = 0.15f), // #DB6A8F en background léger
    background = Color.White,
    onBackground = NeutralDark,
    surface = Color.White,
    onSurface = NeutralDark,
    surfaceVariant = PinkGradientTop.copy(alpha = 0.08f), // #E8AABE en background très léger
    onSurfaceVariant = NeutralDark,
    outline = BluePrimary.copy(alpha = 0.3f),
    outlineVariant = PinkGradientTop.copy(alpha = 0.2f)
)

// Variante sombre BLEUTheme
// Couleurs dominantes: #4AA3A2 (TealAccent) et #A7E0E0 (AquaSoft)
// Couleurs moins dominantes en background: #E8AABE (PinkGradientTop) et #DB6A8F (PinkSecondary)
private val BlueDarkColorScheme = darkColorScheme(
    primary = BluePrimary, // #4AA3A2 - DOMINANT
    onPrimary = Color.White,
    secondary = BlueSecondary, // #A7E0E0 - DOMINANT
    onSecondary = Color.White,
    tertiary = PinkSecondary.copy(alpha = 0.2f), // #DB6A8F en background léger
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF1F2933), // Fond sombre avec légère teinte rose via overlay si nécessaire
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = BluePrimary.copy(alpha = 0.4f),
    outlineVariant = PinkGradientTop.copy(alpha = 0.15f)
)

@Composable
fun LabasniTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    variant: ThemeVariant = ThemeVariant.PINK,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when (variant) {
        ThemeVariant.PINK -> if (darkTheme) PinkDarkColorScheme else PinkLightColorScheme
        ThemeVariant.BLUE -> if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LabasniTypography,
        content = content
    )
}

// Extensions pour accéder facilement aux couleurs du thème actif
// Utilisez ces extensions au lieu de PinkPrimary, TealAccent, etc. directement
object LabasniColors {
    @Composable
    fun primary(): Color = MaterialTheme.colorScheme.primary
    
    @Composable
    fun secondary(): Color = MaterialTheme.colorScheme.secondary
    
    @Composable
    fun tertiary(): Color = MaterialTheme.colorScheme.tertiary
    
    @Composable
    fun background(): Color = MaterialTheme.colorScheme.background
    
    @Composable
    fun surface(): Color = MaterialTheme.colorScheme.surface
    
    @Composable
    fun onPrimary(): Color = MaterialTheme.colorScheme.onPrimary
    
    @Composable
    fun onSecondary(): Color = MaterialTheme.colorScheme.onSecondary
}
