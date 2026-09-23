package com.example.core.git

import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Native Kotlin filesystem abstraction for ABLE Forge Git layer.
 * Strictly adheres to v0.1 specification:
 * - Symlinks are explicitly prohibited.
 * - Sandboxed to project root directory.
 * - Enforces clean UTF-8 text encoding.
 */
class GitFileSystem(val rootDir: File) {

  init {
    if (!rootDir.exists()) {
      rootDir.mkdirs()
    }
  }

  fun resolveSafe(path: String): File {
    val cleanPath = path.trim().removePrefix("/").replace("\\", "/")
    val targetFile = File(rootDir, cleanPath).canonicalFile
    val canonicalRoot = rootDir.canonicalFile

    if (!targetFile.path.startsWith(canonicalRoot.path)) {
      throw SecurityException("Path traversal attempt detected: $path is outside sandbox.")
    }

    // Explicit v0.1 limitation: Symlinks are forbidden
    if (isSymlink(targetFile)) {
      throw UnsupportedOperationException("Symlinks are not supported in ABLE Forge v0.1.")
    }

    return targetFile
  }

  fun exists(path: String): Boolean {
    val file = resolveSafe(path)
    return file.exists()
  }

  fun readFileUtf8(path: String): String {
    val file = resolveSafe(path)
    if (!file.exists() || !file.isFile) {
      throw IllegalArgumentException("File not found or is directory: $path")
    }
    return file.readText(StandardCharsets.UTF_8)
  }

  fun writeFileUtf8(path: String, content: String): Long {
    val file = resolveSafe(path)
    file.parentFile?.mkdirs()
    file.writeText(content, StandardCharsets.UTF_8)
    return file.length()
  }

  fun deleteFile(path: String): Boolean {
    val file = resolveSafe(path)
    return if (file.exists()) file.delete() else false
  }

  fun listFilesRelative(): List<String> {
    val rootCanonical = rootDir.canonicalFile
    return rootDir.walkTopDown()
      .filter { it.isFile && !isSymlink(it) }
      .map { it.canonicalPath.removePrefix(rootCanonical.path).removePrefix("/").replace("\\", "/") }
      .filter { !it.startsWith(".git") && !it.endsWith(".autofix.tmp") && !it.endsWith(".autofix.bak") }
      .toList()
  }

  fun computeSha1(content: String): String {
    val bytes = MessageDigest.getInstance("SHA-1").digest(content.toByteArray(StandardCharsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }

  private fun isSymlink(file: File): Boolean {
    return try {
      val canonical = file.canonicalFile
      val absolute = file.absoluteFile
      !canonical.equals(absolute) && file.exists() && java.nio.file.Files.isSymbolicLink(file.toPath())
    } catch (_: Throwable) {
      false
    }
  }
}
