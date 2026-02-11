package com.kanaz.script.ui.navigation
sealed class Screen(val route: String, val title: String) {
    object Editor : Screen("editor?filePath={filePath}", "Editor")
    object Explorer : Screen("explorer", "Explorer")
    object Terminal : Screen("terminal", "Terminal")
    object Tools : Screen("tools", "Tools")
    object Settings : Screen("settings", "Settings")
}
