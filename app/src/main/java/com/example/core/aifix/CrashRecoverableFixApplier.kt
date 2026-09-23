package com.example.core.aifix

import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Crash-Recoverable Replacement
 *
 * Implements crash-safe file replacement with exact verification stages:
 * 1. Verify currentHash === proposal.originalHash
 * 2. Backup to target.autofix.bak
 * 3. Write proposedContent to target.autofix.tmp
 * 4. Read back target.autofix.tmp and verify
 * 5. Replace target
 * 6. Verify target
 * 7. Remove backup (and temporary file)
 *
 * Explicit Note: This is termed Crash-Recoverable Replacement (not Atomic Replacement).
 * Startup recovery is built-in; newer files are NEVER overwritten during recovery.
 */
class CrashRecoverableFixApplier {

  /**
   * Applies a proposed fix according to the 7-step Crash-Recoverable Replacement protocol.
   * Throws [HashMismatchException] if the file was modified since the proposal was created.
   * Throws [CrashRecoverableApplyException] if any stage of replacement or verification fails.
   */
  fun applyProposal(targetFile: File, proposal: FixProposal): Boolean {
    val bakFile = File(targetFile.parentFile, "${targetFile.name}.autofix.bak")
    val tmpFile = File(targetFile.parentFile, "${targetFile.name}.autofix.tmp")

    // Stage 1: Verify hash
    val currentContent = if (targetFile.exists()) targetFile.readText(StandardCharsets.UTF_8) else ""
    val currentHash = HashUtils.computeSha1(currentContent)

    if (currentHash != proposal.originalHash) {
      throw HashMismatchException(
        expectedHash = proposal.originalHash,
        actualHash = currentHash
      )
    }

    try {
      // Stage 2: Backup
      if (targetFile.exists()) {
        targetFile.copyTo(bakFile, overwrite = true)
      }

      // Stage 3: Write tmp
      tmpFile.writeText(proposal.proposedContent, StandardCharsets.UTF_8)

      // Stage 4: Read back
      val tmpReadBack = tmpFile.readText(StandardCharsets.UTF_8)
      val expectedTmpHash = HashUtils.computeSha1(proposal.proposedContent)
      val actualTmpHash = HashUtils.computeSha1(tmpReadBack)

      if (expectedTmpHash != actualTmpHash) {
        throw CrashRecoverableApplyException("Tmp write read-back failed: checksum mismatch.")
      }

      // Stage 5: Replace target
      tmpFile.copyTo(targetFile, overwrite = true)

      // Stage 6: Verify target
      val targetReadBack = targetFile.readText(StandardCharsets.UTF_8)
      val targetHash = HashUtils.computeSha1(targetReadBack)

      if (targetHash != expectedTmpHash) {
        // Rollback from backup if replacement verification fails
        if (bakFile.exists()) {
          bakFile.copyTo(targetFile, overwrite = true)
        }
        throw CrashRecoverableApplyException("Replacement verification failed. Restored from backup.")
      }

      // Stage 7: Remove backup
      if (bakFile.exists()) {
        bakFile.delete()
      }
      if (tmpFile.exists()) {
        tmpFile.delete()
      }

      return true
    } catch (e: Exception) {
      if (e is HashMismatchException) throw e
      // If error occurs before backup removal, ensure target is restored from backup if corrupted
      if (bakFile.exists() && (!targetFile.exists() || targetFile.length() == 0L)) {
        bakFile.copyTo(targetFile, overwrite = true)
      }
      throw CrashRecoverableApplyException("Crash-Recoverable Replacement aborted: ${e.message}", e)
    }
  }

  /**
   * Startup recovery routine:
   * Scans project directory for orphan .autofix.tmp and .autofix.bak files.
   * Rule: A newer file is NEVER overwritten during recovery!
   */
  fun performStartupRecovery(projectDir: File): Int {
    if (!projectDir.exists() || !projectDir.isDirectory) return 0
    var recoveredCount = 0

    val bakFiles = projectDir.walkTopDown().filter { it.name.endsWith(".autofix.bak") }.toList()
    for (bak in bakFiles) {
      val originalName = bak.name.removeSuffix(".autofix.bak")
      val targetFile = File(bak.parentFile, originalName)
      val tmpFile = File(bak.parentFile, "$originalName.autofix.tmp")

      if (!targetFile.exists()) {
        // Target missing: recover from backup
        bak.copyTo(targetFile, overwrite = true)
        recoveredCount++
      } else {
        // Target exists: Check timestamps. A newer file is NEVER overwritten!
        if (targetFile.lastModified() < bak.lastModified()) {
          // Backup is strictly newer and target was incomplete
          bak.copyTo(targetFile, overwrite = true)
          recoveredCount++
        }
      }

      // Clean up backup and tmp files
      bak.delete()
      if (tmpFile.exists()) {
        tmpFile.delete()
      }
    }

    // Clean up any stray tmp files without bak
    val orphanTmpFiles = projectDir.walkTopDown().filter { it.name.endsWith(".autofix.tmp") }.toList()
    for (tmp in orphanTmpFiles) {
      tmp.delete()
    }

    return recoveredCount
  }
}
