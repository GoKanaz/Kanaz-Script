package com.kanaz.script.data

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.filter.PathFilter
import java.io.File

class GitRepository(private val repoPath: String) {
    private var git: Git? = null
    private var repository: Repository? = null

    suspend fun init(): Boolean {
        return try {
            val dir = File(repoPath)
            if (!dir.exists()) dir.mkdirs()
            git = Git.init().setDirectory(dir).call()
            repository = git?.repository
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun open(): Boolean {
        return try {
            val repoDir = File(repoPath, ".git")
            if (!repoDir.exists()) return false
            repository = FileRepositoryBuilder()
                .setGitDir(repoDir)
                .readEnvironment()
                .findGitDir()
                .build()
            git = Git(repository)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getStatus(): Map<String, String> {
        return try {
            val status = git?.status()?.call()
            val result = mutableMapOf<String, String>()
            status?.added?.forEach { result[it] = "added" }
            status?.changed?.forEach { result[it] = "modified" }
            status?.removed?.forEach { result[it] = "deleted" }
            status?.missing?.forEach { result[it] = "missing" }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun commit(message: String): Boolean {
        return try {
            git?.commit()
                ?.setMessage(message)
                ?.setAll(true)
                ?.call()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getDiff(filePath: String): String {
        return try {
            val repo = repository ?: return ""
            val headCommit = repo.resolve("HEAD") ?: return ""
            val headTree = repo.parseCommit(headCommit).tree
            
            val oldTreeParser = CanonicalTreeParser().apply {
                repo.newObjectReader().use { reader ->
                    reset(reader, headTree)
                }
            }
            
            val diff = git?.diff()
                ?.setOldTree(oldTreeParser)
                ?.setNewTree(oldTreeParser)
                ?.setPathFilter(PathFilter.create(filePath))
                ?.call()
            
            diff?.joinToString("\n") { it.toString() } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun close() {
        git?.close()
        repository?.close()
    }
}
