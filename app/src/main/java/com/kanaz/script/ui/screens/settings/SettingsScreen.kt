package com.kanaz.script.ui.screens.settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { SettingsCategory(icon = Icons.Default.Code, title = "Editor") }
        item {
            SliderSetting(
                title = "Font Size",
                value = settings.fontSize.toFloat(),
                range = 12f..24f,
                steps = 11,
                display = "${settings.fontSize} sp",
                onValueChange = { viewModel.saveSetting(SettingsKeys.FONT_SIZE, it.toInt()) }
            )
        }
        item {
            DropdownSetting(
                title = "Font Family",
                value = settings.fontFamily,
                options = listOf("Monospace", "Default", "Small"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.FONT_FAMILY, it) }
            )
        }
        item {
            DropdownSetting(
                title = "Tab Size",
                value = "${settings.tabSize} spaces",
                options = listOf("2 spaces", "4 spaces", "6 spaces", "8 spaces"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.TAB_SIZE, it.first().digitToInt()) }
            )
        }
        item { SwitchSetting("Line Numbers", settings.lineNumbers) { viewModel.saveSetting(SettingsKeys.LINE_NUMBERS, it) } }
        item { SwitchSetting("Word Wrap", settings.wordWrap) { viewModel.saveSetting(SettingsKeys.WORD_WRAP, it) } }
        item { SwitchSetting("Auto Save", settings.autoSave) { viewModel.saveSetting(SettingsKeys.AUTO_SAVE, it) } }
        if (settings.autoSave) {
            item {
                DropdownSetting(
                    title = "Auto Save Interval",
                    value = "${settings.autoSaveInterval} min",
                    options = listOf("1 min", "3 min", "5 min", "10 min"),
                    onValueChange = { viewModel.saveSetting(SettingsKeys.AUTO_SAVE_INTERVAL, it.first().digitToInt()) }
                )
            }
        }
        item { SwitchSetting("Highlight Current Line", settings.highlightLine) { viewModel.saveSetting(SettingsKeys.HIGHLIGHT_LINE, it) } }
        item { SwitchSetting("Show Whitespace", settings.showWhitespace) { viewModel.saveSetting(SettingsKeys.SHOW_WHITESPACE, it) } }
        item { SwitchSetting("Auto Pair Brackets", settings.autoPair) { viewModel.saveSetting(SettingsKeys.AUTO_PAIR, it) } }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Palette, title = "Appearance / Theme") }
        item {
            DropdownSetting(
                title = "App Theme",
                value = settings.appTheme,
                options = listOf("Light", "Dark", "System", "High Contrast"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.APP_THEME, it) }
            )
        }
        item {
            DropdownSetting(
                title = "Editor Color Scheme",
                value = settings.editorScheme,
                options = listOf("Dark", "Light", "Monokai", "Solarized", "Dracula"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.EDITOR_SCHEME, it) }
            )
        }
        item { SwitchSetting("Syntax Highlighting", settings.syntaxHighlight) { viewModel.saveSetting(SettingsKeys.SYNTAX_HIGHLIGHT, it) } }
        item {
            DropdownSetting(
                title = "UI Density",
                value = settings.uiDensity,
                options = listOf("Compact", "Comfortable", "Standard"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.UI_DENSITY, it) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Folder, title = "File Management") }
        item {
            DropdownSetting(
                title = "Default Save Location",
                value = settings.defaultSaveLocation,
                options = listOf("Internal", "External", "Custom"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.DEFAULT_SAVE_LOCATION, it) }
            )
        }
        item { SwitchSetting("Auto Backup", settings.autoBackup) { viewModel.saveSetting(SettingsKeys.AUTO_BACKUP, it) } }
        if (settings.autoBackup) {
            item {
                DropdownSetting(
                    title = "Max Backups",
                    value = "${settings.maxBackups}",
                    options = listOf("5", "10", "20"),
                    onValueChange = { viewModel.saveSetting(SettingsKeys.MAX_BACKUPS, it.toInt()) }
                )
            }
        }
        item {
            DropdownSetting(
                title = "Recent Files Limit",
                value = "${settings.recentFilesLimit}",
                options = listOf("10", "20", "50", "100"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.RECENT_FILES_LIMIT, it.toInt()) }
            )
        }
        item { SwitchSetting("Show Hidden Files", settings.showHiddenFiles) { viewModel.saveSetting(SettingsKeys.SHOW_HIDDEN_FILES, it) } }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Computer, title = "Terminal") }
        item {
            DropdownSetting(
                title = "Shell",
                value = settings.terminalShell,
                options = listOf("sh", "bash", "zsh"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.TERMINAL_SHELL, it) }
            )
        }
        item {
            SliderSetting(
                title = "Terminal Font Size",
                value = settings.terminalFontSize.toFloat(),
                range = 10f..20f,
                steps = 9,
                display = "${settings.terminalFontSize} sp",
                onValueChange = { viewModel.saveSetting(SettingsKeys.TERMINAL_FONT_SIZE, it.toInt()) }
            )
        }
        item {
            DropdownSetting(
                title = "Scrollback Lines",
                value = "${settings.terminalScrollback}",
                options = listOf("1000", "5000", "10000"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.TERMINAL_SCROLLBACK, it.toInt()) }
            )
        }
        item {
            DropdownSetting(
                title = "History Size",
                value = "${settings.terminalHistory}",
                options = listOf("100", "500", "1000"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.TERMINAL_HISTORY, it.toInt()) }
            )
        }
        item {
            DropdownSetting(
                title = "Terminal Theme",
                value = settings.terminalTheme,
                options = listOf("Dark", "Light", "Green on Black"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.TERMINAL_THEME, it) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.AccountTree, title = "Git Integration") }
        item { SwitchSetting("Enable Git", settings.gitEnabled) { viewModel.saveSetting(SettingsKeys.GIT_ENABLED, it) } }
        item { SwitchSetting("Auto Refresh Status", settings.gitAutoRefresh) { viewModel.saveSetting(SettingsKeys.GIT_AUTO_REFRESH, it) } }
        item {
            DropdownSetting(
                title = "Diff Viewer Theme",
                value = settings.gitDiffTheme,
                options = listOf("Default", "Compact"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.GIT_DIFF_THEME, it) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Build, title = "Developer Tools") }
        item {
            DropdownSetting(
                title = "JSON Formatter Indent",
                value = "${settings.jsonIndent} spaces",
                options = listOf("2 spaces", "4 spaces"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.JSON_INDENT, it.first().digitToInt()) }
            )
        }
        item { SwitchSetting("Regex Highlighting", settings.regexHighlight) { viewModel.saveSetting(SettingsKeys.REGEX_HIGHLIGHT, it) } }
        item {
            DropdownSetting(
                title = "API Request Timeout",
                value = "${settings.apiTimeout} sec",
                options = listOf("10 sec", "30 sec", "60 sec"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.API_TIMEOUT, it.substringBefore(" ").toInt()) }
            )
        }
        item {
            DropdownSetting(
                title = "QR Code Size",
                value = "${settings.qrSize} px",
                options = listOf("200 px", "300 px", "400 px"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.QR_SIZE, it.substringBefore(" ").toInt()) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Extension, title = "Plugins / Extensions") }
        item { SwitchSetting("Enable Plugin System", settings.pluginEnabled) { viewModel.saveSetting(SettingsKeys.PLUGIN_ENABLED, it) } }
        item { SwitchSetting("Auto Update Plugins", settings.autoUpdatePlugins) { viewModel.saveSetting(SettingsKeys.AUTO_UPDATE_PLUGINS, it) } }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Cloud, title = "Cloud & Sync") }
        item { SwitchSetting("Enable Cloud Sync", settings.cloudSync) { viewModel.saveSetting(SettingsKeys.CLOUD_SYNC, it) } }
        if (settings.cloudSync) {
            item {
                DropdownSetting(
                    title = "Provider",
                    value = settings.cloudProvider,
                    options = listOf("Google Drive", "GitHub", "GitLab"),
                    onValueChange = { viewModel.saveSetting(SettingsKeys.CLOUD_PROVIDER, it) }
                )
            }
            item {
                DropdownSetting(
                    title = "Auto Sync Interval",
                    value = settings.syncInterval,
                    options = listOf("Never", "Daily", "Weekly"),
                    onValueChange = { viewModel.saveSetting(SettingsKeys.SYNC_INTERVAL, it) }
                )
            }
            item { SwitchSetting("Sync Over Cellular", settings.syncCellular) { viewModel.saveSetting(SettingsKeys.SYNC_CELLULAR, it) } }
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Lock, title = "Privacy & Security") }
        item { SwitchSetting("Remember Last Files", settings.rememberFiles) { viewModel.saveSetting(SettingsKeys.REMEMBER_FILES, it) } }
        item { SwitchSetting("Clear History on Exit", settings.clearHistoryExit) { viewModel.saveSetting(SettingsKeys.CLEAR_HISTORY_EXIT, it) } }
        item { SwitchSetting("Analytics", settings.analytics) { viewModel.saveSetting(SettingsKeys.ANALYTICS, it) } }
        item { SwitchSetting("Crash Reports", settings.crashReports) { viewModel.saveSetting(SettingsKeys.CRASH_REPORTS, it) } }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Speed, title = "Performance") }
        item {
            DropdownSetting(
                title = "Max File Size to Preview",
                value = "${settings.maxPreviewSize} MB",
                options = listOf("1 MB", "5 MB", "10 MB", "50 MB"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.MAX_PREVIEW_SIZE, it.substringBefore(" ").toInt()) }
            )
        }
        item {
            DropdownSetting(
                title = "Virtual Render Buffer",
                value = "${settings.virtualBuffer} lines",
                options = listOf("100 lines", "500 lines", "1000 lines"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.VIRTUAL_BUFFER, it.substringBefore(" ").toInt()) }
            )
        }
        item { SwitchSetting("Hardware Acceleration", settings.hardwareAccel) { viewModel.saveSetting(SettingsKeys.HARDWARE_ACCEL, it) } }
        item { SwitchSetting("Background Indexing", settings.bgIndexing) { viewModel.saveSetting(SettingsKeys.BG_INDEXING, it) } }
        item {
            DropdownSetting(
                title = "Memory Optimization",
                value = settings.memoryOpt,
                options = listOf("Low", "Medium", "High"),
                onValueChange = { viewModel.saveSetting(SettingsKeys.MEMORY_OPT, it) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Notifications, title = "Notifications") }
        item { SwitchSetting("Build Complete", settings.notifBuild) { viewModel.saveSetting(SettingsKeys.NOTIF_BUILD, it) } }
        item { SwitchSetting("Git Operations", settings.notifGit) { viewModel.saveSetting(SettingsKeys.NOTIF_GIT, it) } }
        item { SwitchSetting("File Changes Detected", settings.notifFileChanges) { viewModel.saveSetting(SettingsKeys.NOTIF_FILE_CHANGES, it) } }
        item { SwitchSetting("Auto Save Confirmation", settings.notifAutoSave) { viewModel.saveSetting(SettingsKeys.NOTIF_AUTO_SAVE, it) } }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.RestartAlt, title = "Backup & Restore") }
        item {
            SettingsButton(
                title = "Reset to Defaults",
                icon = Icons.Default.RestartAlt,
                isDestructive = true,
                onClick = { showResetDialog = true }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { SettingsCategory(icon = Icons.Default.Info, title = "About") }
        item { InfoSetting(title = "App Version", value = "1.0.0") }
        item { Spacer(Modifier.height(32.dp)) }
    }
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings?") },
            text = { Text("Semua pengaturan akan dikembalikan ke default. Tindakan ini tidak bisa dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetToDefaults()
                    showResetDialog = false
                }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun SettingsCategory(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
    Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
}
@Composable
fun SwitchSetting(title: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 14.sp)
            Switch(checked = value, onCheckedChange = onValueChange)
        }
    }
}
@Composable
fun DropdownSetting(
    title: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    leadingIcon = if (option == value) {
                        { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }
                    } else null
                )
            }
        }
    }
}
@Composable
fun SliderSetting(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    display: String,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, fontSize = 14.sp)
                Text(
                    text = display,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = range,
                steps = steps,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Composable
fun SettingsButton(
    title: String,
    icon: ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
@Composable
fun InfoSetting(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 14.sp)
            Text(
                text = value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
