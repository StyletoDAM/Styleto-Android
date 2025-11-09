package tn.esprit.labasniandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import tn.esprit.labasniandroid.ui.theme.LabasniTheme
import tn.esprit.labasniandroid.ui.theme.ThemeController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeController.initialize(applicationContext)
        setContent {
            val themeMode by ThemeController.themeMode.collectAsState()
            LabasniTheme(themeMode = themeMode) {
                Surface {
                    LabasniApp()
                }
            }
        }
    }
}
