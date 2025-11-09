package tn.esprit.labasniandroid.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tn.esprit.labasniandroid.ui.theme.AquaSoft
import tn.esprit.labasniandroid.ui.theme.PinkGradientTop
import tn.esprit.labasniandroid.ui.theme.PinkPrimary
import tn.esprit.labasniandroid.R

@Composable
fun LabasniIntroScreen(
    onLogin: () -> Unit,
    onSignup: () -> Unit
) {
    LabasniGradientBackground(
        topColor = PinkGradientTop,
        bottomColor = AquaSoft
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logocercle),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp),
                    contentScale = ContentScale.Fit
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Labasni",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Votre styliste intelligent",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LabasniPillButton(
                    text = "Se connecter",
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth(),
                    background = PinkPrimary,
                    contentColor = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                LabasniPillButton(
                    text = "Créer un compte",
                    onClick = onSignup,
                    modifier = Modifier.fillMaxWidth(),
                    background = AquaSoft.copy(alpha = 0.9f),
                    contentColor = Color.Black.copy(alpha = 0.6f),
                    borderColor = Color.White.copy(alpha = 0.35f)
                )
                Spacer(modifier = Modifier.height(28.dp))

                LabasniPageIndicators(
                    activeIndex = 1,
                    total = 3
                )
            }
        }
    }
}
