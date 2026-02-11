package com.kanaz.script.data
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
class FileManager(private val context: Context) {
    private val storageDirs = mutableListOf<File>()
    init {
        updateStorageDirs()
    }
    private fun updateStorageDirs() {
        storageDirs.clear()
        storageDirs.add(context.filesDir)
        storageDirs.add(context.externalCacheDir ?: File("/storage/emulated/0/Android/data/${context.packageName}/cache"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getExternalFilesDirs(null).forEach {
                if (it != null) storageDirs.add(it)
            }
        }
    }
    suspend fun readFile(filePath: String): String = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) throw IllegalArgumentException("File not found")
        if (file.length() > 10 * 1024 * 1024) throw IllegalStateException("File too large")
        file.readText(Charsets.UTF_8)
    }
    suspend fun writeFile(filePath: String, content: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.writeText(content, Charsets.UTF_8)
            true
        } catch (e: Exception) {
            false
        }
    }
    suspend fun listFiles(directory: String): List<FileItem> = withContext(Dispatchers.IO) {
        val dir = File(directory)
        if (!dir.exists() || !dir.isDirectory) return@withContext emptyList()
        dir.listFiles()?.map { file ->
            FileItem(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory,
                size = file.length(),
                modified = file.lastModified(),
                extension = if (file.isFile) {
                    file.name.substringAfterLast(".", "")
                } else {
                    ""
                }
            )
        } ?: emptyList()
    }
    suspend fun createDirectory(path: String): Boolean = withContext(Dispatchers.IO) {
        val dir = File(path)
        dir.mkdirs()
    }
    suspend fun deleteFile(path: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
    suspend fun renameFile(oldPath: String, newPath: String): Boolean = withContext(Dispatchers.IO) {
        val oldFile = File(oldPath)
        val newFile = File(newPath)
        oldFile.renameTo(newFile)
    }
    suspend fun copyFile(srcPath: String, destPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val srcFile = File(srcPath)
            val destFile = File(destPath)
            if (!destFile.parentFile.exists()) {
                destFile.parentFile.mkdirs()
            }
            srcFile.copyTo(destFile, overwrite = true)
            true
        } catch (e: Exception) {
            false
        }
    }
    fun getStorageRoots(): List<File> {
        return storageDirs
    }
    fun getDefaultProjectDir(): File {
        return File(Environment.getExternalStorageDirectory(), "KanazScript")
    }
}
data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val modified: Long,
    val extension: String
)
