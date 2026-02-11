package com.kanaz.script.ui.navigation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kanaz.script.ui.screens.editor.EditorScreen
import com.kanaz.script.ui.screens.explorer.FileExplorerScreen
import com.kanaz.script.ui.screens.settings.SettingsScreen
import com.kanaz.script.ui.screens.terminal.TerminalScreen
import com.kanaz.script.ui.screens.tools.ToolsScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onTopBarVisibilityChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Explorer.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(
            route = "editor?filePath={filePath}",
            arguments = listOf(
                navArgument("filePath") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val filePath = backStackEntry.arguments?.getString("filePath")
            EditorScreen(filePath = filePath)
        }
        composable(Screen.Explorer.route) {
            FileExplorerScreen(
                onFileSelected = { filePath ->
                    val encodedPath = URLEncoder.encode(filePath, StandardCharsets.UTF_8.toString())
                    navController.navigate("editor?filePath=$encodedPath")
                }
            )
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
