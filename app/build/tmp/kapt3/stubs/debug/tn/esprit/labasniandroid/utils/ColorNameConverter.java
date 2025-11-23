package tn.esprit.labasniandroid.utils;

/**
 * Convertit un code hexadécimal de couleur en nom de couleur lisible
 * Ex: #466F4B → "Dark Green"
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0004J \u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\bH\u0002J\u000e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u0004\u00a8\u0006\r"}, d2 = {"Ltn/esprit/labasniandroid/utils/ColorNameConverter;", "", "()V", "extractHexFromText", "", "text", "findClosestColorName", "r", "", "g", "b", "hexToColorName", "hexCode", "app_debug"})
public final class ColorNameConverter {
    @org.jetbrains.annotations.NotNull()
    public static final tn.esprit.labasniandroid.utils.ColorNameConverter INSTANCE = null;
    
    private ColorNameConverter() {
        super();
    }
    
    /**
     * Convertit un hex en nom de couleur
     * @param hexCode Code hexadécimal (ex: "#466F4B" ou "466F4B")
     * @return Nom de la couleur (ex: "Dark Green")
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String hexToColorName(@org.jetbrains.annotations.NotNull()
    java.lang.String hexCode) {
        return null;
    }
    
    /**
     * Trouve le nom de couleur le plus proche basé sur RGB
     */
    private final java.lang.String findClosestColorName(int r, int g, int b) {
        return null;
    }
    
    /**
     * Parse une ligne de résultat de détection pour extraire le hex
     * Ex: "Couleur dominante : #466F4B" → "#466F4B"
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String extractHexFromText(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
        return null;
    }
}