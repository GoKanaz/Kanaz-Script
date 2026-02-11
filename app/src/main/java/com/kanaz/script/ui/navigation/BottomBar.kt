package com.kanaz.script.ui.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kanaz.script.R
@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        Screen.Editor,
        Screen.Explorer,
        Screen.Terminal,
        Screen.Tools,
        Screen.Settings
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Editor -> Icon(Icons.Filled.Code, contentDescription = null)
                        Screen.Explorer -> Icon(Icons.Filled.Folder, contentDescription = null)
                        Screen.Terminal -> Icon(Icons.Filled.Computer, contentDescription = null)
                        Screen.Tools -> Icon(Icons.Filled.Widgets, contentDescription = null)
                        Screen.Settings -> Icon(Icons.Filled.Settings, contentDescription = null)
                    }
                },
                label = {
                    Text(
                        when (screen) {
                            Screen.Editor -> stringResource(R.string.editor)
                            Screen.Explorer -> stringResource(R.string.explorer)
                            Screen.Terminal -> stringResource(R.string.terminal)
                            Screen.Tools -> stringResource(R.string.tools)
                            Screen.Settings -> stringResource(R.string.settings)
                        }
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
