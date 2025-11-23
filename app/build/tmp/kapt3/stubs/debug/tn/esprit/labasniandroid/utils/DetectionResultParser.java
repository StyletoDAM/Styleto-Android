package tn.esprit.labasniandroid.utils;

/**
 * Parse le résultat brut de détection IA en objet structuré
 * Ex: "Type du vêtement : footwear\nCouleur dominante : #466F4B\nStyle : casual\nSaison : summer"
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002J\u0010\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004\u00a8\u0006\u000b"}, d2 = {"Ltn/esprit/labasniandroid/utils/DetectionResultParser;", "", "()V", "normalizeCategory", "", "value", "normalizeSeason", "normalizeStyle", "parse", "Ltn/esprit/labasniandroid/models/DetectionResult;", "rawText", "app_debug"})
public final class DetectionResultParser {
    @org.jetbrains.annotations.NotNull()
    public static final tn.esprit.labasniandroid.utils.DetectionResultParser INSTANCE = null;
    
    private DetectionResultParser() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final tn.esprit.labasniandroid.models.DetectionResult parse(@org.jetbrains.annotations.NotNull()
    java.lang.String rawText) {
        return null;
    }
    
    private final java.lang.String normalizeCategory(java.lang.String value) {
        return null;
    }
    
    private final java.lang.String normalizeStyle(java.lang.String value) {
        return null;
    }
    
    private final java.lang.String normalizeSeason(java.lang.String value) {
        return null;
    }
}