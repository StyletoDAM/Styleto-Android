package tn.esprit.labasniandroid.ui.screen.store.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import tn.esprit.labasniandroid.models.entities.Message
import tn.esprit.labasniandroid.models.entities.StoreItem
import tn.esprit.labasniandroid.ui.theme.DynamicThemeColors
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.ui.theme.ThemeVariant

/**
 * Fonction utilitaire pour normaliser et comparer les IDs
 * Gère les différents formats possibles (MongoDB ObjectId, UUID, etc.)
 */
private fun normalizeId(id: String?): String {
    if (id.isNullOrBlank()) return ""
    return id.trim().lowercase()
}

@Composable
fun ChatDetailView(
    token: String,
    userId: String,
    ownerId: String,
    ownerName: String?,
    ownerAvatar: String?,
    storeItem: StoreItem? = null,
    modifier: Modifier = Modifier,
    viewModel: ChatDetailViewModel = viewModel(),
    messagingViewModel: MessagingViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToConversation: (String, String?, String?) -> Unit = { _, _, _ -> }
) {
    val isMale = ThemeController.themeVariant.collectAsState().value == ThemeVariant.BLUE
    val themePrimary = DynamicThemeColors.primary(isMale)
    val themeBackground = DynamicThemeColors.background()
    val themeCard = DynamicThemeColors.card()
    val themeText = DynamicThemeColors.text(isMale)

    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    
    // Conversations pour le menu dropdown
    val conversations by messagingViewModel.conversations.collectAsState()
    var showConversationsMenu by remember { mutableStateOf(false) }

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    LaunchedEffect(token, userId) {
        messagingViewModel.initialize(token, userId)
    }
    
    LaunchedEffect(ownerId) {
        if (ownerId.isNotBlank()) {
            // Log détaillé pour vérifier le userId utilisé
            viewModel.initializeChat(token, userId, ownerId)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            // 🧍‍♂️ Header compact moderne (WhatsApp + iMessage style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flèche retour petite et élégante
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF4AA3A2), // Teal
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Avatar rond 48dp premium
                Box {
                    if (ownerAvatar != null && ownerAvatar.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(ownerAvatar)
                                .crossfade(true)
                                .build(),
                            contentDescription = ownerName,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = CircleShape,
                                    ambientColor = Color.Black.copy(alpha = 0.1f)
                                ),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = CircleShape,
                                    ambientColor = Color.Black.copy(alpha = 0.1f)
                                )
                                .background(Color(0xFFA7E0E0).copy(alpha = 0.8f)), // Teal pastel
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (ownerName?.firstOrNull()?.uppercaseChar() ?: "U").toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                    
                    // Badge "En ligne" vert doux
                    if (isConnected) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00D97E)) // Vert doux
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                // Nom et statut compacts
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                Text(
                    text = ownerName ?: "Utilisateur",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp, // HeaderStyle.titleSize exact
                        color = Color(0xFF4AA3A2) // HeaderStyle.titleColor exact
                    )
                )
                    if (isConnected) {
                        Text(
                            text = "En ligne",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = Color(0xFF00D97E), // HeaderStyle.onlineColor exact
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                
                // Bouton menu 3 points sobre
                IconButton(
                    onClick = { showConversationsMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Menu",
                        tint = Color(0xFF4AA3A2), // Teal
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Menu dropdown simplifié
                if (conversations.isNotEmpty()) {
                    DropdownMenu(
                        expanded = showConversationsMenu,
                        onDismissRequest = { showConversationsMenu = false },
                        modifier = Modifier
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .fillMaxWidth(0.7f)
                    ) {
                        conversations.forEach { conversation ->
                            val otherParticipant = conversation.participants.firstOrNull { it != userId }
                            val otherName = otherParticipant?.let { conversation.participantNames[it] } ?: "Utilisateur"
                            val otherAvatar = otherParticipant?.let { conversation.participantAvatars[it] }
                            
                            DropdownMenuItem(
                                onClick = {
                                    showConversationsMenu = false
                                    if (otherParticipant != null && otherParticipant != ownerId) {
                                        onNavigateToConversation(otherParticipant, otherName, otherAvatar)
                                    }
                                },
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Avatar mini
                                        if (otherAvatar != null) {
                                            AsyncImage(
                                                model = otherAvatar,
                                                contentDescription = otherName,
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFA7E0E0).copy(alpha = 0.6f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = otherName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                )
                                            }
                                        }
                                        Text(
                                            text = otherName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF4AA3A2)
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // 🎨 Fond coloré plus visible et gras
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE8AABE).copy(alpha = 0.20f), // Rose doux plus gras et visible
                            Color.White.copy(alpha = 0.95f),       // Blanc avec légère teinte
                            Color(0xFFA7E0E0).copy(alpha = 0.15f)  // Aqua doux plus visible
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
            // 💬 Messages avec ChatSpacing exact
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // ChatSpacing.horizontal
                    .padding(top = 4.dp) // Commence juste après header
                    .padding(bottom = 80.dp), // Espace pour footer compact
                verticalArrangement = Arrangement.spacedBy(8.dp) // ChatSpacing.betweenMessages
            ) {
                items(messages, key = { it.id }) { message ->
                    // Normaliser les IDs pour la comparaison
                    val normalizedSenderId = normalizeId(message.senderId)
                    val normalizedUserId = normalizeId(userId)
                    val isOwnMessage = normalizedSenderId == normalizedUserId && normalizedSenderId.isNotBlank() && normalizedUserId.isNotBlank()
                    
                    MessageBubble(
                        message = message,
                        isOwnMessage = isOwnMessage,
                        themePrimary = themePrimary,
                        themeCard = themeCard,
                        themeText = themeText
                    )
                }
            }

            // 📝 Footer compact 68dp max (WhatsApp + iMessage style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp) // Hauteur fixe compacte
                    .background(Color.Transparent)
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 📎 AttachButtonStyle exact
                IconButton(
                    onClick = { /* TODO: Attach file */ },
                    modifier = Modifier
                        .size(40.dp) // AttachButtonStyle.size
                        .background(
                            Color(0x1A4AA3A2), // aqua clair transparent exact
                            shape = CircleShape // cornerRadius 50 = CircleShape
                        )
                        .shadow(
                            elevation = 18.dp, // SoftShadow
                            shape = CircleShape,
                            ambientColor = Color(0x33000000) // SoftShadow color
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.AttachFile,
                        contentDescription = "Attach",
                        tint = Color(0xFF4AA3A2), // AttachButtonStyle.iconTint exact
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // ChatInputField - Style exact
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp) // Hauteur fixe
                        .shadow(
                            elevation = 18.dp, // SoftShadow
                            shape = RoundedCornerShape(28.dp), // ChatInputField.cornerRadius
                            ambientColor = Color(0x33000000) // SoftShadow color
                        ),
                    placeholder = { 
                        Text(
                            "Écrivez un message…",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF6B7280), // ChatInputField.placeholderColor exact
                                fontSize = 15.sp
                            )
                        ) 
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White, // ChatInputField.background
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black, // ChatInputField.textColor
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFF4AA3A2)
                    ),
                    shape = RoundedCornerShape(28.dp), // ChatInputField.cornerRadius
                    maxLines = 1, // Une seule ligne pour compacité
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                // SendButtonStyle exact
                val isSending = false // TODO: Get from viewModel
                val isEnabled = messageText.isNotBlank() && !isSending
                
                Box(
                    modifier = Modifier
                        .size(48.dp) // SendButtonStyle.size exact
                        .shadow(
                            elevation = 18.dp, // SoftShadow
                            shape = CircleShape, // cornerRadius 50 = CircleShape
                            ambientColor = Color(0x33000000) // SoftShadow color
                        )
                        .background(
                            color = if (isEnabled) Color(0xFF4AA3A2) else Color.Gray.copy(alpha = 0.5f), // SendButtonStyle.background
                            shape = CircleShape
                        )
                        .clickable(enabled = isEnabled) {
                            if (isEnabled) {
                                val textToSend = messageText
                                messageText = ""
                                viewModel.sendMessage(token, userId, textToSend)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSending) Icons.Filled.Schedule else Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White, // SendButtonStyle.iconTint exact
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isOwnMessage: Boolean,
    themePrimary: Color,
    themeCard: Color,
    themeText: Color
) {
    val context = LocalContext.current
    val time = message.createdAt?.let {
        try {
            val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            format.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = format.parse(it)
            if (date != null) {
                val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                timeFormat.format(date)
            } else {
                it.substringOrNull(11, 16) ?: ""
            }
        } catch (e: Exception) {
            ""
        }
    } ?: ""

    val ctx = LocalContext.current
    
    if (isOwnMessage) {
        OutgoingMessage(
            text = message.content,
            time = time,
            themeCard = themeCard,
            themeText = themeText,
            extractedInfo = message.extractedInfo
        )
    } else {
        IncomingMessage(
            text = message.content,
            time = time,
            avatarLetter = (message.senderName?.firstOrNull()?.uppercaseChar() ?: "U").toString(),
            profilePictureURL = message.senderAvatar,
            themePrimary = themePrimary,
            extractedInfo = message.extractedInfo
        )
    }
}

private fun String.substringOrNull(startIndex: Int, endIndex: Int): String? {
    return if (startIndex >= 0 && endIndex <= this.length && startIndex < endIndex) {
        this.substring(startIndex, endIndex)
    } else null
}
