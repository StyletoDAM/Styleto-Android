package tn.esprit.labasniandroid.utils

import android.graphics.Color

/**
 * Convertit un code hexadécimal de couleur en nom de couleur lisible
 * Ex: #466F4B → "Dark Green"
 */
object ColorNameConverter {
    
    /**
     * Convertit un hex en nom de couleur
     * @param hexCode Code hexadécimal (ex: "#466F4B" ou "466F4B")
     * @return Nom de la couleur (ex: "Dark Green")
     */
    fun hexToColorName(hexCode: String): String {
        val hex = hexCode.trim().removePrefix("#").uppercase()
        if (hex.length != 6) return "Unknown"
        
        try {
            val color = Color.parseColor("#$hex")
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            
            return findClosestColorName(r, g, b)
        } catch (e: Exception) {
            return "Unknown"
        }
    }
    
    /**
     * Trouve le nom de couleur le plus proche basé sur RGB
     */
    private fun findClosestColorName(r: Int, g: Int, b: Int): String {
        // Calculer la luminosité
        val brightness = (r * 299 + g * 587 + b * 114) / 1000
        
        // Couleurs de base avec leurs valeurs RGB
        val colors = mapOf(
            "Black" to intArrayOf(0, 0, 0),
            "White" to intArrayOf(255, 255, 255),
            "Red" to intArrayOf(255, 0, 0),
            "Green" to intArrayOf(0, 128, 0),
            "Blue" to intArrayOf(0, 0, 255),
            "Yellow" to intArrayOf(255, 255, 0),
            "Orange" to intArrayOf(255, 165, 0),
            "Purple" to intArrayOf(128, 0, 128),
            "Pink" to intArrayOf(255, 192, 203),
            "Brown" to intArrayOf(165, 42, 42),
            "Gray" to intArrayOf(128, 128, 128),
            "Navy" to intArrayOf(0, 0, 128),
            "Teal" to intArrayOf(0, 128, 128),
            "Maroon" to intArrayOf(128, 0, 0),
            "Olive" to intArrayOf(128, 128, 0),
            "Lime" to intArrayOf(0, 255, 0),
            "Aqua" to intArrayOf(0, 255, 255),
            "Silver" to intArrayOf(192, 192, 192),
            "Gold" to intArrayOf(255, 215, 0),
            "Beige" to intArrayOf(245, 245, 220),
            "Coral" to intArrayOf(255, 127, 80),
            "Salmon" to intArrayOf(250, 128, 114),
            "Turquoise" to intArrayOf(64, 224, 208),
            "Lavender" to intArrayOf(230, 230, 250),
            "Ivory" to intArrayOf(255, 255, 240),
            "Khaki" to intArrayOf(240, 230, 140),
            "Magenta" to intArrayOf(255, 0, 255),
            "Cyan" to intArrayOf(0, 255, 255),
            "Indigo" to intArrayOf(75, 0, 130),
            "Violet" to intArrayOf(238, 130, 238)
        )
        
        // Trouver la couleur la plus proche (distance euclidienne)
        var minDistance = Double.MAX_VALUE
        var closestColor = "Unknown"
        
        for ((name, rgb) in colors) {
            val distance = Math.sqrt(
                Math.pow((r - rgb[0]).toDouble(), 2.0) +
                Math.pow((g - rgb[1]).toDouble(), 2.0) +
                Math.pow((b - rgb[2]).toDouble(), 2.0)
            )
            
            if (distance < minDistance) {
                minDistance = distance
                closestColor = name
            }
        }
        
        // Ajouter des modificateurs selon la luminosité
        return when {
            brightness < 50 -> "Dark $closestColor"
            brightness > 200 -> "Light $closestColor"
            else -> closestColor
        }
    }
    
    /**
     * Parse une ligne de résultat de détection pour extraire le hex
     * Ex: "Couleur dominante : #466F4B" → "#466F4B"
     */
    fun extractHexFromText(text: String): String? {
        val pattern = Regex("#?[A-Fa-f0-9]{6}")
        return pattern.find(text)?.value?.let {
            if (it.startsWith("#")) it else "#$it"
        }
    }
}

