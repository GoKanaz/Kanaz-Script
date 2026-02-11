package com.kanaz.script.ui.screens.editor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File
@Composable
fun EditorScreen(
    filePath: String? = null,
    viewModel: EditorViewModel = hiltViewModel()
) {
    var content by remember { mutableStateOf("") }
    var currentFileName by remember { mutableStateOf("untitled.txt") }
    var currentLanguage by remember { mutableStateOf("text") }
    LaunchedEffect(filePath) {
        if (filePath != null) {
            try {
                val file = File(filePath)
                if (file.exists() && file.isFile) {
                    content = file.readText()
                    currentFileName = file.name
                    currentLanguage = detectLanguage(file.extension)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        CodeEditor(
            content = content,
            onContentChange = { 
                content = it
                if (filePath != null) {
                    try {
                        File(filePath).writeText(it)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            language = currentLanguage,
            fileName = currentFileName,
            viewModel = viewModel
        )
    }
}
fun detectLanguage(extension: String): String {
    return when (extension.lowercase()) {
        "kt", "kts" -> "kotlin"
        "java" -> "java"
        "js", "jsx" -> "javascript"
        "ts", "tsx" -> "typescript"
        "py" -> "python"
        "html", "htm" -> "html"
        "css" -> "css"
        "json" -> "json"
        "xml" -> "xml"
        "md" -> "markdown"
        "sh", "bash" -> "bash"
        "c", "h" -> "c"
        "cpp", "hpp", "cc" -> "cpp"
        "rs" -> "rust"
        "go" -> "go"
        "php" -> "php"
        "rb" -> "ruby"
        "swift" -> "swift"
        else -> "text"
    }
}
