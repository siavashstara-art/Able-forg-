package com.example.core.git

enum class GitFileStatusType {
  UNTRACKED,
  MODIFIED,
  STAGED,
  COMMITTED,
  DELETED
}

data class GitFileStatus(
  val path: String,
  val status: GitFileStatusType,
  val hash: String,
  val size: Long
)

data class GitBranch(
  val name: String,
  val commitHash: String,
  val isCurrent: Boolean = false
)

data class GitRemote(
  val name: String = "origin",
  val url: String,
  val isConfigured: Boolean = false
)

data class GitCommitResult(
  val success: Boolean,
  val hash: String,
  val message: String,
  val filesCount: Int,
  val timestamp: Long = System.currentTimeMillis()
)

data class GitPushResult(
  val success: Boolean,
  val remoteBranch: String,
  val pushedCommitsCount: Int,
  val remoteHead: String,
  val message: String
)

data class GitPullResult(
  val success: Boolean,
  val updatedFilesCount: Int,
  val newHead: String,
  val message: String
)

data class GitCloneResult(
  val success: Boolean,
  val repoName: String,
  val clonedFilesCount: Int,
  val initialCommitHash: String,
  val message: String
)

/**
 * Concurrency detection error when GitHub remote HEAD changes during commit pipeline.
 * Strict v0.1 policy: No auto-retry.
 */
class ConcurrentModificationError(
  val expectedHeadOid: String,
  val actualHeadOid: String,
  message: String = "Concurrent modification detected on remote HEAD. Expected '$expectedHeadOid' but found '$actualHeadOid'. Auto-retry is disabled in v0.1."
) : Exception(message)

class GitValidationException(message: String) : Exception(message)
