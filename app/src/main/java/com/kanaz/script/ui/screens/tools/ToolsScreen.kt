package com.kanaz.script.ui.screens.tools
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanaz.script.data.GitStatusType
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(viewModel: ToolsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Git", "Find & Replace", "Formatter", "Converter")
    val defaultPath = Environment.getExternalStorageDirectory().absolutePath
    LaunchedEffect(Unit) {
        viewModel.setRepoPath(defaultPath)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        if (state.message.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isError)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (state.isError) Icons.Default.Error else Icons.Default.CheckCircle,
                        null,
                        tint = if (state.isError)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = state.message,
                        color = if (state.isError)
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 0.dp) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 13.sp) }
                )
            }
        }
        when (selectedTab) {
            0 -> GitTab(state = state, viewModel = viewModel)
            1 -> FindReplaceTab(state = state, viewModel = viewModel, searchPath = defaultPath)
            2 -> FormatterTab(viewModel = viewModel)
            3 -> ConverterTab(viewModel = viewModel)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitTab(state: ToolsState, viewModel: ToolsViewModel) {
    var repoPath by remember { mutableStateOf(state.repoPath) }
    var commitMsg by remember { mutableStateOf(state.commitMessage) }
    var showPushDialog by remember { mutableStateOf(false) }
    var showPullDialog by remember { mutableStateOf(false) }
    var selectedBranch by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ToolSection(title = "Repository", icon = Icons.Default.AccountTree) {
                OutlinedTextField(
                    value = repoPath,
                    onValueChange = { repoPath = it },
                    label = { Text("Repository Path") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.setRepoPath(repoPath) }) {
                            Icon(Icons.Default.Search, "Open")
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
                if (!state.isGitRepo) {
                    Button(
                        onClick = { viewModel.gitInit() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Initialize Git Repository")
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Git repository", fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.weight(1f))
                        Text("Branch: ${state.currentBranch}", fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        if (state.isGitRepo) {
            item {
                ToolSection(title = "Status", icon = Icons.Default.Info) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${state.gitStatus.size} file(s) changed",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        IconButton(onClick = { viewModel.refreshGitStatus() }) {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                    }
                    if (state.gitStatus.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        state.gitStatus.take(10).forEach { status ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (status.type) {
                                        GitStatusType.ADDED -> "A"
                                        GitStatusType.MODIFIED -> "M"
                                        GitStatusType.DELETED -> "D"
                                        GitStatusType.MISSING -> "!"
                                    },
                                    color = when (status.type) {
                                        GitStatusType.ADDED -> MaterialTheme.colorScheme.primary
                                        GitStatusType.MODIFIED -> MaterialTheme.colorScheme.tertiary
                                        GitStatusType.DELETED -> MaterialTheme.colorScheme.error
                                        GitStatusType.MISSING -> MaterialTheme.colorScheme.error
                                    },
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(20.dp)
                                )
                                Text(
                                    text = status.filePath,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        if (state.gitStatus.size > 10) {
                            Text("...and ${state.gitStatus.size - 10} more",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp)
                        }
                    }
                }
            }
            item {
                ToolSection(title = "Commit", icon = Icons.Default.Save) {
                    OutlinedTextField(
                        value = commitMsg,
                        onValueChange = {
                            commitMsg = it
                            viewModel.updateCommitMessage(it)
                        },
                        label = { Text("Commit Message") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.gitAdd() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Stage All")
                        }
                        Button(
                            onClick = { viewModel.gitCommit() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Commit")
                        }
                    }
                }
            }
            item {
                ToolSection(title = "Remote", icon = Icons.Default.Cloud) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showPullDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Pull")
                        }
                        Button(
                            onClick = { showPushDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Upload, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Push")
                        }
                    }
                }
            }
            if (state.branches.isNotEmpty()) {
                item {
                    ToolSection(title = "Branches", icon = Icons.Default.AccountTree) {
                        state.branches.forEach { branch ->
                            val shortName = branch.removePrefix("refs/heads/")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.checkoutBranch(shortName) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (shortName == state.currentBranch)
                                        Icons.Default.RadioButtonChecked
                                    else
                                        Icons.Default.RadioButtonUnchecked,
                                    null,
                                    tint = if (shortName == state.currentBranch)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(shortName, fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
    if (showPushDialog) {
        CredentialsDialog(
            title = "Push",
            onDismiss = { showPushDialog = false },
            onConfirm = { user, pass ->
                viewModel.gitPush(user, pass)
                showPushDialog = false
            }
        )
    }
    if (showPullDialog) {
        CredentialsDialog(
            title = "Pull",
            onDismiss = { showPullDialog = false },
            onConfirm = { user, pass ->
                viewModel.gitPull(user, pass)
                showPullDialog = false
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindReplaceTab(state: ToolsState, viewModel: ToolsViewModel, searchPath: String) {
    var useRegex by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ToolSection(title = "Find & Replace", icon = Icons.Default.FindReplace) {
            OutlinedTextField(
                value = state.findText,
                onValueChange = { viewModel.updateFindText(it) },
                label = { Text("Find") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (state.findText.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateFindText("") }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.replaceText,
                onValueChange = { viewModel.updateReplaceText(it) },
                label = { Text("Replace with") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = useRegex, onCheckedChange = { useRegex = it })
                Text("Use Regex", fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                Button(onClick = { viewModel.findInFiles(searchPath, useRegex) }) {
                    Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Search")
                }
            }
        }
        if (state.findResults.isNotEmpty()) {
            Text("${state.findResults.size} result(s)",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium)
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.findResults) { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = result.filePath.substringAfterLast("/"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Line ${result.lineNumber}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = result.lineContent,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            if (state.replaceText.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                TextButton(
                                    onClick = { viewModel.replaceInFile(result.filePath) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.FindReplace, null,
                                        modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Replace in this file", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormatterTab(viewModel: ToolsViewModel) {
    var inputText by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf("JSON") }
    val formats = listOf("JSON")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ToolSection(title = "Code Formatter", icon = Icons.Default.Code) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                formats.forEach { format ->
                    FilterChip(
                        selected = selectedFormat == format,
                        onClick = { selectedFormat = format },
                        label = { Text(format) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Input $selectedFormat") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        outputText = when (selectedFormat) {
                            "JSON" -> viewModel.formatJson(inputText)
                            else -> inputText
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AutoFixHigh, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Format")
                }
                OutlinedButton(
                    onClick = {
                        inputText = ""
                        outputText = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Clear, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Clear")
                }
            }
            if (outputText.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("Output:", fontWeight = FontWeight.Medium)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = outputText,
                        modifier = Modifier.padding(12.dp),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterTab(viewModel: ToolsViewModel) {
    var filePath by remember { mutableStateOf("") }
    var targetExtension by remember { mutableStateOf("txt") }
    val commonExtensions = listOf("txt", "md", "json", "xml", "csv", "html", "kt", "py")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ToolSection(title = "File Converter", icon = Icons.Default.SwapHoriz) {
            OutlinedTextField(
                value = filePath,
                onValueChange = { filePath = it },
                label = { Text("Source File Path") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("/storage/emulated/0/file.txt") }
            )
            Spacer(Modifier.height(12.dp))
            Text("Convert to:", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = targetExtension,
                    onValueChange = { targetExtension = it },
                    label = { Text("Extension") },
                    modifier = Modifier.width(120.dp),
                    singleLine = true,
                    prefix = { Text(".") }
                )
                Button(
                    onClick = {
                        if (filePath.isNotBlank()) {
                            viewModel.convertFile(filePath, targetExtension)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.SwapHoriz, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Convert")
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Common extensions:", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                commonExtensions.take(4).forEach { ext ->
                    FilterChip(
                        selected = targetExtension == ext,
                        onClick = { targetExtension = ext },
                        label = { Text(ext, fontSize = 11.sp) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                commonExtensions.drop(4).forEach { ext ->
                    FilterChip(
                        selected = targetExtension == ext,
                        onClick = { targetExtension = ext },
                        label = { Text(ext, fontSize = 11.sp) }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialsDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Git $title") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter credentials (optional for public repos)",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username / Token") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password / Token") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(username, password) }) {
                Text(title)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
@Composable
fun ToolSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}
