package com.kanaz.script.ui.screens.explorer
import android.Manifest
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FileExplorerScreen(
    onFileSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var currentPath by remember { 
        mutableStateOf(Environment.getExternalStorageDirectory().absolutePath) 
    }
    var files by remember { mutableStateOf<List<File>>(emptyList()) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = permissions.values.all { it }
        if (hasPermission) {
            files = loadFiles(currentPath)
        }
    }
    LaunchedEffect(Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        permissionLauncher.launch(permissions)
    }
    LaunchedEffect(currentPath) {
        if (hasPermission) {
            files = loadFiles(currentPath)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = File(currentPath).name.ifEmpty { "Storage" },
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    if (currentPath != Environment.getExternalStorageDirectory().absolutePath) {
                        IconButton(onClick = {
                            currentPath = File(currentPath).parent ?: currentPath
                        }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Create")
                    }
                    IconButton(onClick = { files = loadFiles(currentPath) }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (!hasPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Storage permission required")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }
                        permissionLauncher.launch(permissions)
                    }) {
                        Text("Grant Permission")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = currentPath,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Divider()
                if (files.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Empty folder")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(files) { file ->
                            FileItem(
                                file = file,
                                onClick = {
                                    if (file.isDirectory) {
                                        currentPath = file.absolutePath
                                    } else {
                                        onFileSelected(file.absolutePath)
                                    }
                                },
                                onLongClick = {
                                    selectedFile = file
                                    showMenu = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    if (showCreateDialog) {
        CreateFileDialog(
            currentPath = currentPath,
            onDismiss = { showCreateDialog = false },
            onCreated = {
                files = loadFiles(currentPath)
                showCreateDialog = false
            }
        )
    }
    if (showMenu && selectedFile != null) {
        FileMenuDialog(
            file = selectedFile!!,
            onDismiss = { 
                showMenu = false
                selectedFile = null
            },
            onAction = {
                files = loadFiles(currentPath)
                showMenu = false
                selectedFile = null
            }
        )
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileItem(
    file: File,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
            contentDescription = null,
            tint = if (file.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.name,
                fontWeight = if (file.isDirectory) FontWeight.Medium else FontWeight.Normal
            )
            Text(
                text = formatFileInfo(file),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFileDialog(
    currentPath: String,
    onDismiss: () -> Unit,
    onCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isFolder by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isFolder) "Create Folder" else "Create File") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isFolder,
                        onCheckedChange = { isFolder = it }
                    )
                    Text("Create as folder")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        try {
                            val newFile = File(currentPath, name)
                            if (isFolder) {
                                newFile.mkdirs()
                            } else {
                                newFile.createNewFile()
                            }
                            onCreated()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun FileMenuDialog(
    file: File,
    onDismiss: () -> Unit,
    onAction: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(file.name) },
        text = {
            Column {
                TextButton(
                    onClick = {
                        try {
                            file.delete()
                            onAction()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
fun loadFiles(path: String): List<File> {
    return try {
        val dir = File(path)
        dir.listFiles()
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
            ?: emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
fun formatFileInfo(file: File): String {
    val date = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        .format(Date(file.lastModified()))
    val size = if (file.isFile) {
        formatFileSize(file.length())
    } else {
        "${file.listFiles()?.size ?: 0} items"
    }
    return "$size • $date"
}
fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
