package com.kanaz.script.data
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import java.io.File
class GitManager(private val context: Context) {
    suspend fun initRepository(path: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val dir = File(path)
            Git.init().setDirectory(dir).call()
            true
        } catch (e: Exception) {
            false
        }
    }
    suspend fun getRepository(path: String): Git? = withContext(Dispatchers.IO) {
        try {
            val dir = File(path)
            if (File(dir, ".git").exists()) {
                Git.open(dir)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getStatus(path: String): List<GitStatus> = withContext(Dispatchers.IO) {
        val git = getRepository(path) ?: return@withContext emptyList()
        val status = git.status().call()
        val result = mutableListOf<GitStatus>()
        status.added.forEach { result.add(GitStatus(it, GitStatusType.ADDED)) }
        status.changed.forEach { result.add(GitStatus(it, GitStatusType.MODIFIED)) }
        status.removed.forEach { result.add(GitStatus(it, GitStatusType.DELETED)) }
        status.missing.forEach { result.add(GitStatus(it, GitStatusType.MISSING)) }
        result
    }
    suspend fun commit(path: String, message: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val git = getRepository(path) ?: return@withContext false
            git.add().addFilepattern(".").call()
            git.commit().setMessage(message).call()
            true
        } catch (e: Exception) {
            false
        }
    }
    suspend fun getBranches(path: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val git = getRepository(path) ?: return@withContext emptyList()
            git.branchList().call().map { it.name }
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun checkoutBranch(path: String, branchName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val git = getRepository(path) ?: return@withContext false
            git.checkout().setName(branchName).call()
            true
        } catch (e: Exception) {
            false
        }
    }
    suspend fun createBranch(path: String, branchName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val git = getRepository(path) ?: return@withContext false
            git.branchCreate().setName(branchName).call()
            true
        } catch (e: Exception) {
            false
        }
    }
}
data class GitStatus(
    val filePath: String,
    val type: GitStatusType
)
enum class GitStatusType {
    ADDED, MODIFIED, DELETED, MISSING
}
