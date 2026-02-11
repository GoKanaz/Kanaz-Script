package com.kanaz.script
import com.kanaz.script.data.LargeFileHandler
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue
class LargeFileHandlerTest {
    @Test
    fun testLineCount() {
        val tempFile = File.createTempFile("test", ".txt")
        tempFile.writeText("Line 1\nLine 2\nLine 3\nLine 4\nLine 5")
        val lineCount = LargeFileHandler.getLineCount(tempFile.absolutePath)
        assertEquals(5, lineCount)
        tempFile.delete()
    }
    @Test
    fun testLinesAroundPosition() {
        val tempFile = File.createTempFile("test", ".txt")
        val content = StringBuilder()
        for (i in 1..200) {
            content.append("Line $i\n")
        }
        tempFile.writeText(content.toString())
        val (lines, position) = LargeFileHandler.getLinesAroundPosition(
            tempFile.absolutePath,
            100,
            10
        )
        assertTrue(lines.size <= 21)
        assertEquals(100, position)
        tempFile.delete()
    }
}
