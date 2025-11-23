package tn.esprit.labasniandroid.ui.theme;

/**
 * Système de couleurs dynamique basé sur le genre (comme iOS)
 * S'adapte automatiquement selon le genre (male/female) et le mode (light/dark)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001d\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\t\u001a\u00020\u0004H\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0015\u0010\f\u001a\u00020\u0004H\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\r\u0010\u000bJ\b\u0010\u000e\u001a\u00020\u0006H\u0003J\u001d\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0010\u0010\bJ\u001d\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0012\u0010\bJ\u0015\u0010\u0013\u001a\u00020\u0004H\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0014\u0010\u000bJ\u001d\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0016\u0010\bJ\u001d\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0018\u0010\bJ\u001d\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001a\u0010\b\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u001b"}, d2 = {"Ltn/esprit/labasniandroid/ui/theme/DynamicThemeColors;", "", "()V", "aqua", "Landroidx/compose/ui/graphics/Color;", "isMale", "", "aqua-vNxB06k", "(Z)J", "background", "background-0d7_KjU", "()J", "card", "card-0d7_KjU", "isDarkTheme", "primary", "primary-vNxB06k", "secondary", "secondary-vNxB06k", "secondaryText", "secondaryText-0d7_KjU", "softPink", "softPink-vNxB06k", "teal", "teal-vNxB06k", "text", "text-vNxB06k", "app_debug"})
public final class DynamicThemeColors {
    @org.jetbrains.annotations.NotNull()
    public static final tn.esprit.labasniandroid.ui.theme.DynamicThemeColors INSTANCE = null;
    
    private DynamicThemeColors() {
        super();
    }
    
    /**
     * Détermine si le thème sombre est actif (comme iOS ThemeManager.updateTheme)
     */
    @androidx.compose.runtime.Composable()
    private final boolean isDarkTheme() {
        return false;
    }
}