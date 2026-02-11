package com.kanaz.script.manager
import com.kanaz.script.data.FileManager
import io.mockk.mockk
import org.junit.Test
import org.junit.Assert.assertNotNull
class FileManagerTest {
    private val mockContext = mockk<android.content.Context>(relaxed = true)
    @Test
    fun testFileManagerCreation() {
        val fileManager = FileManager(mockContext)
        assertNotNull(fileManager)
    }
}
