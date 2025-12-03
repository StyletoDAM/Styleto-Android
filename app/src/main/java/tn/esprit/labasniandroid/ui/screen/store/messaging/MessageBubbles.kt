package tn.esprit.labasniandroid.ui.screen.store.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import tn.esprit.labasniandroid.models.entities.ExtractedInfo

/**
 * 💬 Message reçu PREMIUM (rose premium)
 * Forme arrondie 22-26dp, couleur rose premium, texte blanc pur, ombre douce iOS
 */
@Composable
fun IncomingMessage(
    text: String,
    time: String,
    avatarLetter: String,
    profilePictureURL: String?,
    themePrimary: Color,
    extractedInfo: ExtractedInfo? = null,
    context: android.content.Context? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar premium avec ombre
        if (profilePictureURL != null && profilePictureURL.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureURL)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .shadow(
                        elevation = 3.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    ),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .shadow(
                        elevation = 3.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .background(Color(0xFFA7E0E0).copy(alpha = 0.8f)), // Teal pastel
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarLetter,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.width(10.dp))
        
        // Bulle premium rose
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 🎨 BubbleIncoming - Style exact du code fourni
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 18.dp,  // SoftShadow blur
                        shape = RoundedCornerShape(26.dp), // cornerRadius exact
                        ambientColor = Color(0x33000000) // SoftShadow color
                    )
                    .background(
                        color = Color(0xFFCA3C66), // rose primaire exact
                        shape = RoundedCornerShape(26.dp) // cornerRadius exact
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = text, // ✨ Le texte est déjà masqué par le backend
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White, // Texte blanc pur
                        lineHeight = 20.sp
                    )
                )
            }
            
            // Temps discret
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * 💬 Message envoyé PREMIUM (teal premium)
 * Forme arrondie 22-26dp, couleur teal premium, texte teal foncé, ombre légère
 */
@Composable
fun OutgoingMessage(
    text: String,
    time: String,
    themeCard: Color,
    themeText: Color,
    extractedInfo: ExtractedInfo? = null // ✨ Conservé pour compatibilité mais non utilisé (masquage côté backend)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        // Bulle premium teal
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 🎨 BubbleOutgoing - Style exact du code fourni
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 18.dp,  // SoftShadow blur
                        shape = RoundedCornerShape(26.dp), // cornerRadius exact
                        ambientColor = Color(0x33000000) // SoftShadow color
                    )
                    .background(
                        color = Color(0xFF4AA3A2), // teal/aqua exact
                        shape = RoundedCornerShape(26.dp) // cornerRadius exact
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = text, // ✨ Le texte est déjà masqué par le backend
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF043F3D), // teal foncé pour lisibilité (style exact)
                        lineHeight = 20.sp
                    )
                )
            }
            
            // Temps discret
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            )
        }
    }
}
