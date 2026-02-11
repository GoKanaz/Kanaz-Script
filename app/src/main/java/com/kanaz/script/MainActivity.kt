package com.kanaz.script
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kanaz.script.ui.navigation.KanazApp
import com.kanaz.script.ui.theme.KanazScriptTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            KanazScriptTheme {
                SystemBars()
                Surface(color = MaterialTheme.colorScheme.background) {
                    KanazApp()
                }
            }
        }
    }
    @Composable
    private fun SystemBars() {
        val systemUiController = rememberSystemUiController()
        val darkTheme = isSystemInDarkTheme()
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = !darkTheme
            )
        }
    }
}
