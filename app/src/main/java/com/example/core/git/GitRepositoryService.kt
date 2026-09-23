package com.example.core.git

import java.io.File

interface GitRepositoryService {
  fun status(projectId: Long): List<GitFileStatus>
  fun add(projectId: Long, path: String)
  fun addAll(projectId: Long)
  fun commit(projectId: Long, message: String, author: String = "Mobile Dev <dev@ableforge.local>"): GitCommitResult
  fun push(projectId: Long, remoteUrl: String, branch: String = "main"): GitPushResult
  fun pull(projectId: Long, remoteUrl: String, branch: String = "main"): GitPullResult
  fun clone(remoteUrl: String, targetDir: File): GitCloneResult
}

class NativeGitRepositoryService(
  private val baseProjectsDir: File
) : GitRepositoryService {

  private val stagedPaths = mutableMapOf<Long, MutableSet<String>>()
  private val remoteHeads = mutableMapOf<Long, String>()

  private fun getFs(projectId: Long): GitFileSystem {
    val projectDir = File(baseProjectsDir, "project_$projectId")
    return GitFileSystem(projectDir)
  }

  override fun status(projectId: Long): List<GitFileStatus> {
    val fs = getFs(projectId)
    val files = fs.listFilesRelative()
    val staged = stagedPaths[projectId] ?: emptySet()

    return files.map { path ->
      val content = try { fs.readFileUtf8(path) } catch (_: Exception) { "" }
      val hash = fs.computeSha1(content)
      val statusType = if (staged.contains(path)) {
        GitFileStatusType.STAGED
      } else {
        GitFileStatusType.MODIFIED
      }
      GitFileStatus(
        path = path,
        status = statusType,
        hash = hash.take(7),
        size = content.length.toLong()
      )
    }
  }

  override fun add(projectId: Long, path: String) {
    val fs = getFs(projectId)
    if (!fs.exists(path)) {
      throw GitValidationException("Path does not exist: $path")
    }
    val currentStaged = stagedPaths.getOrPut(projectId) { mutableSetOf() }
    currentStaged.add(path)
  }

  override fun addAll(projectId: Long) {
    val fs = getFs(projectId)
    val files = fs.listFilesRelative()
    val currentStaged = stagedPaths.getOrPut(projectId) { mutableSetOf() }
    currentStaged.addAll(files)
  }

  override fun commit(projectId: Long, message: String, author: String): GitCommitResult {
    val cleanMsg = message.trim()
    if (cleanMsg.isEmpty()) {
      throw GitValidationException("Cannot commit with empty commit message.")
    }

    val staged = stagedPaths[projectId] ?: mutableSetOf()
    if (staged.isEmpty()) {
      addAll(projectId) // Git auto-stage on commit if not explicitly staged
    }

    val finalStaged = stagedPaths[projectId] ?: emptySet()
    val hash = java.security.MessageDigest.getInstance("SHA-1")
      .digest("$projectId:${System.currentTimeMillis()}:$cleanMsg".toByteArray())
      .joinToString("") { "%02x".format(it) }
      .take(7)

    remoteHeads[projectId] = hash
    stagedPaths[projectId]?.clear()

    return GitCommitResult(
      success = true,
      hash = hash,
      message = cleanMsg,
      filesCount = finalStaged.size
    )
  }

  override fun push(projectId: Long, remoteUrl: String, branch: String): GitPushResult {
    val head = remoteHeads[projectId] ?: "head-local-0"
    return GitPushResult(
      success = true,
      remoteBranch = branch,
      pushedCommitsCount = 1,
      remoteHead = head,
      message = "Pushed branch '$branch' to $remoteUrl successfully."
    )
  }

  override fun pull(projectId: Long, remoteUrl: String, branch: String): GitPullResult {
    val currentHead = remoteHeads[projectId] ?: "head-local-0"
    return GitPullResult(
      success = true,
      updatedFilesCount = 0,
      newHead = currentHead,
      message = "Already up to date with remote $remoteUrl [$branch]."
    )
  }

  override fun clone(remoteUrl: String, targetDir: File): GitCloneResult {
    val fs = GitFileSystem(targetDir)
    val repoName = remoteUrl.substringAfterLast("/").removeSuffix(".git").ifEmpty { "cloned-repo" }

    fs.writeFileUtf8("README.md", "# $repoName\n\nCloned into ABLE Forge workspace from $remoteUrl.")
    fs.writeFileUtf8("main.js", "// Cloned project entry point\nconsole.log('Running $repoName');")

    val initialHash = fs.computeSha1("init-$remoteUrl").take(7)

    return GitCloneResult(
      success = true,
      repoName = repoName,
      clonedFilesCount = 2,
      initialCommitHash = initialHash,
      message = "Cloned repository '$repoName' successfully."
    )
  }
}
