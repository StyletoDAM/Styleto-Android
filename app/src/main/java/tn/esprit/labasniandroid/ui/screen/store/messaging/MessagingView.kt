package tn.esprit.labasniandroid.ui.screen.store.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.input.KeyboardType
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

/**
 * MessagingView Android (comme iOS ChatView)
 * Liste des conversations avec recherche et navigation vers ChatDetailView
 */
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
    
    var searchText by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }


    LaunchedEffect(token, userId) {
        if (token.isNotBlank() && userId.isNotBlank()) {
            viewModel.initialize(token, userId)
        }
    }

    // Trier les conversations par updatedAt décroissant (comme iOS)
    val sortedConversations = remember(conversations) {
        conversations.sortedByDescending { conv ->
            conv.updatedAt?.let { dateString ->
                try {
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    format.parse(dateString)?.time ?: 0L
                } catch (e: Exception) {
                    0L
                }
            } ?: 0L
        }
    }

    // Filtrer les conversations par recherche (comme iOS - first name + last name)
    val filteredConversations by remember(sortedConversations, searchText, userId) {
        derivedStateOf {
            if (searchText.isBlank()) {
                sortedConversations
            } else {
                val normalizedSearch = normalizeText(searchText)
                sortedConversations.filter { conversation ->
                    val otherParticipant = conversation.participants.firstOrNull { 
                        it.trim().lowercase() != userId.trim().lowercase() 
                    }
                    val otherName = otherParticipant?.let { participantId ->
                        val normalizedId = participantId.trim().lowercase()
                        conversation.participantNames.entries.firstOrNull { 
                            it.key.trim().lowercase() == normalizedId 
                        }?.value
                    } ?: ""

                    if (otherName.isBlank()) return@filter false

                    // Normaliser le nom complet
                    val normalizedFullName = normalizeText(otherName)
                    if (normalizedFullName.contains(normalizedSearch)) {
                        return@filter true
                    }

                    // Vérifier chaque composant du nom (first name, last name...)
                    val nameComponents = otherName.split(" ").filter { it.isNotBlank() }
                    nameComponents.any { component ->
                        val normalizedComponent = normalizeText(component)
                        normalizedComponent.startsWith(normalizedSearch) || 
                        normalizedComponent.contains(normalizedSearch)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            // Header avec titre et bouton retour (comme iOS NavigationStack)
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
                        fontSize = 20.sp,
                        color = themePrimary
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeBackground)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Search Bar (comme iOS)
            MessagingSearchBar(
                value = searchText,
                onValueChange = { searchText = it },
                isFocused = isSearchFocused,
                onFocusChange = { isSearchFocused = it },
                onClear = { 
                    searchText = ""
                    isSearchFocused = false
                },
                themePrimary = themePrimary,
                themeAqua = themeAqua
            )

            // Conversations List ou Empty State
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = themePrimary)
                    }
                }
                filteredConversations.isEmpty() -> {
                    EmptyStateView(
                        searchText = searchText,
                        onClearSearch = { searchText = "" },
                        themePrimary = themePrimary,
                        themeText = themeText
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 14.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredConversations, key = { it.id }) { conversation ->
                            ConversationRow(
                                conversation = conversation,
                                userId = userId,
                                searchText = searchText,
                                themePrimary = themePrimary,
                                themeCard = themeCard,
                                themeText = themeText,
                                onClick = {
                                    
                                    isSearchFocused = false
                                    
                                    // Trouver l'autre participant
                                    val userIdNormalized = userId.trim().lowercase()
                                    
                                    val otherParticipant = conversation.participants.firstOrNull { participantId ->
                                        val participantNormalized = participantId.trim().lowercase()
                                        val isOther = participantNormalized != userIdNormalized
                                        isOther
                                    }
                                    
                                    
                                    if (otherParticipant == null) {
                                        return@ConversationRow
                                    }
                                    
                                    // Chercher le nom
                                    val otherName = otherParticipant.let { participantId ->
                                        
                                        // Essayer avec l'ID exact
                                        val nameExact = conversation.participantNames[participantId]
                                        
                                        // Essayer avec l'ID normalisé
                                        val normalizedId = participantId.trim().lowercase()
                                        val nameNormalized = conversation.participantNames[normalizedId]
                                        
                                        // Essayer avec les entries
                                        val nameFromEntry = conversation.participantNames.entries.firstOrNull { 
                                            it.key.trim().lowercase() == normalizedId 
                                        }?.value
                                        
                                        val finalName = nameExact ?: nameNormalized ?: nameFromEntry
                                        finalName
                                    }
                                    
                                    
                                    // Chercher l'avatar
                                    val otherAvatar = otherParticipant.let { participantId ->
                                        val normalizedId = participantId.trim().lowercase()
                                        val avatar = conversation.participantAvatars[participantId]
                                            ?: conversation.participantAvatars[normalizedId]
                                            ?: conversation.participantAvatars.entries.firstOrNull { 
                                                it.key.trim().lowercase() == normalizedId 
                                            }?.value
                                        avatar
                                    }
                                    
                                    
                                    val finalName = if (otherName.isNullOrBlank() || otherName == "Utilisateur") {
                                        "Utilisateur"
                                    } else {
                                        otherName
                                    }
                                    
                                    onNavigateToChat(otherParticipant, finalName, otherAvatar)
                                    
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Barre de recherche (comme iOS)
 */
@Composable
private fun MessagingSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    onClear: () -> Unit,
    themePrimary: Color,
    themeAqua: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isTextFieldFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isTextFieldFocused) {
        onFocusChange(isTextFieldFocused)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp, bottom = 14.dp)
            .height(55.dp)
            .background(
                color = themeAqua.copy(alpha = 0.25f),
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (isTextFieldFocused) {
                    Modifier.border(
                        width = 2.dp,
                        color = themePrimary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = if (isTextFieldFocused) themePrimary else Color.Gray.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )

            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                interactionSource = interactionSource,
                placeholder = {
                    Text(
                        text = "Search conversation...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = themePrimary,
                    unfocusedTextColor = themePrimary,
                    cursorColor = themePrimary
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // Clear button (comme iOS)
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear",
                        tint = Color.Gray.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Empty State (comme iOS)
 */
@Composable
private fun EmptyStateView(
    searchText: String,
    onClearSearch: () -> Unit,
    themePrimary: Color,
    themeText: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (searchText.isEmpty()) "💬" else "🔍",
            fontSize = 50.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (searchText.isEmpty()) "No conversations yet" else "No results for '$searchText'",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Gray
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (searchText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onClearSearch,
                shape = RoundedCornerShape(50.dp),
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = themePrimary
                )
            ) {
                Text(
                    text = "Clear search",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Conversation Row (comme iOS ChatRow)
 */
@Composable
private fun ConversationRow(
    conversation: Conversation,
    userId: String,
    searchText: String,
    themePrimary: Color,
    themeCard: Color,
    themeText: Color,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    
    // Trouver l'autre participant
    val userIdNormalized = userId.trim().lowercase()
    
    val otherParticipant = conversation.participants.firstOrNull { participantId ->
        val participantNormalized = participantId.trim().lowercase()
        val isOther = participantNormalized != userIdNormalized
        isOther
    }
    
    
    val otherName = otherParticipant?.let { participantId ->
        
        // Essayer avec l'ID exact
        val nameExact = conversation.participantNames[participantId]
        
        // Essayer avec l'ID normalisé
        val normalizedId = participantId.trim().lowercase()
        val nameNormalized = conversation.participantNames[normalizedId]
        
        // Essayer avec les entries
        val nameFromEntry = conversation.participantNames.entries.firstOrNull { 
            it.key.trim().lowercase() == normalizedId 
        }?.value
        
        val finalName = nameExact ?: nameNormalized ?: nameFromEntry
        finalName
    } ?: run {
        "Utilisateur"
    }
    
    
    val otherAvatar = otherParticipant?.let { participantId ->
        val normalizedId = participantId.trim().lowercase()
        val avatar = conversation.participantAvatars[participantId]
            ?: conversation.participantAvatars[normalizedId]
            ?: conversation.participantAvatars.entries.firstOrNull { 
                it.key.trim().lowercase() == normalizedId 
            }?.value
        avatar
    }
    

    val lastMessage = conversation.lastMessage
    val messageText = lastMessage?.content ?: "Start the conversation"
    val messageTime = lastMessage?.createdAt?.let { formatRelativeTime(it) } ?: "New"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(themeCard, RoundedCornerShape(20.dp))
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar (56x56 comme iOS)
        Box {
            if (otherAvatar != null && otherAvatar.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
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
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    )
                }
            }
            // Online indicator (optionnel, toujours false pour l'instant)
        }

        // Name and Message (comme iOS)
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
                text = messageText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = Color.Gray
                ),
                maxLines = 1
            )
        }

        // Time and Badge (comme iOS)
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = messageTime,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )
            // Badge pour messages non lus (optionnel, nil dans iOS)
        }
    }
}

/**
 * Normalise le texte (supprime les accents, lowercase) - comme iOS folding
 */
private fun normalizeText(text: String): String {
    return text
        .lowercase(Locale.getDefault())
        .replace(Regex("[àáâãäå]"), "a")
        .replace(Regex("[èéêë]"), "e")
        .replace(Regex("[ìíîï]"), "i")
        .replace(Regex("[òóôõö]"), "o")
        .replace(Regex("[ùúûü]"), "u")
        .replace(Regex("[ç]"), "c")
        .replace(Regex("[ñ]"), "n")
        .trim()
}

/**
 * Format temps relatif (comme iOS relativeTime)
 */
private fun formatRelativeTime(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return "New"
        val now = Date()
        val diff = now.time - date.time
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        when {
            seconds < 60 -> "now"
            minutes < 60 -> "${minutes}m"
            hours < 24 -> "${hours}h"
            days < 7 -> "${days}d"
            weeks < 4 -> "${weeks}w"
            months < 12 -> "${months}mo"
            else -> "${years}y"
        }
    } catch (e: Exception) {
        "New"
    }
}
