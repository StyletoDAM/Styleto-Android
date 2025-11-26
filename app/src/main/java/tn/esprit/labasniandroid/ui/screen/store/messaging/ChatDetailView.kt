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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeBackground)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
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

                // Header avec avatar et nom (comme iOS toolbar ligne 150-186)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Avatar avec badge en ligne (comme iOS ligne 152-175)
                    Box {
                        if (ownerAvatar != null && ownerAvatar.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(ownerAvatar)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = ownerName,
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
                                    text = (ownerName?.firstOrNull()?.uppercaseChar() ?: "U").toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                        // Badge vert pour "en ligne" (comme iOS ligne 171-174)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isConnected) Color.Green else Color.Gray)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(themeBackground)
                                    .padding(2.dp)
                            )
                        }
                    }

                    // Nom et statut (comme iOS ligne 177-184)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = ownerName ?: "Utilisateur",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                color = themePrimary
                            )
                        )
                        Text(
                            text = if (isConnected) "En ligne" else "Hors ligne",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = if (isConnected) Color.Green else Color.Gray
                            )
                        )
                    }
                    
                            // Menu dropdown pour accéder aux autres conversations
                            if (conversations.isNotEmpty()) {
                                Box {
                                    IconButton(
                                        onClick = { showConversationsMenu = true },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowDropDown,
                                            contentDescription = "Conversations",
                                            tint = themePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = showConversationsMenu,
                                        onDismissRequest = { showConversationsMenu = false },
                                        modifier = Modifier
                                            .background(themeCard)
                                            .fillMaxWidth(0.7f)
                                    ) {
                                        conversations.forEach { conversation ->
                                            val otherParticipant: String? = conversation.participants.firstOrNull { participantId: String -> participantId != userId }
                                            val otherName: String = otherParticipant?.let { id: String -> conversation.participantNames[id] } ?: "Utilisateur"
                                            val otherAvatar: String? = otherParticipant?.let { id: String -> conversation.participantAvatars[id] }
                                            
                                            DropdownMenuItem(
                                                onClick = {
                                                    showConversationsMenu = false
                                                    if (otherParticipant != null && otherParticipant != ownerId) {
                                                        onNavigateToConversation(otherParticipant, otherName, otherAvatar)
                                                    }
                                                },
                                                text = {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        if (otherAvatar != null) {
                                                            AsyncImage(
                                                                model = ImageRequest.Builder(LocalContext.current)
                                                                    .data(otherAvatar)
                                                                    .crossfade(true)
                                                                    .build(),
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
                                                                    .background(Color.Gray.copy(alpha = 0.3f)),
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
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = themeText
                                                        )
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeBackground)
                .padding(innerPadding)
        ) {
            // Messages List (comme iOS - spacing 20dp)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
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

            // Input Bar (comme iOS ligne 108-138)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeBackground)
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Paperclip icon (comme iOS ligne 110-112)
                IconButton(
                    onClick = { /* TODO: Attach file */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AttachFile,
                        contentDescription = "Attach",
                        tint = themePrimary.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Écrivez un message...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = themeCard,
                        unfocusedContainerColor = themeCard,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = themeText,
                        unfocusedTextColor = themeText
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 6,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )

                // Send button (comme iOS ligne 120-132)
                val isSending = false // TODO: Get from viewModel
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank() && !isSending) {
                            val textToSend = messageText
                            messageText = ""
                            viewModel.sendMessage(token, userId, textToSend)
                        }
                    },
                    enabled = messageText.isNotBlank() && !isSending,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSending || messageText.isBlank()) Color.Gray else themePrimary)
                        .shadow(8.dp, CircleShape, spotColor = themePrimary.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = if (isSending) Icons.Filled.Schedule else Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
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

    if (isOwnMessage) {
        OutgoingMessage(
            text = message.content,
            time = time,
            themeCard = themeCard,
            themeText = themeText
        )
    } else {
        IncomingMessage(
            text = message.content,
            time = time,
            avatarLetter = (message.senderName?.firstOrNull()?.uppercaseChar() ?: "U").toString(),
            profilePictureURL = message.senderAvatar,
            themePrimary = themePrimary
        )
    }
}

private fun String.substringOrNull(startIndex: Int, endIndex: Int): String? {
    return if (startIndex >= 0 && endIndex <= this.length && startIndex < endIndex) {
        this.substring(startIndex, endIndex)
    } else null
}
