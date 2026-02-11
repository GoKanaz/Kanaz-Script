package com.kanaz.script.ui.navigation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kanaz.script.ui.screens.editor.EditorScreen
import com.kanaz.script.ui.screens.explorer.FileExplorerScreen
import com.kanaz.script.ui.screens.settings.SettingsScreen
import com.kanaz.script.ui.screens.terminal.TerminalScreen
import com.kanaz.script.ui.screens.tools.ToolsScreen
@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onTopBarVisibilityChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Editor.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Editor.route) {
            EditorScreen(
                onBottomBarVisibilityChange = onBottomBarVisibilityChange,
                onTopBarVisibilityChange = onTopBarVisibilityChange
            )
        }
        composable(Screen.Explorer.route) {
            FileExplorerScreen()
        }
        composable(Screen.Terminal.route) {
            TerminalScreen()
        }
        composable(Screen.Tools.route) {
            ToolsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
