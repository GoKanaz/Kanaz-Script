package com.kanaz.script.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kanaz.script.ui.screens.editor.EditorScreen
import com.kanaz.script.ui.screens.explorer.FileExplorerScreen
import com.kanaz.script.ui.screens.settings.SettingsScreen
import com.kanaz.script.ui.screens.terminal.TerminalScreen
import com.kanaz.script.ui.screens.tools.ToolsScreen

@Composable
fun KanazApp() {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "explorer",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("explorer") {
                FileExplorerScreen(navController = navController)
            }
            composable("editor") {
                EditorScreen(navController = navController)
            }
            composable("terminal") {
                TerminalScreen(navController = navController)
            }
            composable("tools") {
                ToolsScreen(navController = navController)
            }
            composable("settings") {
                SettingsScreen(navController = navController)
            }
        }
    }
}
