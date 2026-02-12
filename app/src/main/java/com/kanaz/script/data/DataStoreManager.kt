package com.kanaz.script.data
import android.content.Context
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class DataStoreManager(private val context: Context) {
    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val FONT_SIZE = intPreferencesKey("font_size")
        private val SHOW_LINE_NUMBERS = booleanPreferencesKey("show_line_numbers")
        private val WORD_WRAP = booleanPreferencesKey("word_wrap")
        private val AUTO_SAVE = booleanPreferencesKey("auto_save")
        private val RECENT_FILES_LIMIT = intPreferencesKey("recent_files_limit")
        private val DEFAULT_FONT_SIZE = 14
        private val DEFAULT_RECENT_LIMIT = 50
    }
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[THEME_MODE] = mode }
    }
    val themeMode: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[THEME_MODE] ?: "auto" }
    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { prefs -> prefs[FONT_SIZE] = size }
    }
    val fontSize: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[FONT_SIZE] ?: DEFAULT_FONT_SIZE }
    suspend fun setShowLineNumbers(show: Boolean) {
        context.dataStore.edit { prefs -> prefs[SHOW_LINE_NUMBERS] = show }
    }
    val showLineNumbers: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[SHOW_LINE_NUMBERS] ?: true }
    suspend fun setWordWrap(wrap: Boolean) {
        context.dataStore.edit { prefs -> prefs[WORD_WRAP] = wrap }
    }
    val wordWrap: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[WORD_WRAP] ?: false }
    suspend fun setAutoSave(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[AUTO_SAVE] = enabled }
    }
    val autoSave: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[AUTO_SAVE] ?: true }
    suspend fun setRecentFilesLimit(limit: Int) {
        context.dataStore.edit { prefs -> prefs[RECENT_FILES_LIMIT] = limit }
    }
    val recentFilesLimit: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[RECENT_FILES_LIMIT] ?: DEFAULT_RECENT_LIMIT }
}
