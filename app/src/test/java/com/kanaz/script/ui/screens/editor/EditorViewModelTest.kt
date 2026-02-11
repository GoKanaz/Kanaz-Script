package com.kanaz.script.ui.screens.editor
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
@ExperimentalCoroutinesApi
class EditorViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var viewModel: EditorViewModel
    @Before
    fun setup() {
        viewModel = EditorViewModel()
    }
    @Test
    fun `initial state should be empty`() = runTest {
        val state = viewModel.editorState.value
        assert(state.content.isEmpty())
        assert(!state.isLoading)
        assert(!state.hasChanges)
    }
    @Test
    fun `update content should set hasChanges to true`() = runTest {
        viewModel.updateContent("test content")
        val state = viewModel.editorState.value
        assert(state.content == "test content")
        assert(state.hasChanges)
    }
}
