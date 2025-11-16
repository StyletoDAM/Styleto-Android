package tn.esprit.labasniandroid.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.NeutralSoft
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop

// Palette adaptative qui utilise les couleurs du thème actif
// En BLEUTheme: primary = #4AA3A2, secondary = #A7E0E0
// En PINKTheme: primary = rose, secondary = aqua
object LabasniPalette {
    @Composable
    fun Primary(): Color = MaterialTheme.colorScheme.primary
    
    @Composable
    fun Secondary(): Color = MaterialTheme.colorScheme.secondary
    
    @Composable
    fun GradientTop(): Color {
        val tertiary = MaterialTheme.colorScheme.tertiary
        return if (tertiary.alpha > 0.1f) tertiary else PinkGradientTop
    }
    
    @Composable
    fun GradientBottom(): Color = MaterialTheme.colorScheme.secondary
    
    @Composable
    fun Accent(): Color = MaterialTheme.colorScheme.primary // Utilise primary comme accent
    
    @Composable
    fun Neutral(): Color = NeutralSoft // Reste neutre
}

@Composable
fun LabasniGradientBackground(
    modifier: Modifier = Modifier,
    topColor: Color = LabasniPalette.GradientTop(),
    bottomColor: Color = LabasniPalette.GradientBottom(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(topColor, bottomColor)
                )
            )
    ) {
        content()
    }
}

@Composable
fun LabasniTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    extraContent: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Retour",
                tint = Color(0xFF2E7D32)
            )
        }
        if (extraContent != null) {
            Spacer(modifier = Modifier.width(8.dp))
            extraContent()
        }
    }
}

@Composable
fun LabasniPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = LabasniPalette.Primary(),
    contentColor: Color = Color.White,
    borderColor: Color? = null,
    enabled: Boolean = true,
    padding: PaddingValues = PaddingValues(vertical = 16.dp)
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .clip(CircleShape),
        shape = CircleShape,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = contentColor,
            disabledContainerColor = background.copy(alpha = 0.4f),
            disabledContentColor = contentColor.copy(alpha = 0.6f)
        ),
        border = borderColor?.let { BorderStroke(1.dp, it) },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        contentPadding = padding
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun LabasniOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colorScheme = MaterialTheme.colorScheme
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        singleLine = true,
        leadingIcon = leading,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            disabledContainerColor = colorScheme.surface,
            focusedBorderColor = LabasniPalette.Primary(),
            unfocusedBorderColor = LabasniPalette.Primary().copy(alpha = 0.6f),
            disabledBorderColor = colorScheme.outline.copy(alpha = 0.3f),
            cursorColor = LabasniPalette.Primary(),
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface,
            disabledTextColor = colorScheme.onSurface.copy(alpha = 0.5f),
            focusedLabelColor = LabasniPalette.Primary(),
            unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.7f),
            focusedPlaceholderColor = colorScheme.onSurface.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = colorScheme.onSurface.copy(alpha = 0.6f)
        )
    )
}

@Composable
fun LabasniCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = LabasniPalette.Accent(),
                uncheckedColor = LabasniPalette.Accent(),
                checkmarkColor = Color.White
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = LabasniPalette.Accent()
        )
    }
}

@Composable
fun LabasniPageIndicators(
    activeIndex: Int,
    total: Int,
    modifier: Modifier = Modifier,
    activeSize: Dp = 10.dp,
    inactiveSize: Dp = 8.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            val isActive = index == activeIndex
            val size by androidx.compose.animation.core.animateDpAsState(targetValue = if (isActive) activeSize else inactiveSize, label = "dotSize")
            val color by animateColorAsState(
                targetValue = if (isActive) Color.White else Color.White.copy(alpha = 0.45f),
                label = "dotColor"
            )
            Box(
                modifier = Modifier
                    .size(size)
                    .background(color, shape = CircleShape)
            )
            if (index != total - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun GenderChip(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (selected) LabasniPalette.Primary() else Color.White,
        label = "chipBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else LabasniPalette.Primary().copy(alpha = 0.8f),
        label = "chipContent"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor
        )
    }
}

@Composable
fun StyleTag(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onToggle: (() -> Unit)? = null
) {
    val background by animateColorAsState(
        targetValue = if (selected) LabasniPalette.Primary() else Color.White,
        label = "styleBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else LabasniPalette.Accent(),
        label = "styleContent"
    )
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .clickable(enabled = onToggle != null) { onToggle?.invoke() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun LabasniStatItem(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(LabasniPalette.Secondary().copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = LabasniPalette.Accent()
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LabasniPalette.Neutral()
        )
    }
}

enum class LabasniTab {
    Dressing, Outfits, Store, Profile
}

@Composable
fun LabasniTabBar(
    modifier: Modifier = Modifier,
    activeTab: LabasniTab = LabasniTab.Profile
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LabasniTabItem(
            icon = Icons.Rounded.Checkroom,
            label = "Dressing",
            isActive = activeTab == LabasniTab.Dressing
        )
        LabasniTabItem(
            icon = Icons.Rounded.People,
            label = "Tenues",
            isActive = activeTab == LabasniTab.Outfits
        )
        LabasniTabItem(
            icon = Icons.Rounded.ShoppingBag,
            label = "Store",
            isActive = activeTab == LabasniTab.Store
        )
        LabasniTabItem(
            icon = Icons.Rounded.Person,
            label = "Profil",
            isActive = activeTab == LabasniTab.Profile
        )
    }
}

@Composable
private fun LabasniTabItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val tint by animateColorAsState(
        targetValue = if (isActive) LabasniPalette.Primary() else LabasniPalette.Neutral(),
        label = "tabTint"
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall ?: TextStyle(fontSize = 12.sp),
            color = tint,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

