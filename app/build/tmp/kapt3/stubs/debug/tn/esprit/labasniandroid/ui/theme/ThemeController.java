package tn.esprit.labasniandroid.ui.theme;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017J\u000e\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\tJ\u000e\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u000bJ\u0016\u0010\u001c\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\u001eJ\u000e\u0010\u001f\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\t0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000b0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011\u00a8\u0006 "}, d2 = {"Ltn/esprit/labasniandroid/ui/theme/ThemeController;", "", "()V", "KEY_THEME_MODE", "", "KEY_THEME_VARIANT", "PREFS_NAME", "_themeMode", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Ltn/esprit/labasniandroid/ui/theme/ThemeMode;", "_themeVariant", "Ltn/esprit/labasniandroid/ui/theme/ThemeVariant;", "preferences", "Landroid/content/SharedPreferences;", "themeMode", "Lkotlinx/coroutines/flow/StateFlow;", "getThemeMode", "()Lkotlinx/coroutines/flow/StateFlow;", "themeVariant", "getThemeVariant", "initialize", "", "context", "Landroid/content/Context;", "setThemeMode", "mode", "setThemeVariant", "variant", "syncThemeVariantWithGender", "gender", "Ltn/esprit/labasniandroid/models/entities/User$Gender;", "syncThemeVariantWithSavedGender", "app_debug"})
public final class ThemeController {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "labasni_theme_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_THEME_MODE = "theme_mode";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_THEME_VARIANT = "theme_variant";
    private static android.content.SharedPreferences preferences;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<tn.esprit.labasniandroid.ui.theme.ThemeMode> _themeMode = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<tn.esprit.labasniandroid.ui.theme.ThemeMode> themeMode = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<tn.esprit.labasniandroid.ui.theme.ThemeVariant> _themeVariant = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<tn.esprit.labasniandroid.ui.theme.ThemeVariant> themeVariant = null;
    @org.jetbrains.annotations.NotNull()
    public static final tn.esprit.labasniandroid.ui.theme.ThemeController INSTANCE = null;
    
    private ThemeController() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<tn.esprit.labasniandroid.ui.theme.ThemeMode> getThemeMode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<tn.esprit.labasniandroid.ui.theme.ThemeVariant> getThemeVariant() {
        return null;
    }
    
    public final void initialize(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void setThemeMode(@org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.ui.theme.ThemeMode mode) {
    }
    
    public final void setThemeVariant(@org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.ui.theme.ThemeVariant variant) {
    }
    
    /**
     * Synchronise automatiquement le themeVariant avec le genre de l'utilisateur
     * (comme iOS: Male = BLUE, Female = PINK)
     * Cette fonction doit être appelée lors du chargement du profil utilisateur
     * et lors de la mise à jour du genre dans les settings
     */
    public final void syncThemeVariantWithGender(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    tn.esprit.labasniandroid.models.entities.User.Gender gender) {
    }
    
    /**
     * Synchronise automatiquement le themeVariant avec le genre depuis TokenManager
     * (utilisé lors de l'initialisation si l'utilisateur est déjà connecté)
     */
    public final void syncThemeVariantWithSavedGender(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}