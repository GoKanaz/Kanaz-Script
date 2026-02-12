package com.kanaz.script.ui.screens.tools
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanaz.script.data.GitManager
import com.kanaz.script.data.GitStatus
import com.kanaz.script.dataStore
import com.kanaz.script.ui.screens.settings.SettingsKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import javax.inject.Inject
data class ToolsState(
    val repoPath: String = "",
    val isGitEnabled: Boolean = true,
    val isGitRepo: Boolean = false,
    val gitStatus: List<GitStatus> = emptyList(),
    val branches: List<String> = emptyList(),
    val currentBranch: String = "",
    val commitMessage: String = "",
    val isLoading: Boolean = false,
    val message: String = "",
    val isError: Boolean = false,
    val findText: String = "",
    val replaceText: String = "",
    val findResults: List<FindResult> = emptyList(),
    val jsonIndent: Int = 2,
    val autoRefresh: Boolean = true
)
data class FindResult(
    val filePath: String,
    val lineNumber: Int,
    val lineContent: String,
    val matchStart: Int,
    val matchEnd: Int
)
@HiltViewModel
class ToolsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(ToolsState())
    val state: StateFlow<ToolsState> = _state.asStateFlow()
    private val gitManager = GitManager(context)
    init {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                val gitEnabled = prefs[SettingsKeys.GIT_ENABLED] ?: true
                val autoRefresh = prefs[SettingsKeys.GIT_AUTO_REFRESH] ?: true
                val jsonIndent = prefs[SettingsKeys.JSON_INDENT] ?: 2
                val commitTemplate = prefs[SettingsKeys.GIT_COMMIT_TEMPLATE] ?: ""
                _state.update { it.copy(
                    isGitEnabled = gitEnabled,
                    autoRefresh = autoRefresh,
                    jsonIndent = jsonIndent,
                    commitMessage = if (it.commitMessage.isEmpty()) commitTemplate else it.commitMessage
                )}
            }
        }
    }
    fun setRepoPath(path: String) {
        viewModelScope.launch {
            _state.update { it.copy(repoPath = path, isLoading = true) }
            val isRepo = File(path, ".git").exists()
            _state.update { it.copy(isGitRepo = isRepo, isLoading = false) }
            if (isRepo) {
                refreshGitStatus()
                loadBranches()
                loadCurrentBranch()
            }
        }
    }
    fun refreshGitStatus() {
        viewModelScope.launch {
            val path = _state.value.repoPath
            if (path.isEmpty()) return@launch
            _state.update { it.copy(isLoading = true) }
            val status = gitManager.getStatus(path)
            _state.update { it.copy(gitStatus = status, isLoading = false) }
        }
    }
    private fun loadBranches() {
        viewModelScope.launch {
            val branches = gitManager.getBranches(_state.value.repoPath)
            _state.update { it.copy(branches = branches) }
        }
    }
    private fun loadCurrentBranch() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val git = Git.open(File(_state.value.repoPath))
                    val branch = git.repository.branch
                    _state.update { it.copy(currentBranch = branch) }
                } catch (e: Exception) {
                    _state.update { it.copy(currentBranch = "unknown") }
                }
            }
        }
    }
    fun updateCommitMessage(msg: String) {
        _state.update { it.copy(commitMessage = msg) }
    }
    fun gitAdd() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                try {
                    val git = Git.open(File(_state.value.repoPath))
                    git.add().addFilepattern(".").call()
                    withContext(Dispatchers.Main) {
                        showMessage("All files staged successfully")
                        refreshGitStatus()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showMessage("Add failed: ${e.message}", isError = true)
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
    fun gitCommit() {
        val msg = _state.value.commitMessage
        if (msg.isBlank()) {
            showMessage("Commit message cannot be empty", isError = true)
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val success = gitManager.commit(_state.value.repoPath, msg)
            if (success) {
                showMessage("Committed: $msg")
                _state.update { it.copy(commitMessage = "") }
                refreshGitStatus()
            } else {
                showMessage("Commit failed", isError = true)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
    fun gitPush(username: String = "", password: String = "") {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                try {
                    val git = Git.open(File(_state.value.repoPath))
                    val pushCmd = git.push()
                    if (username.isNotBlank() && password.isNotBlank()) {
                        pushCmd.setCredentialsProvider(
                            UsernamePasswordCredentialsProvider(username, password)
                        )
                    }
                    pushCmd.call()
                    withContext(Dispatchers.Main) {
                        showMessage("Push successful!")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showMessage("Push failed: ${e.message}", isError = true)
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
    fun gitPull(username: String = "", password: String = "") {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                try {
                    val git = Git.open(File(_state.value.repoPath))
                    val pullCmd = git.pull()
                    if (username.isNotBlank() && password.isNotBlank()) {
                        pullCmd.setCredentialsProvider(
                            UsernamePasswordCredentialsProvider(username, password)
                        )
                    }
                    pullCmd.call()
                    withContext(Dispatchers.Main) {
                        showMessage("Pull successful!")
                        refreshGitStatus()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showMessage("Pull failed: ${e.message}", isError = true)
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
    fun gitInit() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val success = gitManager.initRepository(_state.value.repoPath)
            if (success) {
                showMessage("Git repository initialized!")
                _state.update { it.copy(isGitRepo = true) }
                loadBranches()
            } else {
                showMessage("Init failed", isError = true)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
    fun checkoutBranch(branch: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val success = gitManager.checkoutBranch(_state.value.repoPath, branch)
            if (success) {
                showMessage("Switched to $branch")
                _state.update { it.copy(currentBranch = branch) }
                refreshGitStatus()
            } else {
                showMessage("Checkout failed", isError = true)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
    fun updateFindText(text: String) {
        _state.update { it.copy(findText = text, findResults = emptyList()) }
    }
    fun updateReplaceText(text: String) {
        _state.update { it.copy(replaceText = text) }
    }
    fun findInFiles(directory: String, useRegex: Boolean = false) {
        val query = _state.value.findText
        if (query.isBlank()) {
            showMessage("Enter text to search", isError = true)
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, findResults = emptyList()) }
            withContext(Dispatchers.IO) {
                val results = mutableListOf<FindResult>()
                val dir = File(directory)
                findInDirectory(dir, query, useRegex, results)
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(findResults = results, isLoading = false) }
                    if (results.isEmpty()) showMessage("No results found")
                    else showMessage("Found ${results.size} result(s)")
                }
            }
        }
    }
    private fun findInDirectory(dir: File, query: String, useRegex: Boolean, results: MutableList<FindResult>) {
        if (!dir.exists() || !dir.canRead()) return
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory && !file.name.startsWith(".")) {
                findInDirectory(file, query, useRegex, results)
            } else if (file.isFile && file.length() < 5 * 1024 * 1024) {
                try {
                    val lines = file.readLines()
                    lines.forEachIndexed { index, line ->
                        val matchRange = if (useRegex) {
                            Regex(query).find(line)?.range
                        } else {
                            val start = line.indexOf(query, ignoreCase = true)
                            if (start >= 0) start until start + query.length else null
                        }
                        if (matchRange != null) {
                            results.add(FindResult(
                                filePath = file.absolutePath,
                                lineNumber = index + 1,
                                lineContent = line.trim(),
                                matchStart = matchRange.first,
                                matchEnd = matchRange.last
                            ))
                        }
                    }
                } catch (e: Exception) { }
            }
        }
    }
    fun replaceInFile(filePath: String) {
        val find = _state.value.findText
        val replace = _state.value.replaceText
        if (find.isBlank()) {
            showMessage("Enter text to find", isError = true)
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                try {
                    val file = File(filePath)
                    val content = file.readText()
                    val newContent = content.replace(find, replace, ignoreCase = true)
                    file.writeText(newContent)
                    withContext(Dispatchers.Main) {
                        showMessage("Replace successful in ${file.name}")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showMessage("Replace failed: ${e.message}", isError = true)
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
    fun formatJson(input: String): String {
        return try {
            val indent = _state.value.jsonIndent
            val obj = org.json.JSONObject(input)
            obj.toString(indent)
        } catch (e: Exception) {
            try {
                val arr = org.json.JSONArray(input)
                arr.toString(_state.value.jsonIndent)
            } catch (e2: Exception) {
                "Invalid JSON: ${e2.message}"
            }
        }
    }
    fun convertFile(sourcePath: String, targetExtension: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                try {
                    val source = File(sourcePath)
                    val targetPath = sourcePath.substringBeforeLast(".") + ".$targetExtension"
                    val content = source.readText()
                    File(targetPath).writeText(content)
                    withContext(Dispatchers.Main) {
                        showMessage("Converted to $targetExtension: ${File(targetPath).name}")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showMessage("Conversion failed: ${e.message}", isError = true)
                    }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
    private fun showMessage(msg: String, isError: Boolean = false) {
        _state.update { it.copy(message = msg, isError = isError) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _state.update { it.copy(message = "") }
        }
    }
    fun clearMessage() {
        _state.update { it.copy(message = "") }
    }
}
