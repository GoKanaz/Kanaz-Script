package com.kanaz.script.ui.screens.editor
import com.kanaz.script.ui.screens.editor.EditorState
import org.junit.Assert.assertEquals
import org.junit.Test
class EditorTest {
    @Test
    fun testEditorStateInitialization() {
        val state = EditorState()
        assertEquals("", state.content)
        assertEquals("", state.currentFilePath)
        assertEquals(false, state.isLoading)
        assertEquals(false, state.isSaving)
        assertEquals(false, state.hasChanges)
        assertEquals(null, state.error)
        assertEquals(0, state.selectionStart)
        assertEquals(0, state.selectionEnd)
        assertEquals(0, state.scrollPosition)
    }
    @Test
    fun testEditorStateCopy() {
        val initialState = EditorState()
        val updatedState = initialState.copy(
            content = "test",
            hasChanges = true
        )
        assertEquals("test", updatedState.content)
        assertEquals(true, updatedState.hasChanges)
        assertEquals(false, updatedState.isLoading)
    }
}
