package com.kanaz.script.ui.screens.settings
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanaz.script.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
object SettingsKeys {
    val FONT_SIZE = intPreferencesKey("font_size")
    val FONT_FAMILY = stringPreferencesKey("font_family")
    val TAB_SIZE = intPreferencesKey("tab_size")
    val LINE_NUMBERS = booleanPreferencesKey("line_numbers")
    val WORD_WRAP = booleanPreferencesKey("word_wrap")
    val AUTO_SAVE = booleanPreferencesKey("auto_save")
    val AUTO_SAVE_INTERVAL = intPreferencesKey("auto_save_interval")
    val HIGHLIGHT_LINE = booleanPreferencesKey("highlight_line")
    val SHOW_WHITESPACE = booleanPreferencesKey("show_whitespace")
    val AUTO_PAIR = booleanPreferencesKey("auto_pair")
    val APP_THEME = stringPreferencesKey("app_theme")
    val EDITOR_SCHEME = stringPreferencesKey("editor_scheme")
    val SYNTAX_HIGHLIGHT = booleanPreferencesKey("syntax_highlight")
    val UI_DENSITY = stringPreferencesKey("ui_density")
    val DEFAULT_SAVE_LOCATION = stringPreferencesKey("save_location")
    val AUTO_BACKUP = booleanPreferencesKey("auto_backup")
    val MAX_BACKUPS = intPreferencesKey("max_backups")
    val RECENT_FILES_LIMIT = intPreferencesKey("recent_files_limit")
    val SHOW_HIDDEN_FILES = booleanPreferencesKey("show_hidden_files")
    val TERMINAL_SHELL = stringPreferencesKey("terminal_shell")
    val TERMINAL_FONT_SIZE = intPreferencesKey("terminal_font_size")
    val TERMINAL_SCROLLBACK = intPreferencesKey("terminal_scrollback")
    val TERMINAL_HISTORY = intPreferencesKey("terminal_history")
    val TERMINAL_THEME = stringPreferencesKey("terminal_theme")
    val GIT_ENABLED = booleanPreferencesKey("git_enabled")
    val GIT_AUTO_REFRESH = booleanPreferencesKey("git_auto_refresh")
    val GIT_DIFF_THEME = stringPreferencesKey("git_diff_theme")
    val GIT_COMMIT_TEMPLATE = stringPreferencesKey("git_commit_template")
    val JSON_INDENT = intPreferencesKey("json_indent")
    val REGEX_HIGHLIGHT = booleanPreferencesKey("regex_highlight")
    val API_TIMEOUT = intPreferencesKey("api_timeout")
    val QR_SIZE = intPreferencesKey("qr_size")
    val PLUGIN_ENABLED = booleanPreferencesKey("plugin_enabled")
    val AUTO_UPDATE_PLUGINS = booleanPreferencesKey("auto_update_plugins")
    val CLOUD_SYNC = booleanPreferencesKey("cloud_sync")
    val CLOUD_PROVIDER = stringPreferencesKey("cloud_provider")
    val SYNC_INTERVAL = stringPreferencesKey("sync_interval")
    val SYNC_CELLULAR = booleanPreferencesKey("sync_cellular")
    val REMEMBER_FILES = booleanPreferencesKey("remember_files")
    val CLEAR_HISTORY_EXIT = booleanPreferencesKey("clear_history_exit")
    val ANALYTICS = booleanPreferencesKey("analytics")
    val CRASH_REPORTS = booleanPreferencesKey("crash_reports")
    val MAX_PREVIEW_SIZE = intPreferencesKey("max_preview_size")
    val VIRTUAL_BUFFER = intPreferencesKey("virtual_buffer")
    val HARDWARE_ACCEL = booleanPreferencesKey("hardware_accel")
    val BG_INDEXING = booleanPreferencesKey("bg_indexing")
    val MEMORY_OPT = stringPreferencesKey("memory_opt")
    val NOTIF_BUILD = booleanPreferencesKey("notif_build")
    val NOTIF_GIT = booleanPreferencesKey("notif_git")
    val NOTIF_FILE_CHANGES = booleanPreferencesKey("notif_file_changes")
    val NOTIF_AUTO_SAVE = booleanPreferencesKey("notif_auto_save")
}
data class AppSettings(
    val fontSize: Int = 14,
    val fontFamily: String = "Monospace",
    val tabSize: Int = 4,
    val lineNumbers: Boolean = true,
    val wordWrap: Boolean = false,
    val autoSave: Boolean = false,
    val autoSaveInterval: Int = 3,
    val highlightLine: Boolean = true,
    val showWhitespace: Boolean = false,
    val autoPair: Boolean = true,
    val appTheme: String = "System",
    val editorScheme: String = "Dark",
    val syntaxHighlight: Boolean = true,
    val uiDensity: String = "Standard",
    val defaultSaveLocation: String = "Internal",
    val autoBackup: Boolean = true,
    val maxBackups: Int = 10,
    val recentFilesLimit: Int = 20,
    val showHiddenFiles: Boolean = false,
    val terminalShell: String = "sh",
    val terminalFontSize: Int = 14,
    val terminalScrollback: Int = 1000,
    val terminalHistory: Int = 500,
    val terminalTheme: String = "Dark",
    val gitEnabled: Boolean = true,
    val gitAutoRefresh: Boolean = true,
    val gitDiffTheme: String = "Default",
    val gitCommitTemplate: String = "",
    val jsonIndent: Int = 2,
    val regexHighlight: Boolean = true,
    val apiTimeout: Int = 30,
    val qrSize: Int = 300,
    val pluginEnabled: Boolean = false,
    val autoUpdatePlugins: Boolean = false,
    val cloudSync: Boolean = false,
    val cloudProvider: String = "GitHub",
    val syncInterval: String = "Never",
    val syncCellular: Boolean = false,
    val rememberFiles: Boolean = true,
    val clearHistoryExit: Boolean = false,
    val analytics: Boolean = false,
    val crashReports: Boolean = false,
    val maxPreviewSize: Int = 10,
    val virtualBuffer: Int = 500,
    val hardwareAccel: Boolean = true,
    val bgIndexing: Boolean = true,
    val memoryOpt: String = "Medium",
    val notifBuild: Boolean = true,
    val notifGit: Boolean = true,
    val notifFileChanges: Boolean = false,
    val notifAutoSave: Boolean = false
)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    init {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _settings.value = AppSettings(
                    fontSize = prefs[SettingsKeys.FONT_SIZE] ?: 14,
                    fontFamily = prefs[SettingsKeys.FONT_FAMILY] ?: "Monospace",
                    tabSize = prefs[SettingsKeys.TAB_SIZE] ?: 4,
                    lineNumbers = prefs[SettingsKeys.LINE_NUMBERS] ?: true,
                    wordWrap = prefs[SettingsKeys.WORD_WRAP] ?: false,
                    autoSave = prefs[SettingsKeys.AUTO_SAVE] ?: false,
                    autoSaveInterval = prefs[SettingsKeys.AUTO_SAVE_INTERVAL] ?: 3,
                    highlightLine = prefs[SettingsKeys.HIGHLIGHT_LINE] ?: true,
                    showWhitespace = prefs[SettingsKeys.SHOW_WHITESPACE] ?: false,
                    autoPair = prefs[SettingsKeys.AUTO_PAIR] ?: true,
                    appTheme = prefs[SettingsKeys.APP_THEME] ?: "System",
                    editorScheme = prefs[SettingsKeys.EDITOR_SCHEME] ?: "Dark",
                    syntaxHighlight = prefs[SettingsKeys.SYNTAX_HIGHLIGHT] ?: true,
                    uiDensity = prefs[SettingsKeys.UI_DENSITY] ?: "Standard",
                    defaultSaveLocation = prefs[SettingsKeys.DEFAULT_SAVE_LOCATION] ?: "Internal",
                    autoBackup = prefs[SettingsKeys.AUTO_BACKUP] ?: true,
                    maxBackups = prefs[SettingsKeys.MAX_BACKUPS] ?: 10,
                    recentFilesLimit = prefs[SettingsKeys.RECENT_FILES_LIMIT] ?: 20,
                    showHiddenFiles = prefs[SettingsKeys.SHOW_HIDDEN_FILES] ?: false,
                    terminalShell = prefs[SettingsKeys.TERMINAL_SHELL] ?: "sh",
                    terminalFontSize = prefs[SettingsKeys.TERMINAL_FONT_SIZE] ?: 14,
                    terminalScrollback = prefs[SettingsKeys.TERMINAL_SCROLLBACK] ?: 1000,
                    terminalHistory = prefs[SettingsKeys.TERMINAL_HISTORY] ?: 500,
                    terminalTheme = prefs[SettingsKeys.TERMINAL_THEME] ?: "Dark",
                    gitEnabled = prefs[SettingsKeys.GIT_ENABLED] ?: true,
                    gitAutoRefresh = prefs[SettingsKeys.GIT_AUTO_REFRESH] ?: true,
                    gitDiffTheme = prefs[SettingsKeys.GIT_DIFF_THEME] ?: "Default",
                    gitCommitTemplate = prefs[SettingsKeys.GIT_COMMIT_TEMPLATE] ?: "",
                    jsonIndent = prefs[SettingsKeys.JSON_INDENT] ?: 2,
                    regexHighlight = prefs[SettingsKeys.REGEX_HIGHLIGHT] ?: true,
                    apiTimeout = prefs[SettingsKeys.API_TIMEOUT] ?: 30,
                    qrSize = prefs[SettingsKeys.QR_SIZE] ?: 300,
                    pluginEnabled = prefs[SettingsKeys.PLUGIN_ENABLED] ?: false,
                    autoUpdatePlugins = prefs[SettingsKeys.AUTO_UPDATE_PLUGINS] ?: false,
                    cloudSync = prefs[SettingsKeys.CLOUD_SYNC] ?: false,
                    cloudProvider = prefs[SettingsKeys.CLOUD_PROVIDER] ?: "GitHub",
                    syncInterval = prefs[SettingsKeys.SYNC_INTERVAL] ?: "Never",
                    syncCellular = prefs[SettingsKeys.SYNC_CELLULAR] ?: false,
                    rememberFiles = prefs[SettingsKeys.REMEMBER_FILES] ?: true,
                    clearHistoryExit = prefs[SettingsKeys.CLEAR_HISTORY_EXIT] ?: false,
                    analytics = prefs[SettingsKeys.ANALYTICS] ?: false,
                    crashReports = prefs[SettingsKeys.CRASH_REPORTS] ?: false,
                    maxPreviewSize = prefs[SettingsKeys.MAX_PREVIEW_SIZE] ?: 10,
                    virtualBuffer = prefs[SettingsKeys.VIRTUAL_BUFFER] ?: 500,
                    hardwareAccel = prefs[SettingsKeys.HARDWARE_ACCEL] ?: true,
                    bgIndexing = prefs[SettingsKeys.BG_INDEXING] ?: true,
                    memoryOpt = prefs[SettingsKeys.MEMORY_OPT] ?: "Medium",
                    notifBuild = prefs[SettingsKeys.NOTIF_BUILD] ?: true,
                    notifGit = prefs[SettingsKeys.NOTIF_GIT] ?: true,
                    notifFileChanges = prefs[SettingsKeys.NOTIF_FILE_CHANGES] ?: false,
                    notifAutoSave = prefs[SettingsKeys.NOTIF_AUTO_SAVE] ?: false
                )
            }
        }
    }
    fun <T> saveSetting(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            context.dataStore.edit { prefs -> prefs[key] = value }
        }
    }
    fun resetToDefaults() {
        viewModelScope.launch {
            context.dataStore.edit { it.clear() }
        }
    }
}
