package com.kanaz.script.ui
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kanaz.script.ui.navigation.AppNavHost
import com.kanaz.script.ui.navigation.BottomBar
import com.kanaz.script.ui.navigation.TopBar
@Composable
fun KanazApp() {
    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(true) }
    var showTopBar by remember { mutableStateOf(true) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { if (showTopBar) TopBar(navController) },
        bottomBar = { if (showBottomBar) BottomBar(navController) }
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            paddingValues = paddingValues,
            onBottomBarVisibilityChange = { showBottomBar = it },
            onTopBarVisibilityChange = { showTopBar = it }
        )
    }
}
