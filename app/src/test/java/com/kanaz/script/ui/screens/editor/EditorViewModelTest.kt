package com.kanaz.script.ui.screens.editor
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
@ExperimentalCoroutinesApi
class EditorViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: EditorViewModel
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EditorViewModel()
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
    fun `initial state should be empty`() = runTest {
        val state = viewModel.editorState.value
        assertTrue(state.content.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.hasChanges)
    }
    @Test
    fun `update content should set hasChanges to true`() = runTest {
        viewModel.updateContent("test content")
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.editorState.value
        assertEquals("test content", state.content)
        assertTrue(state.hasChanges)
    }
}
