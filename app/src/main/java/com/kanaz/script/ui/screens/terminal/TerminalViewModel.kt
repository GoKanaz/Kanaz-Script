package com.kanaz.script.ui.screens.terminal
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanaz.script.dataStore
import com.kanaz.script.ui.screens.settings.SettingsKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
data class TerminalLine(
    val text: String,
    val type: LineType = LineType.OUTPUT
)
enum class LineType { COMMAND, OUTPUT, ERROR, INFO }
data class TerminalState(
    val lines: List<TerminalLine> = listOf(
        TerminalLine("Kanaz Script Terminal", LineType.INFO),
        TerminalLine("Type 'help' for available commands", LineType.INFO),
        TerminalLine("", LineType.INFO)
    ),
    val currentInput: String = "",
    val isRunning: Boolean = false,
    val currentDirectory: String = "/storage/emulated/0",
    val shell: String = "sh",
    val fontSize: Int = 14,
    val terminalTheme: String = "Dark"
)
@HiltViewModel
class TerminalViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(TerminalState())
    val state: StateFlow<TerminalState> = _state.asStateFlow()
    private val commandHistory = mutableListOf<String>()
    private var historyIndex = -1
    private var maxHistory = 500
    private var maxScrollback = 1000
    init {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _state.update { it.copy(
                    shell = prefs[SettingsKeys.TERMINAL_SHELL] ?: "sh",
                    fontSize = prefs[SettingsKeys.TERMINAL_FONT_SIZE] ?: 14,
                    terminalTheme = prefs[SettingsKeys.TERMINAL_THEME] ?: "Dark"
                )}
                maxHistory = prefs[SettingsKeys.TERMINAL_HISTORY] ?: 500
                maxScrollback = prefs[SettingsKeys.TERMINAL_SCROLLBACK] ?: 1000
            }
        }
    }
    fun updateInput(input: String) {
        _state.update { it.copy(currentInput = input) }
        historyIndex = -1
    }
    fun historyUp() {
        if (commandHistory.isEmpty()) return
        historyIndex = (historyIndex + 1).coerceAtMost(commandHistory.size - 1)
        _state.update { it.copy(currentInput = commandHistory[historyIndex]) }
    }
    fun historyDown() {
        if (historyIndex <= 0) {
            historyIndex = -1
            _state.update { it.copy(currentInput = "") }
            return
        }
        historyIndex--
        _state.update { it.copy(currentInput = commandHistory[historyIndex]) }
    }
    fun executeCommand(command: String) {
        if (command.isBlank()) return
        val trimmed = command.trim()
        if (commandHistory.isEmpty() || commandHistory.first() != trimmed) {
            commandHistory.add(0, trimmed)
            if (commandHistory.size > maxHistory) commandHistory.removeLast()
        }
        historyIndex = -1
        appendLine("${_state.value.currentDirectory} $ $trimmed", LineType.COMMAND)
        _state.update { it.copy(currentInput = "", isRunning = true) }
        when (trimmed.lowercase()) {
            "help" -> showHelp()
            "clear", "cls" -> clearTerminal()
            "history" -> showHistory()
            else -> {
                if (trimmed.startsWith("cd ")) {
                    changeDirectory(trimmed.removePrefix("cd ").trim())
                } else {
                    runShellCommand(trimmed)
                }
            }
        }
    }
    private fun showHelp() {
        val helpText = listOf(
            "Available Commands:",
            "  help          - Show this help",
            "  clear / cls   - Clear terminal",
            "  history       - Show command history",
            "  cd <path>     - Change directory",
            "  ls / dir      - List files",
            "  pwd           - Print working directory",
            "  cat <file>    - Print file content",
            "  mkdir <dir>   - Create directory",
            "  rm <file>     - Remove file",
            "  cp <src> <dst>- Copy file",
            "  mv <src> <dst>- Move/rename file",
            "  echo <text>   - Print text",
            "  whoami        - Show current user",
            "  date          - Show current date"
        )
        helpText.forEach { appendLine(it, LineType.INFO) }
        _state.update { it.copy(isRunning = false) }
    }
    private fun showHistory() {
        if (commandHistory.isEmpty()) {
            appendLine("No history", LineType.INFO)
        } else {
            commandHistory.reversed().forEachIndexed { index, cmd ->
                appendLine("  ${index + 1}  $cmd", LineType.OUTPUT)
            }
        }
        _state.update { it.copy(isRunning = false) }
    }
    private fun clearTerminal() {
        _state.update { it.copy(
            lines = listOf(TerminalLine("Terminal cleared", LineType.INFO)),
            isRunning = false
        )}
    }
    private fun changeDirectory(path: String) {
        viewModelScope.launch {
            val newPath = when {
                path == "~" -> "/storage/emulated/0"
                path == ".." -> File(_state.value.currentDirectory).parent ?: _state.value.currentDirectory
                path.startsWith("/") -> path
                else -> "${_state.value.currentDirectory}/$path"
            }
            val dir = File(newPath)
            if (dir.exists() && dir.isDirectory) {
                _state.update { it.copy(currentDirectory = dir.canonicalPath, isRunning = false) }
            } else {
                appendLine("cd: $path: No such file or directory", LineType.ERROR)
                _state.update { it.copy(isRunning = false) }
            }
        }
    }
    private fun runShellCommand(command: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val shell = _state.value.shell
                    val process = ProcessBuilder(shell, "-c", command)
                        .directory(File(_state.value.currentDirectory))
                        .redirectErrorStream(false)
                        .start()
                    val stdout = BufferedReader(InputStreamReader(process.inputStream))
                    val stderr = BufferedReader(InputStreamReader(process.errorStream))
                    val outputLines = mutableListOf<String>()
                    val errorLines = mutableListOf<String>()
                    stdout.forEachLine { outputLines.add(it) }
                    stderr.forEachLine { errorLines.add(it) }
                    process.waitFor()
                    withContext(Dispatchers.Main) {
                        outputLines.forEach { appendLine(it, LineType.OUTPUT) }
                        errorLines.forEach { appendLine(it, LineType.ERROR) }
                        if (outputLines.isEmpty() && errorLines.isEmpty()) {
                            appendLine("(no output)", LineType.INFO)
                        }
                        _state.update { it.copy(isRunning = false) }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        appendLine("Error: ${e.message}", LineType.ERROR)
                        _state.update { it.copy(isRunning = false) }
                    }
                }
            }
        }
    }
    private fun appendLine(text: String, type: LineType) {
        val currentLines = _state.value.lines.toMutableList()
        currentLines.add(TerminalLine(text, type))
        if (currentLines.size > maxScrollback) {
            currentLines.removeAt(0)
        }
        _state.update { it.copy(lines = currentLines) }
    }
}
