package tn.esprit.labasniandroid.utils

import tn.esprit.labasniandroid.models.DetectionResult

/**
 * Parse le résultat brut de détection IA en objet structuré
 * Ex: "Type du vêtement : footwear\nCouleur dominante : #466F4B\nStyle : casual\nSaison : summer"
 */
object DetectionResultParser {
    
    fun parse(rawText: String): DetectionResult {
        // Protection contre les valeurs nulles ou vides
        if (rawText.isBlank()) {
            return DetectionResult(
                type = "Other",
                colorHex = "#808080",
                colorName = "Unknown",
                style = "casual",
                season = "all"
            )
        }
        
        val lines = rawText.split("\n")
        
        var type = "Other"
        var colorHex = "#808080" // Gris par défaut
        var style = "casual"
        var season = "all"
        
        for (line in lines) {
            try {
                val trimmed = line.trim()
                // Ignorer les lignes vides et les lignes de séparation
                if (trimmed.isEmpty() || trimmed.all { it == '-' || it == '=' }) continue
                // Ignorer les lignes comme "Résultat final"
                if (trimmed.lowercase().contains("résultat") && !trimmed.contains(":")) continue
                
                val colonIndex = trimmed.indexOf(':')
                if (colonIndex == -1 || colonIndex == 0 || colonIndex >= trimmed.length - 1) continue
                
                val key = trimmed.substring(0, colonIndex).lowercase()
                val value = trimmed.substring(colonIndex + 1).trim()
                
                if (value.isBlank()) continue
                
                when {
                    key.contains("type") || key.contains("vêtement") || key.contains("clothing") -> {
                        type = normalizeCategory(value)
                    }
                    key.contains("color") || key.contains("couleur") -> {
                        val hex = ColorNameConverter.extractHexFromText(value)
                        if (hex != null) {
                            colorHex = hex
                        }
                    }
                    key.contains("style") -> {
                        style = normalizeStyle(value)
                    }
                    key.contains("season") || key.contains("saison") -> {
                        season = normalizeSeason(value)
                    }
                }
            } catch (e: Exception) {
                // Ignorer les lignes qui causent des erreurs et continuer
                e.printStackTrace()
                continue
            }
        }
        
        // Protection pour la conversion de couleur
        val colorName = try {
            ColorNameConverter.hexToColorName(colorHex)
        } catch (e: Exception) {
            "Unknown"
        }
        
        return DetectionResult(
            type = type,
            colorHex = colorHex,
            colorName = colorName,
            style = style,
            season = season
        )
    }
    
    private fun normalizeCategory(value: String): String {
        val v = value.lowercase()
        return when {
            v.contains("footwear") || v.contains("shoe") || v.contains("chaussure") -> "Shoes"
            v.contains("tshirt") || v.contains("shirt") || v.contains("haut") || v.contains("top") -> "Tshirt"
            v.contains("pant") || v.contains("jean") || v.contains("pantalon") -> "Pants"
            v.contains("dress") || v.contains("robe") -> "Dress"
            v.contains("jacket") || v.contains("veste") -> "Jacket"
            v.contains("accessory") || v.contains("accessoire") -> "Accessory"
            else -> "Other"
        }
    }
    
    private fun normalizeStyle(value: String): String {
        val v = value.lowercase()
        return when {
            v.contains("casual") -> "casual"
            v.contains("formal") || v.contains("elegant") || v.contains("chic") -> "formal"
            v.contains("sport") -> "sport"
            v.contains("vintage") -> "vintage"
            v.contains("modern") -> "modern"
            v.contains("boho") || v.contains("bohemian") -> "bohemian"
            else -> "casual"
        }
    }
    
    private fun normalizeSeason(value: String): String {
        val v = value.lowercase()
        return when {
            v.contains("summer") || v.contains("été") -> "summer"
            v.contains("winter") || v.contains("hiver") -> "winter"
            v.contains("fall") || v.contains("autumn") || v.contains("automne") -> "fall"
            v.contains("spring") || v.contains("printemps") -> "spring"
            else -> "all"
        }
    }
}

