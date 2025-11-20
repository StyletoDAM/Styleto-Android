package tn.esprit.labasniandroid.models

/**
 * Résultat de la détection IA
 */
data class DetectionResult(
    val type: String,        // Ex: "footwear", "tshirt", etc.
    val colorHex: String,    // Ex: "#466F4B"
    val colorName: String,   // Ex: "Dark Green"
    val style: String,       // Ex: "casual", "formal", etc.
    val season: String       // Ex: "summer", "winter", etc.
)

/**
 * Réponse de l'API /detect
 */
data class DetectionApiResponse(
    val success: Boolean,
    val image_url: String,
    val detection_result: String  // Texte brut à parser
)

