package com.kanaz.script.data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.RandomAccessFile
class LargeFileHandler {
    companion object {
        private const val MAX_LINES_IN_MEMORY = 5000
        private const val CHUNK_SIZE = 1024 * 1024
        fun readLargeFileByChunks(
            filePath: String,
            chunkSize: Int = CHUNK_SIZE
        ): Flow<String> = flow {
            RandomAccessFile(filePath, "r").use { file ->
                val buffer = ByteArray(chunkSize)
                var position = 0L
                var bytesRead: Int
                do {
                    bytesRead = file.read(buffer)
                    if (bytesRead > 0) {
                        val chunk = String(buffer, 0, bytesRead)
                        emit(chunk)
                        position += bytesRead
                    }
                } while (bytesRead == chunkSize)
            }
        }
        fun getLineCount(filePath: String): Long {
            RandomAccessFile(filePath, "r").use { file ->
                var lines = 0L
                var byte: Int
                while (true) {
                    byte = file.read()
                    if (byte == -1) break
                    if (byte.toChar() == '\n') lines++
                }
                return lines
            }
        }
        fun getLinesAroundPosition(
            filePath: String,
            lineNumber: Long,
            contextLines: Int = 100
        ): Pair<List<String>, Long> {
            val lines = mutableListOf<String>()
            RandomAccessFile(filePath, "r").use { file ->
                var currentLine = 0L
                var line: String?
                while (true) {
                    line = file.readLine()
                    if (line == null) break
                    if (currentLine >= lineNumber - contextLines &&
                        currentLine <= lineNumber + contextLines) {
                        lines.add(line)
                    }
                    if (currentLine > lineNumber + contextLines) break
                    currentLine++
                }
                return Pair(lines, lineNumber)
            }
        }
    }
}
