package com.example.core.aifix

import java.security.MessageDigest

/**
 * Standard interface for AI Fix Proposals as required by ABLE Forge specifications.
 * Human review and confirmation is strictly required before any code can be applied.
 */
interface FixProposal {
  val id: String
  val originalPath: String
  val originalHash: String
  val proposedContent: String
  val explanation: String
  val language: String
  val verificationRequired: Boolean
}

data class ConcreteFixProposal(
  override val id: String,
  override val originalPath: String,
  override val originalHash: String,
  override val proposedContent: String,
  override val explanation: String,
  override val language: String = "plaintext",
  override val verificationRequired: Boolean = true
) : FixProposal

class HashMismatchException(
  val expectedHash: String,
  val actualHash: String,
  message: String = "Concurrency conflict: File modified since proposal was generated. Expected hash: $expectedHash, actual: $actualHash. Apply aborted."
) : Exception(message)

class CrashRecoverableApplyException(message: String, cause: Throwable? = null) : Exception(message, cause)

object HashUtils {
  fun computeSha1(content: String): String {
    val digest = MessageDigest.getInstance("SHA-1").digest(content.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }
  }
}
