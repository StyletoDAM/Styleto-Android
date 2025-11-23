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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Message entrant (comme iOS IncomingMessage ligne 209-249)
 * Avatar 36x36 à gauche, bulle themePrimary avec cornerRadius 20dp sauf topLeft 4dp, texte blanc
 */
@Composable
fun IncomingMessage(
    text: String,
    time: String,
    avatarLetter: String,
    profilePictureURL: String?,
    themePrimary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar (36x36 comme iOS ligne 225-226)
        if (profilePictureURL != null && profilePictureURL.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePictureURL)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f)),
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
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Bulle et temps (comme iOS ligne 234-245)
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Bulle avec cornerRadius spécial (20dp partout sauf topLeft 4dp comme iOS ligne 238-240)
            Box(
                modifier = Modifier
                    .background(
                        color = themePrimary,
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 20.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
            }
            
            // Temps (caption2 comme iOS ligne 242-244)
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = Color.Gray.copy(alpha = 0.8f)
                )
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * Message sortant (comme iOS OutgoingMessage ligne 251-272)
 * Aligné à droite, bulle avec themeCard, texte primary, cornerRadius 20dp sauf topRight 4dp
 */
@Composable
fun OutgoingMessage(
    text: String,
    time: String,
    themeCard: Color,
    themeText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        // Bulle et temps (comme iOS ligne 258-269)
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Bulle avec cornerRadius spécial (20dp partout sauf topRight 4dp comme iOS ligne 262-264)
            Box(
                modifier = Modifier
                    .background(
                        color = themeCard,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 4.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        color = themeText
                    )
                )
            }
            
            // Temps (caption2 comme iOS ligne 266-268)
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = Color.Gray.copy(alpha = 0.8f)
                )
            )
        }
    }
}

