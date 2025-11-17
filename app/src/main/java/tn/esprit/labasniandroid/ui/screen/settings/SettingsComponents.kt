package tn.esprit.labasniandroid.ui.screen.settings

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import tn.esprit.labasniandroid.models.entities.User
import tn.esprit.labasniandroid.ui.screen.profile.ProfileViewModel
import tn.esprit.labasniandroid.ui.theme.ThemeMode

// MARK: - Edit Profile Section Card
@Composable
fun EditProfileSectionCard(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    phone: String,
    onPhoneChange: (String) -> Unit,
    gender: User.Gender,
    onGenderChange: (User.Gender) -> Unit,
    selectedStyles: Set<StylePreference>,
    onStyleToggle: (StylePreference) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    hasChanges: Boolean,
    isLoading: Boolean,
    themePrimary: Color,
    themeSecondary: Color,
    themeTeal: Color,
    themeCard: Color,
    themeText: Color,
    themeSecondaryText: Color,
    themeSoftPink: Color,
    themeBackground: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeCard)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(themePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = themePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = themeText,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = themeSecondaryText,
                    modifier = Modifier.size(14.dp)
                )
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        // Full Name
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Full Name",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = themeSecondaryText
                            )
                            CustomTextField(
                                value = fullName,
                                onValueChange = onFullNameChange,
                                placeholder = "Full Name",
                                themeBackground = themeBackground,
                                themeTeal = themeTeal,
                                themePrimary = themePrimary,
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText
                            )
                        }
                        
                        // Email (disabled)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Email",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = themeSecondaryText
                            )
                            CustomTextField(
                                value = email,
                                onValueChange = {},
                                placeholder = "Email",
                                enabled = false,
                                themeBackground = themeBackground,
                                themeTeal = themeTeal,
                                themePrimary = themePrimary,
                                themeText = themeSecondaryText,
                                themeSecondaryText = themeSecondaryText
                            )
                        }
                        
                        // Phone
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Phone",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = themeSecondaryText
                            )
                            CustomTextField(
                                value = phone,
                                onValueChange = onPhoneChange,
                                placeholder = "Phone",
                                keyboardType = KeyboardType.Phone,
                                themeBackground = themeBackground,
                                themeTeal = themeTeal,
                                themePrimary = themePrimary,
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText
                            )
                        }
                        
                        // Gender
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Gender",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = themeSecondaryText
                            )
                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                SegmentedButton(
                                    selected = gender == User.Gender.FEMALE,
                                    onClick = { onGenderChange(User.Gender.FEMALE) },
                                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                                ) {
                                    Text("Female")
                                }
                                SegmentedButton(
                                    selected = gender == User.Gender.MALE,
                                    onClick = { onGenderChange(User.Gender.MALE) },
                                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                                ) {
                                    Text("Male")
                                }
                            }
                        }
                        
                        // Style Preferences
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Style Preferences",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = themeSecondaryText
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.height(200.dp)
                            ) {
                                items(StylePreference.values().toList()) { style ->
                                    StyleChip(
                                        title = style.displayName,
                                        isSelected = selectedStyles.contains(style),
                                        onClick = { onStyleToggle(style) },
                                        themePrimary = themePrimary,
                                        themeTeal = themeTeal,
                                        themeSoftPink = themeSoftPink
                                    )
                                }
                            }
                        }
                    }
                    
                    Divider(modifier = Modifier.padding(horizontal = 0.dp))
                    
                    // Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = themeText
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(vertical = 14.dp)
                            )
                        }
                        Button(
                            onClick = onSave,
                            modifier = Modifier.weight(1f),
                            enabled = hasChanges && !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasChanges) themePrimary else themePrimary.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Save changes",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// MARK: - Settings Section Card
@Composable
fun SettingsSectionCard(
    section: SettingsSection,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    themeMode: ThemeMode,
    themePrimary: Color,
    themeSecondary: Color,
    themeTeal: Color,
    themeCard: Color,
    themeText: Color,
    themeSecondaryText: Color,
    onThemeClick: () -> Unit,
    onPasswordChange: () -> Unit,
    onDeleteAccount: () -> Unit,
    viewModel: ProfileViewModel,
    context: android.content.Context
) {
    var showPasswordUpdate by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeCard)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(themePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = themePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = themeText,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = themeSecondaryText,
                    modifier = Modifier.size(14.dp)
                )
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    section.options.forEachIndexed { index, option ->
                        SettingsOptionRow(
                            option = option,
                            themeMode = themeMode,
                            themePrimary = themePrimary,
                            themeText = themeText,
                            themeSecondaryText = themeSecondaryText,
                            onThemeClick = onThemeClick,
                            onPasswordChange = { showPasswordUpdate = true },
                            onDeleteAccount = onDeleteAccount
                        )
                        if (index < section.options.size - 1) {
                            Divider(modifier = Modifier.padding(start = 80.dp))
                        }
                    }
                    
                    // Password update field (if expanded)
                    if (showPasswordUpdate && section.id == "security") {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "New Password",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = themeSecondaryText
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text("Enter new password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = themeCard,
                                    unfocusedContainerColor = themeCard
                                )
                            )
                            Button(
                                onClick = {
                                    val token = tn.esprit.labasniandroid.utils.TokenManager.getToken(context)
                                    if (token != null) {
                                        viewModel.updateProfile(token = token, password = password)
                                        showPasswordUpdate = false
                                        password = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = themePrimary)
                            ) {
                                Text("Save Password", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// MARK: - Settings Option Row
@Composable
private fun SettingsOptionRow(
    option: SettingsOption,
    themeMode: ThemeMode,
    themePrimary: Color,
    themeText: Color,
    themeSecondaryText: Color,
    onThemeClick: () -> Unit,
    onPasswordChange: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var toggleValue by remember { mutableStateOf(option.toggleValue) }
    val isDeleteAccount = option.title == "Delete Account"
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                when (option.title) {
                    "Theme" -> onThemeClick()
                    "Change Password" -> onPasswordChange()
                    "Delete Account" -> onDeleteAccount()
                }
            }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = null,
            tint = if (isDeleteAccount) Color.Red else themeSecondaryText,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = option.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = if (isDeleteAccount) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (isDeleteAccount) Color.Red else themeText,
            modifier = Modifier.weight(1f)
        )
        
        when {
            option.title == "Theme" -> {
                val label = when (themeMode) {
                    ThemeMode.LIGHT -> "Light"
                    ThemeMode.DARK -> "Dark"
                    ThemeMode.SYSTEM -> "System"
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = themeSecondaryText
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = themeSecondaryText,
                    modifier = Modifier.size(12.dp)
                )
            }
            option.hasToggle -> {
                Switch(
                    checked = toggleValue,
                    onCheckedChange = { toggleValue = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = themePrimary
                    )
                )
            }
            option.hasChevron -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = themeSecondaryText,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// MARK: - Style Chip
@Composable
private fun StyleChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    themePrimary: Color,
    themeTeal: Color,
    themeSoftPink: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(
                if (isSelected) themePrimary else themeSoftPink.copy(alpha = 0.6f)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = if (isSelected) Color.White else themeTeal
        )
    }
}

// MARK: - Custom Text Field
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    themeBackground: Color,
    themeTeal: Color,
    themePrimary: Color,
    themeText: Color,
    themeSecondaryText: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = themeSecondaryText.copy(alpha = 0.7f)) },
        enabled = enabled,
        readOnly = !enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = themeBackground,
            unfocusedContainerColor = themeBackground,
            disabledContainerColor = themeBackground,
            focusedBorderColor = themeTeal.copy(alpha = 0.2f),
            unfocusedBorderColor = themeTeal.copy(alpha = 0.2f),
            cursorColor = themePrimary,
            focusedTextColor = themeText,
            unfocusedTextColor = themeText,
            disabledTextColor = themeText.copy(alpha = 0.5f)
        )
    )
}

// MARK: - Theme Picker Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerSheet(
    themeMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
    themePrimary: Color,
    themeCard: Color,
    themeText: Color,
    themeBackground: Color
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = themeCard,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            ThemeMode.values().forEach { mode ->
                val label = when (mode) {
                    ThemeMode.LIGHT -> "Light"
                    ThemeMode.DARK -> "Dark"
                    ThemeMode.SYSTEM -> "System"
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeSelected(mode) }
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = themeText
                    )
                    if (themeMode == mode) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = themePrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// MARK: - Change Password Dialog
@Composable
fun ChangePasswordDialog(
    password: String,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean,
    themePrimary: Color,
    themeCard: Color,
    themeText: Color,
    themeSecondaryText: Color,
    themeSoftPink: Color,
    themeBackground: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = { Text("Enter new password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = themeCard,
                        unfocusedContainerColor = themeCard
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading && password.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = themePrimary
                    )
                } else {
                    Text("Save Password", fontWeight = FontWeight.Bold, color = themePrimary)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        }
    )
}

