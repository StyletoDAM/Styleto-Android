package tn.esprit.labasniandroid.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.ui.theme.TealAccent

@Composable
fun PhoneInputField(
    number: String,
    onNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = number,
        onValueChange = { newValue ->
            // Filtrer pour garder uniquement les chiffres
            val digits = newValue.filter { it.isDigit() }
            onNumberChange(digits)
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Numéro de téléphone",
                color = TealAccent.copy(alpha = 0.7f)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = PinkPrimary,
            unfocusedBorderColor = PinkPrimary.copy(alpha = 0.8f),
            cursorColor = PinkPrimary
        )
    )
}

