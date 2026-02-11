package com.kanaz.script.ui.screens.editor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class EditorViewModel @Inject constructor() : ViewModel() {
    private val _editorState = MutableStateFlow(EditorState())
    val editorState: StateFlow<EditorState> = _editorState.asStateFlow()
    fun openFile(filePath: String) {
        viewModelScope.launch {
            _editorState.value = _editorState.value.copy(
                isLoading = true,
                currentFilePath = filePath
            )
            try {
                val content = loadFileContent(filePath)
                _editorState.value = _editorState.value.copy(
                    content = content,
                    isLoading = false,
                    hasChanges = false
                )
            } catch (e: Exception) {
                _editorState.value = _editorState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
    fun updateContent(newContent: String) {
        _editorState.value = _editorState.value.copy(
            content = newContent,
            hasChanges = true
        )
    }
    fun saveFile() {
        viewModelScope.launch {
            _editorState.value = _editorState.value.copy(isSaving = true)
            try {
                saveContentToFile(
                    _editorState.value.currentFilePath,
                    _editorState.value.content
                )
                _editorState.value = _editorState.value.copy(
                    hasChanges = false,
                    isSaving = false
                )
            } catch (e: Exception) {
                _editorState.value = _editorState.value.copy(
                    error = e.message,
                    isSaving = false
                )
            }
        }
    }
    private suspend fun loadFileContent(filePath: String): String {
        return ""
    }
    private suspend fun saveContentToFile(filePath: String, content: String) {
    }
}
data class EditorState(
    val content: String = "",
    val currentFilePath: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val hasChanges: Boolean = false,
    val error: String? = null,
    val selectionStart: Int = 0,
    val selectionEnd: Int = 0,
    val scrollPosition: Int = 0
)
