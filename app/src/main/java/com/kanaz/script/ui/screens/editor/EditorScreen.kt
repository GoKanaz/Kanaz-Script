package com.kanaz.script.ui.screens.editor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    filePath: String? = null,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var content by remember { mutableStateOf("") }
    var currentFileName by remember { mutableStateOf("untitled.txt") }
    var currentLanguage by remember { mutableStateOf("text") }
    var hasChanges by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf("") }
    LaunchedEffect(filePath) {
        if (filePath != null) {
            withContext(Dispatchers.IO) {
                try {
                    val file = File(filePath)
                    if (file.exists() && file.isFile) {
                        val text = file.readText()
                        withContext(Dispatchers.Main) {
                            content = text
                            currentFileName = file.name
                            currentLanguage = detectLanguage(file.extension)
                            hasChanges = false
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    fun saveFile() {
        if (filePath == null) {
            saveMessage = "No file path - cannot save"
            return
        }
        scope.launch {
            isSaving = true
            withContext(Dispatchers.IO) {
                try {
                    File(filePath).writeText(content, Charsets.UTF_8)
                    withContext(Dispatchers.Main) {
                        hasChanges = false
                        isSaving = false
                        saveMessage = "Saved!"
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isSaving = false
                        saveMessage = "Save failed: ${e.message}"
                    }
                }
            }
        }
    }
    BackHandler(enabled = hasChanges) {
        showSaveDialog = true
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentFileName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                    Text(
                        text = buildString {
                            append(currentLanguage.uppercase())
                            if (hasChanges) append(" • Unsaved changes")
                            if (filePath != null) append(" • ${content.lines().size} lines")
                        },
                        fontSize = 11.sp,
                        color = if (hasChanges)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (saveMessage.isNotEmpty()) {
                    Text(
                        text = saveMessage,
                        fontSize = 11.sp,
                        color = if (saveMessage.startsWith("Save failed"))
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    LaunchedEffect(saveMessage) {
                        kotlinx.coroutines.delay(2500)
                        saveMessage = ""
                    }
                }
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = { saveFile() },
                        enabled = hasChanges && filePath != null
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "Save",
                            tint = if (hasChanges && filePath != null)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
                IconButton(onClick = {
                    content = ""
                    hasChanges = false
                }) {
                    Icon(Icons.Default.Add, "New File",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            CodeEditor(
                content = content,
                onContentChange = {
                    content = it
                    hasChanges = true
                },
                language = currentLanguage,
                fileName = currentFileName,
                viewModel = viewModel
            )
        }
        if (hasChanges) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Unsaved changes",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = {
                                if (filePath != null) {
                                    scope.launch {
                                        withContext(Dispatchers.IO) {
                                            content = File(filePath).readText()
                                        }
                                        hasChanges = false
                                    }
                                } else {
                                    content = ""
                                    hasChanges = false
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Discard", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error)
                        }
                        Button(
                            onClick = { saveFile() },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Save", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("File '$currentFileName' has unsaved changes. What do you want to do?") },
            confirmButton = {
                Button(onClick = {
                    saveFile()
                    showSaveDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        hasChanges = false
                        showSaveDialog = false
                    }) {
                        Text("Discard", color = MaterialTheme.colorScheme.error)
                    }
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
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
