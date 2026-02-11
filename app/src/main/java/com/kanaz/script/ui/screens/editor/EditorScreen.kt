package com.kanaz.script.ui.screens.editor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
@Composable
fun EditorScreen(
    viewModel: EditorViewModel = hiltViewModel()
) {
    var content by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize()) {
        CodeEditor(
            content = content,
            onContentChange = { content = it },
            language = "kotlin",
            fileName = "test.kt",
            viewModel = viewModel
        )
    }
}
