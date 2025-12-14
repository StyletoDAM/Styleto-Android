package tn.esprit.labasniandroid.models

/**
 * Résultat de la détection IA (avec valeurs originales pour originalDetection)
 */
data class DetectionResult(
    val type: String,        // Ex: "footwear", "tshirt", etc. (normalisé)
    val colorHex: String,    // Ex: "#466F4B"
    val colorName: String,   // Ex: "Dark Green"
    val style: String,       // Ex: "casual", "formal", etc. (normalisé)
    val season: String,      // Ex: "summer", "winter", etc. (normalisé)
    // ✅ NOUVEAU : Valeurs originales brutes du backend (pour originalDetection)
    val originalType: String? = null,
    val originalColor: String? = null,
    val originalStyle: String? = null,
    val originalSeason: String? = null
)

/**
 * Réponse de l'API /detect
 */
data class DetectionApiResponse(
    val success: Boolean,
    val image_url: String,
    val detection_result: String  // Texte brut à parser
)

