package tn.esprit.labasniandroid.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tn.esprit.labasniandroid.R
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent
import tn.esprit.labasniandroid.utils.TokenManager
import tn.esprit.labasniandroid.viewmodels.ProfileViewModel
import androidx.compose.ui.layout.ContentScale

@Composable
fun LabasniProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val preferredStyles = listOf("Casual", "Sport", "Élégant", "Vintage", "Moderne", "Bohème")
    val selectedStyles = remember { 
        mutableStateListOf<String>().apply {
            user?.preferences?.forEach { add(it) }
        }
    }
    
    // Charger le profil au démarrage
    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context)
        if (token != null) {
            viewModel.loadProfile(token)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Session expirée. Veuillez vous reconnecter.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    
    // Gérer les erreurs
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    
    // Mettre à jour les styles sélectionnés quand l'utilisateur change
    LaunchedEffect(user?.preferences) {
        selectedStyles.clear()
        user?.preferences?.forEach { selectedStyles.add(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PinkGradientTop.copy(alpha = 0.3f), Color.White)
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LabasniTopBar(onBack = onBack)
                        TextButton(onClick = { /* TODO edit */ }) {
                            Text(
                                text = "Modifier",
                                color = PinkPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Mon Profil",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PinkPrimary
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(PinkPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials(user?.fullName ?: "User"),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                        
                        // Afficher le provider d'authentification si disponible
                        user?.authProvider?.let { provider ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(TealAccent.copy(alpha = 0.12f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = when (provider) {
                                        tn.esprit.labasniandroid.models.User.AuthProvider.GOOGLE -> "🌐 Connecté avec Google"
                                        tn.esprit.labasniandroid.models.User.AuthProvider.APPLE -> "🍎 Connecté avec Apple"
                                        tn.esprit.labasniandroid.models.User.AuthProvider.LOCAL -> "👤 Compte local"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = TealAccent.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        }
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PinkPrimary)
                        }
                    } else {
                        val currentUser = user
                        if (currentUser != null) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                ProfileLabeledField(
                                    label = "Nom complet",
                                    value = currentUser.fullName
                                )
                                ProfileLabeledField(
                                    label = "Email",
                                    value = currentUser.email
                                )

                                Text(
                                    text = "Sexe",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = TealAccent
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    listOf("Femme", "Homme").forEach { option ->
                                        val isSelected = when {
                                            option == "Femme" && currentUser.gender == tn.esprit.labasniandroid.models.User.Gender.FEMALE -> true
                                            option == "Homme" && currentUser.gender == tn.esprit.labasniandroid.models.User.Gender.MALE -> true
                                            else -> false
                                        }
                                        GenderChip(
                                            title = option,
                                            selected = isSelected,
                                            onClick = { /* TODO: Update gender */ }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Styles préférés",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = TealAccent
                        )
                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            preferredStyles.forEach { style ->
                                val isSelected = selectedStyles.contains(style)
                                StyleTag(
                                    title = style,
                                    selected = isSelected,
                                    onToggle = {
                                        if (isSelected) {
                                            selectedStyles.remove(style)
                                        } else {
                                            selectedStyles.add(style)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Carte Avatar 3D
                    ProfileCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Avatar 3D",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PinkPrimary
                                    )
                                )
                                Text(
                                    text = "Personnalisez votre avatar virtuel et visualisez vos tenues en 3D.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TealAccent.copy(alpha = 0.9f)
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(PinkPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Personnaliser",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = PinkPrimary
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Image(
                                painter = painterResource(id = R.drawable.logocercle),
                                contentDescription = null,
                                modifier = Modifier.size(92.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    // Mes tenues récentes
                    ProfileCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mes tenues récentes",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PinkPrimary
                                    )
                                )
                                TextButton(onClick = { /* TODO */ }) {
                                    Text(
                                        text = "Voir tout",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = PinkPrimary.copy(alpha = 0.85f)
                                        )
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                listOf("Casual Chic", "Streetwear", "Vintage", "Business", "Soirée").forEach { look ->
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(140.dp, 170.dp)
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = look,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = PinkPrimary
                                                )
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.ThumbUp,
                                                contentDescription = null,
                                                tint = PinkPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "${(120..240).random()}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TealAccent
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Suggestions IA
                    ProfileCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Suggestions IA pour Amira",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PinkPrimary
                                )
                            )
                            SuggestionRow(
                                icon = Icons.Rounded.AutoAwesome,
                                text = "Osez un look \"Casual Chic\" pour votre prochaine sortie du week-end."
                            )
                            SuggestionRow(
                                icon = Icons.Rounded.ShoppingCart,
                                text = "Nouveautés dans votre style favori : découvrez les articles Vintage sélectionnés pour vous."
                            )
                            SuggestionRow(
                                icon = Icons.Rounded.Notifications,
                                text = "Pensez à mettre à jour votre avatar pour essayer les derniers accessoires tendances."
                            )
                        }
                    }

                    // Programme VIP
                    ProfileCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Programme VIP",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PinkPrimary
                                )
                            )
                            Text(
                                text = "Cumulez des points à chaque interaction et débloquez des avantages exclusifs.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TealAccent
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressView(progress = 0.65f, points = "650 pts")
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ProgressTile(title = "Likes reçus", value = "120")
                                    ProgressTile(title = "Tenues partagées", value = "18")
                                    ProgressTile(title = "Commentaires", value = "45")
                                }
                            }
                        }
                    }

                    // Boutons CTA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LabasniPillButton(
                            text = "Voir ma garde-robe",
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f),
                            background = PinkPrimary,
                            contentColor = Color.White
                        )
                        LabasniPillButton(
                            text = "Créer une tenue",
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f),
                            background = AquaSoft,
                            contentColor = Color.White
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Mes statistiques",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PinkPrimary
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LabasniStatItem(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Checkroom,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                },
                                value = "48",
                                label = "Vêtements"
                            )
                            LabasniStatItem(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                },
                                value = "23",
                                label = "Tenues portées"
                            )
                            LabasniStatItem(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.LocalMall,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                },
                                value = "12",
                                label = "Articles vendus"
                            )
                        }
                    }

                    LabasniPillButton(
                        text = "Se déconnecter",
                        onClick = {
                            // Afficher la boîte de dialogue de confirmation
                            showLogoutDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        background = PinkPrimary.copy(alpha = 0.18f),
                        contentColor = PinkPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color.White)
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    LabasniTabBar(
                        modifier = Modifier.fillMaxWidth(),
                        activeTab = LabasniTab.Profile
                    )
                }
            }
        }
        
        // Boîte de dialogue de confirmation de déconnexion
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = "Déconnexion",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PinkPrimary
                        )
                    )
                },
                text = {
                    Text(
                        text = "Voulez-vous vous déconnecter ? Vous serez redirigé vers la page de connexion.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TealAccent
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            scope.launch {
                                // Supprimer le token
                                TokenManager.clearToken(context)
                                snackbarHostState.showSnackbar(
                                    message = "Vous êtes maintenant déconnecté(e).",
                                    duration = SnackbarDuration.Short
                                )
                                // Rediriger vers la page de connexion
                                onLogout()
                            }
                        }
                    ) {
                        Text(
                            text = "Déconnecter",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = PinkPrimary
                            )
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text(
                            text = "Annuler",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TealAccent
                            )
                        )
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun ProfileLabeledField(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TealAccent
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF374151)
            )
        }
    }
}

private fun initials(name: String): String =
    name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { it.first().uppercase() }

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        content()
    }
}

@Composable
private fun SuggestionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PinkPrimary.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PinkPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TealAccent.copy(alpha = 0.92f)
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CircularProgressView(
    progress: Float,
    points: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val strokeWidth = 10.dp.toPx()
                drawCircle(
                    color = PinkPrimary.copy(alpha = 0.25f),
                    radius = size.minDimension / 2 - strokeWidth / 2,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                )
                drawArc(
                    color = PinkPrimary,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strokeWidth,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    ),
                    topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2),
                    size = androidx.compose.ui.geometry.Size(
                        size.minDimension - strokeWidth,
                        size.minDimension - strokeWidth
                    )
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            )
        }
        Text(
            text = points,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = PinkPrimary
            )
        )
    }
}

@Composable
private fun ProgressTile(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PinkPrimary.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TealAccent.copy(alpha = 0.85f)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = PinkPrimary
            )
        )
    }
}
