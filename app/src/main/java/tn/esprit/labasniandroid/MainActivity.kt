package tn.esprit.labasniandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import tn.esprit.labasniandroid.ui.theme.LabasniTheme
import tn.esprit.labasniandroid.ui.theme.ThemeController
import tn.esprit.labasniandroid.utils.CartManager

@androidx.compose.material3.ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeController.initialize(applicationContext)
        CartManager.initialize(applicationContext) // Initialiser CartManager (comme iOS PersistenceController)
        setContent {
            val themeMode by ThemeController.themeMode.collectAsState()
            val themeVariant by ThemeController.themeVariant.collectAsState()
            LabasniTheme(themeMode = themeMode, variant = themeVariant) {
                Surface {
                    LabasniApp()
                }
            }
        }
    }
}
