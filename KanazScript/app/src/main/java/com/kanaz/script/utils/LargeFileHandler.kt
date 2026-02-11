package com.kanaz.script.utils
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.min
object LargeFileHandler {
    private const val CHUNK_SIZE = 8192
    private const val MAX_LINES_PER_CHUNK = 1000
    private const val MAX_MEMORY_SIZE = 10 * 1024 * 1024
    suspend fun readFileInChunks(
        context: Context,
        filePath: String,
        onProgress: (Float) -> Unit
    ): List<String> = withContext(Dispatchers.IO) {
        val file = File(filePath)
        val totalSize = file.length()
        var bytesRead = 0L
        val lines = mutableListOf<String>()
        BufferedReader(file.reader()).use { reader ->
            var line: String?
            var lineCount = 0
            while (reader.readLine().also { line = it } != null) {
                lines.add(line!!)
                lineCount++
                bytesRead += line!!.toByteArray().size
                if (lineCount % 100 == 0) {
                    onProgress(bytesRead.toFloat() / totalSize)
                }
                if (lines.size > 50000) {
                    break
                }
            }
        }
        lines
    }
    suspend fun getLineCount(filePath: String): Int = withContext(Dispatchers.IO) {
        var count = 0
        RandomAccessFile(filePath, "r").use { file ->
            while (file.readLine() != null) {
                count++
                if (count > 100000) break
            }
        }
        count
    }
    suspend fun readLines(
        filePath: String,
        startLine: Int,
        lineCount: Int
    ): List<String> = withContext(Dispatchers.IO) {
        val lines = mutableListOf<String>()
        RandomAccessFile(filePath, "r").use { file ->
            var currentLine = 0
            while (currentLine < startLine + lineCount) {
                val line = file.readLine()
                if (line == null) break
                if (currentLine >= startLine) {
                    lines.add(line)
                }
                currentLine++
            }
        }
        lines
    }
    fun estimateMemoryUsage(lines: List<String>): Long {
        var totalBytes = 0L
        for (line in lines) {
            totalBytes += line.toByteArray().size + 16
        }
        return totalBytes
    }
    fun shouldUseVirtualRendering(fileSize: Long, lineCount: Int): Boolean {
        return fileSize > MAX_MEMORY_SIZE || lineCount > 10000
    }
}
