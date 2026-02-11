package com.kanaz.script.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kanaz.script.ui.screens.editor.EditorScreen
import com.kanaz.script.ui.screens.explorer.FileExplorerScreen
import com.kanaz.script.ui.screens.settings.SettingsScreen
import com.kanaz.script.ui.screens.terminal.TerminalScreen
import com.kanaz.script.ui.screens.tools.ToolsScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun KanazApp() {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(navController = navController)
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "explorer",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("explorer") {
                FileExplorerScreen(
                    onFileSelected = { filePath ->
                        navController.navigate("editor/$filePath")
                    }
                )
            }
            composable(
                route = "editor/{filePath}",
                arguments = listOf(
                    navArgument("filePath") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val encodedPath = backStackEntry.arguments?.getString("filePath")
                val filePath = encodedPath?.let {
                    URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                }
                EditorScreen(filePath = filePath)
            }
            composable("editor") {
                EditorScreen(filePath = null)
            }
            composable("terminal") {
                TerminalScreen()
            }
            composable("tools") {
                ToolsScreen()
            }
            composable("settings") {
                SettingsScreen()
            }
        }
    }
}
