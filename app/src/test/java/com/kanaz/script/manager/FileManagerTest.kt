package com.kanaz.script.manager
import com.kanaz.script.data.FileManager
import io.mockk.mockk
import org.junit.Test
import org.junit.Assert.assertEquals
class FileManagerTest {
    private val mockContext = mockk<android.content.Context>(relaxed = true)
    @Test
    fun testFileManagerCreation() {
        val fileManager = FileManager(mockContext)
        assert(fileManager != null)
    }
    @Test
    fun testFileItemStructure() {
        val item = FileManager.FileItem(
            name = "test.txt",
            path = "/test.txt",
            isDirectory = false,
            size = 100,
            modified = 123456789,
            depth = 0
        )
        assertEquals("test.txt", item.name)
        assertEquals("/test.txt", item.path)
        assertEquals(false, item.isDirectory)
        assertEquals(100, item.size)
        assertEquals(123456789, item.modified)
        assertEquals(0, item.depth)
    }
}
