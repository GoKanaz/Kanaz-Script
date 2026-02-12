package com.kanaz.script.ui.screens.editor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    var isReadOnly by remember { mutableStateOf(false) }
    var isPinned by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var cursorLine by remember { mutableStateOf(1) }
    var cursorCol by remember { mutableStateOf(1) }
    var undoStack by remember { mutableStateOf(listOf<String>()) }
    var redoStack by remember { mutableStateOf(listOf<String>()) }
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
                            undoStack = listOf()
                            redoStack = listOf()
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
            saveMessage = "No file path"
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
                        delay(2000)
                        saveMessage = ""
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isSaving = false
                        saveMessage = "Failed: ${e.message}"
                        delay(3000)
                        saveMessage = ""
                    }
                }
            }
        }
    }
    fun pushUndo(oldContent: String) {
        undoStack = (undoStack + oldContent).takeLast(50)
        redoStack = listOf()
    }
    fun undo() {
        if (undoStack.isEmpty()) return
        redoStack = redoStack + content
        content = undoStack.last()
        undoStack = undoStack.dropLast(1)
        hasChanges = true
    }
    fun redo() {
        if (redoStack.isEmpty()) return
        undoStack = undoStack + content
        content = redoStack.last()
        redoStack = redoStack.dropLast(1)
        hasChanges = true
    }
    BackHandler(enabled = hasChanges) {
        showSaveDialog = true
    }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { isPinned = !isPinned }) {
                    Icon(
                        if (isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                        "Pin",
                        tint = if (isPinned)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { undo() },
                    enabled = undoStack.isNotEmpty()
                ) {
                    Icon(
                        Icons.Default.Undo,
                        "Undo",
                        tint = if (undoStack.isNotEmpty())
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                IconButton(
                    onClick = { redo() },
                    enabled = redoStack.isNotEmpty()
                ) {
                    Icon(
                        Icons.Default.Redo,
                        "Redo",
                        tint = if (redoStack.isNotEmpty())
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                if (isSaving) {
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    IconButton(
                        onClick = { saveFile() },
                        enabled = hasChanges && filePath != null
                    ) {
                        Icon(
                            Icons.Default.Save,
                            "Save",
                            tint = if (hasChanges && filePath != null)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
                IconButton(onClick = { isReadOnly = !isReadOnly }) {
                    Icon(
                        if (isReadOnly) Icons.Default.EditOff else Icons.Default.Edit,
                        "Toggle Edit",
                        tint = if (!isReadOnly)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Box {
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Save") },
                            leadingIcon = { Icon(Icons.Default.Save, null) },
                            onClick = {
                                saveFile()
                                showMoreMenu = false
                            },
                            enabled = hasChanges && filePath != null
                        )
                        DropdownMenuItem(
                            text = { Text("Discard Changes") },
                            leadingIcon = { Icon(Icons.Default.Restore, null) },
                            onClick = {
                                scope.launch {
                                    if (filePath != null) {
                                        content = withContext(Dispatchers.IO) {
                                            File(filePath).readText()
                                        }
                                    } else {
                                        content = ""
                                    }
                                    hasChanges = false
                                    undoStack = listOf()
                                    redoStack = listOf()
                                }
                                showMoreMenu = false
                            },
                            enabled = hasChanges
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text(if (isReadOnly) "Enable Editing" else "Read Only Mode") },
                            leadingIcon = {
                                Icon(
                                    if (isReadOnly) Icons.Default.Edit else Icons.Default.EditOff,
                                    null
                                )
                            },
                            onClick = {
                                isReadOnly = !isReadOnly
                                showMoreMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Select All") },
                            leadingIcon = { Icon(Icons.Default.SelectAll, null) },
                            onClick = { showMoreMenu = false }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("File Info") },
                            leadingIcon = { Icon(Icons.Default.Info, null) },
                            onClick = {
                                val lines = content.lines().size
                                val chars = content.length
                                val size = filePath?.let { File(it).length() } ?: 0
                                saveMessage = "$lines lines, $chars chars, ${size}B"
                                showMoreMenu = false
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currentFileName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (saveMessage.isNotEmpty()) {
                        Text(
                            text = saveMessage,
                            fontSize = 11.sp,
                            color = if (saveMessage.startsWith("F") || saveMessage.startsWith("No"))
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "$cursorLine:$cursorCol",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "UTF-8",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isReadOnly) {
                        Text(
                            text = "READ",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            CodeEditor(
                content = content,
                onContentChange = { newContent ->
                    if (!isReadOnly) {
                        pushUndo(content)
                        content = newContent
                        hasChanges = true
                        val lines = newContent.lines()
                        cursorLine = lines.size
                        cursorCol = (lines.lastOrNull()?.length ?: 0) + 1
                    }
                },
                language = currentLanguage,
                fileName = currentFileName,
                viewModel = viewModel,
                readOnly = isReadOnly
            )
        }
        val keyboardScrollState = rememberScrollState()
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(keyboardScrollState)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf("→", "/", "+", "-", "*", "=", "<", ">", "{", "}", "(", ")", "[", "]", ";", ":", "\"", "'", "\\", "&", "|", "!", "?", "#", "@", ".", ",").forEach { symbol ->
                    TextButton(
                        onClick = {
                            if (!isReadOnly) {
                                pushUndo(content)
                                content = content + symbol
                                hasChanges = true
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = symbol,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("'$currentFileName' has unsaved changes.") },
            confirmButton = {
                Button(onClick = {
                    saveFile()
                    showSaveDialog = false
                }) { Text("Save") }
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
