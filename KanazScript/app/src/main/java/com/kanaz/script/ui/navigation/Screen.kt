package com.kanaz.script.ui.navigation
sealed class Screen(val route: String) {
    object Editor : Screen("editor")
    object Explorer : Screen("explorer")
    object Terminal : Screen("terminal")
    object Tools : Screen("tools")
    object Settings : Screen("settings")
}
