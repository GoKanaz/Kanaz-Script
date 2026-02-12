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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Code, contentDescription = null) },
            label = { Text("Editor") },
            selected = currentRoute?.startsWith("editor") == true,
            onClick = {
                navController.navigate("editor") {
                    popUpTo("explorer") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Folder, contentDescription = null) },
            label = { Text("Explorer") },
            selected = currentRoute == "explorer",
            onClick = {
                navController.navigate("explorer") {
                    popUpTo("explorer") { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Computer, contentDescription = null) },
            label = { Text("Terminal") },
            selected = currentRoute == "terminal",
            onClick = {
                navController.navigate("terminal") {
                    popUpTo("explorer") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Widgets, contentDescription = null) },
            label = { Text("Tools") },
            selected = currentRoute == "tools",
            onClick = {
                navController.navigate("tools") {
                    popUpTo("explorer") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            label = { Text("Settings") },
            selected = currentRoute == "settings",
            onClick = {
                navController.navigate("settings") {
                    popUpTo("explorer") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
