package tn.esprit.labasniandroid.ui.screen.store.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import tn.esprit.labasniandroid.models.entities.Conversation
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessagingView(
    token: String,
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: MessagingViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String, String?, String?) -> Unit = { _, _, _ -> }
) {
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeBackground = DynamicThemeColors.background()
    val themeCard = DynamicThemeColors.card()
    val themeText = DynamicThemeColors.text(isMale)
    val themeAqua = DynamicThemeColors.aqua(isMale)
    val themeSecondary = DynamicThemeColors.secondary(isMale)

    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(token, userId) {
        if (token.isNotBlank() && userId.isNotBlank()) {
            viewModel.initialize(token, userId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = themePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Messages",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = themePrimary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeBackground)
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = themePrimary)
                    }
                }
                conversations.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "💬",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Messagerie",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = themePrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucun message",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = themeText.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
                else -> {
                    // Search Bar (comme iOS)
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Search Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .height(55.dp)
                                .background(
                                    color = themeAqua.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Search conversations...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray.copy(alpha = 0.8f)
                                )
                            )
                        }

                        // Conversations List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 16.dp,
                                vertical = 14.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(conversations.size) { index ->
                                val conversation = conversations[index]
                                // Trouver l'autre participant avec normalisation
                                val otherParticipant: String? = conversation.participants.firstOrNull { participantId: String -> 
                                    participantId.trim().lowercase() != userId.trim().lowercase()
                                }
                                
                                // Récupérer le nom et l'avatar avec normalisation
                                val otherName: String? = if (otherParticipant != null) {
                                    val normalizedOtherId = otherParticipant.trim().lowercase()
                                    conversation.participantNames.entries.firstOrNull { 
                                        it.key.trim().lowercase() == normalizedOtherId 
                                    }?.value
                                } else null
                                
                                val otherAvatar: String? = if (otherParticipant != null) {
                                    val normalizedOtherId = otherParticipant.trim().lowercase()
                                    conversation.participantAvatars.entries.firstOrNull { 
                                        it.key.trim().lowercase() == normalizedOtherId 
                                    }?.value
                                } else null
                                
                                android.util.Log.d("MessagingView", "Conversation ${conversation.id}:")
                                android.util.Log.d("MessagingView", "  Participants: ${conversation.participants}")
                                android.util.Log.d("MessagingView", "  UserId: $userId")
                                android.util.Log.d("MessagingView", "  Other participant: $otherParticipant")
                                android.util.Log.d("MessagingView", "  Other name: $otherName")
                                android.util.Log.d("MessagingView", "  Participant names map: ${conversation.participantNames}")
                                
                                ChatRow(
                                    conversation = conversation,
                                    userId = userId,
                                    themePrimary = themePrimary,
                                    themeCard = themeCard,
                                    themeText = themeText,
                                    otherName = otherName ?: "Utilisateur",
                                    otherAvatar = otherAvatar,
                                    onClick = {
                                        android.util.Log.d("MessagingView", "=== CLICKED ON CONVERSATION ===")
                                        android.util.Log.d("MessagingView", "Conversation ID: ${conversation.id}")
                                        android.util.Log.d("MessagingView", "Current userId: $userId")
                                        android.util.Log.d("MessagingView", "All participants: ${conversation.participants}")
                                        android.util.Log.d("MessagingView", "Other participant ID: $otherParticipant")
                                        android.util.Log.d("MessagingView", "Other participant name: $otherName")
                                        android.util.Log.d("MessagingView", "Other participant avatar: $otherAvatar")
                                        android.util.Log.d("MessagingView", "Participant names map: ${conversation.participantNames}")
                                        
                                        if (otherParticipant != null) {
                                            android.util.Log.d("MessagingView", "✅ Calling onNavigateToChat with: $otherParticipant, name: $otherName")
                                            onNavigateToChat(otherParticipant, otherName, otherAvatar)
                                        } else {
                                            android.util.Log.e("MessagingView", "❌ ERROR: otherParticipant is null! Cannot navigate.")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatRow(
    conversation: Conversation,
    userId: String,
    themePrimary: Color,
    themeCard: Color,
    themeText: Color,
    otherName: String,
    otherAvatar: String?,
    onClick: () -> Unit
) {
    
    val lastMessage = conversation.lastMessage
    val lastMessageText = lastMessage?.content?.take(50) ?: "Aucun message"
    val lastMessageTime = lastMessage?.createdAt?.let { formatTime(it) } ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(themeCard, RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box {
            if (otherAvatar != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(otherAvatar)
                        .crossfade(true)
                        .build(),
                    contentDescription = otherName,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = otherName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
            // Badge vert pour "en ligne" (optionnel, toujours false pour l'instant)
        }

        // Name and Message
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = otherName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = themePrimary
                )
            )
            Text(
                text = lastMessageText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = Color.Gray
                ),
                maxLines = 1
            )
        }

        // Time and Badge
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (lastMessageTime.isNotBlank()) {
                Text(
                    text = lastMessageTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                )
            }
            // Badge pour messages non lus (optionnel)
        }
    }
}

private fun formatTime(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return ""
        val now = Date()
        val diff = now.time - date.time
        val minutes = diff / (1000 * 60)
        val hours = diff / (1000 * 60 * 60)
        val days = diff / (1000 * 60 * 60 * 24)

        when {
            minutes < 1 -> "Maintenant"
            minutes < 60 -> "${minutes.toInt()}m"
            hours < 24 -> "${hours.toInt()}h"
            days < 7 -> "${days.toInt()}j"
            else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
        }
    } catch (e: Exception) {
        ""
    }
}
