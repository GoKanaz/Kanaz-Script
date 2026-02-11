package com.kanaz.script.utils
import org.junit.Test
import org.junit.Assert.assertTrue
class LargeFileHandlerTest {
    @Test
    fun `shouldUseVirtualRendering should return true for large files`() {
        val largeFile = 15 * 1024 * 1024L
        val manyLines = 15000
        assertTrue(LargeFileHandler.shouldUseVirtualRendering(largeFile, 100))
        assertTrue(LargeFileHandler.shouldUseVirtualRendering(1000L, manyLines))
    }
    @Test
    fun `estimateMemoryUsage should calculate correctly`() {
        val lines = listOf(
            "Hello, World!",
            "This is a test",
            "Another line"
        )
        val usage = LargeFileHandler.estimateMemoryUsage(lines)
        assertTrue(usage > 0)
    }
}
