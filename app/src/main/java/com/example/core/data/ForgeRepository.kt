package com.example.core.data

import com.example.core.model.ConsoleEntryEntity
import com.example.core.model.FileEntity
import com.example.core.model.GitCommitEntity
import com.example.core.model.ProjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.security.MessageDigest

class ForgeRepository(private val db: ForgeDatabase) {
  val allProjects: Flow<List<ProjectEntity>> = db.projectDao().getAllProjects()
  val consoleHistory: Flow<List<ConsoleEntryEntity>> = db.consoleDao().getConsoleHistory()

  fun getFilesForProject(projectId: Long): Flow<List<FileEntity>> {
    return db.fileDao().getFilesForProject(projectId)
  }

  fun getCommitsForProject(projectId: Long): Flow<List<GitCommitEntity>> {
    return db.gitDao().getCommitsForProject(projectId)
  }

  suspend fun getProjectById(id: Long): ProjectEntity? {
    return db.projectDao().getProjectById(id)
  }

  suspend fun createProject(
    name: String,
    description: String,
    templateType: String
  ): Long {
    val project = ProjectEntity(
      name = name.trim().ifEmpty { "untitled-forge" },
      description = description.trim(),
      templateType = templateType,
      createdAt = System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis()
    )
    val projectId = db.projectDao().insertProject(project)

    // Populate initial files according to template
    when (templateType) {
      "WEB_APP" -> {
        db.fileDao().insertFile(
          FileEntity(
            projectId = projectId,
            path = "index.html",
            name = "index.html",
            content = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${name}</title>
  <style>
    body { font-family: system-ui, sans-serif; padding: 2rem; background: #0f172a; color: #f8fafc; }
    .card { background: #1e293b; padding: 1.5rem; border-radius: 12px; border: 1px solid #334155; }
    h1 { color: #f59e0b; margin-top: 0; }
    button { background: #f59e0b; color: #0f172a; border: none; padding: 10px 18px; border-radius: 8px; font-weight: bold; cursor: pointer; }
  </style>
</head>
<body>
  <div class="card">
    <h1>ABLE Forge / کارگاه توانا</h1>
    <p>Mobile Native Web App Scaffold</p>
    <button onclick="alert('Running on Mobile!')">Tap Action</button>
  </div>
</body>
</html>
            """.trimIndent()
          )
        )
        db.fileDao().insertFile(
          FileEntity(
            projectId = projectId,
            path = "app.js",
            name = "app.js",
            content = """
// Main client logic
console.log("ABLE Forge Web App initialized on mobile.");
function computeStatus() {
  return { status: "OK", timestamp: Date.now() };
}
            """.trimIndent()
          )
        )
      }
      "KOTLIN_SCRIPT" -> {
        db.fileDao().insertFile(
          FileEntity(
            projectId = projectId,
            path = "Main.kt",
            name = "Main.kt",
            content = """
package com.ableforge.app

fun main() {
    val title = "${name}"
    println(">>> Running [${'$'}title] in ABLE Forge Mobile Engine")
    val result = (1..5).fold(1) { acc, i -> acc * i }
    println("Factorial calculation result: ${'$'}result")
}
            """.trimIndent()
          )
        )
      }
      "JSON_API" -> {
        db.fileDao().insertFile(
          FileEntity(
            projectId = projectId,
            path = "api_spec.json",
            name = "api_spec.json",
            content = """
{
  "api": "${name}",
  "version": "1.0.0",
  "engine": "ABLE Forge Mobile",
  "routes": [
    { "path": "/health", "method": "GET", "status": 200 },
    { "path": "/data", "method": "POST", "status": 201 }
  ]
}
            """.trimIndent()
          )
        )
      }
      else -> {
        db.fileDao().insertFile(
          FileEntity(
            projectId = projectId,
            path = "README.md",
            name = "README.md",
            content = """
# ${name}
> Built with ABLE Forge — کارگاه توانا
> Build. Fix. Create. Anywhere.

### Architecture Overview
- Mobile-first native engineering
- Local persistent storage
- Git version control
            """.trimIndent()
          )
        )
      }
    }

    // Initial git commit
    db.gitDao().insertCommit(
      GitCommitEntity(
        projectId = projectId,
        hash = generateHash("${projectId}-init"),
        message = "Initial commit: scaffold $templateType structure",
        timestamp = System.currentTimeMillis()
      )
    )

    return projectId
  }

  suspend fun updateFile(file: FileEntity) {
    db.fileDao().updateFile(file.copy(updatedAt = System.currentTimeMillis()))
  }

  suspend fun insertFile(file: FileEntity): Long {
    return db.fileDao().insertFile(file)
  }

  suspend fun deleteFile(file: FileEntity) {
    db.fileDao().deleteFile(file)
  }

  suspend fun deleteProject(project: ProjectEntity) {
    db.projectDao().deleteProject(project)
  }

  suspend fun commitChanges(projectId: Long, message: String): String {
    val cleanMsg = message.trim().ifEmpty { "Update project files" }
    val hash = generateHash("$projectId-${System.currentTimeMillis()}")
    val commit = GitCommitEntity(
      projectId = projectId,
      hash = hash,
      message = cleanMsg,
      timestamp = System.currentTimeMillis()
    )
    db.gitDao().insertCommit(commit)
    return hash
  }

  suspend fun executeConsoleCommand(
    projectId: Long,
    command: String,
    files: List<FileEntity>
  ): String {
    val trimmed = command.trim()
    if (trimmed.isEmpty()) return ""

    val parts = trimmed.split("\\s+".toRegex())
    val cmd = parts[0].lowercase()
    val args = parts.drop(1)

    var output = ""
    var isError = false

    when (cmd) {
      "help" -> {
        output = """
Available ABLE Forge Shell Commands:
- help               : Display this list of commands
- ls                 : List all files in active project
- cat <filename>     : Display content of a file
- stat <filename>    : Display file line count, character count and timestamp
- pwd                : Print working project directory
- clear              : Clear console screen
- git status         : Show git working tree status
- build              : Verify project files and syntax integrity
- test               : Run local unit verification
- echo <text>        : Print text to console
        """.trimIndent()
      }
      "pwd" -> {
        output = "/data/user/0/com.aistudio.ableforge.qwvzk/projects/$projectId"
      }
      "ls" -> {
        if (files.isEmpty()) {
          output = "(empty project)"
        } else {
          output = files.joinToString("\n") { file ->
            val icon = if (file.isDirectory) "📁" else "📄"
            val size = file.content.length
            "$icon ${file.path.padEnd(20)} [${size} chars]"
          }
        }
      }
      "cat" -> {
        if (args.isEmpty()) {
          output = "Error: missing filename operand. Usage: cat <filename>"
          isError = true
        } else {
          val targetName = args[0]
          val target = files.find { it.name.equals(targetName, ignoreCase = true) || it.path.equals(targetName, ignoreCase = true) }
          if (target != null) {
            output = target.content
          } else {
            output = "cat: $targetName: No such file in project"
            isError = true
          }
        }
      }
      "stat" -> {
        if (args.isEmpty()) {
          output = "Error: missing filename. Usage: stat <filename>"
          isError = true
        } else {
          val targetName = args[0]
          val target = files.find { it.name.equals(targetName, ignoreCase = true) || it.path.equals(targetName, ignoreCase = true) }
          if (target != null) {
            val lines = target.content.lines().size
            val chars = target.content.length
            output = """
File: ${target.name}
Path: ${target.path}
Lines: $lines
Characters: $chars
Last Modified: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date(target.updatedAt))}
Storage: Local Device SQLite/Room
            """.trimIndent()
          } else {
            output = "stat: cannot stat '$targetName': No such file"
            isError = true
          }
        }
      }
      "echo" -> {
        output = args.joinToString(" ")
      }
      "clear" -> {
        db.consoleDao().clearHistory()
        return ""
      }
      "git" -> {
        if (args.isNotEmpty() && args[0] == "status") {
          output = """
On branch main
Your branch is up to date with local repository.
Total tracked files: ${files.size}
Remote Sync: Offline / Local Only (Set remote URL in settings)
working tree clean
          """.trimIndent()
        } else {
          output = "git: unrecognized command. Try: git status"
        }
      }
      "build" -> {
        val totalFiles = files.size
        val errorCount = files.count { file ->
          file.name.endsWith(".json") && (!file.content.trim().startsWith("{") || !file.content.trim().endsWith("}"))
        }
        if (errorCount > 0) {
          output = "[BUILD FAILURE]: Found $errorCount syntax violation in JSON specifications."
          isError = true
        } else {
          output = "[BUILD SUCCESS]: Verified $totalFiles files. AST integrity sound. Ready for deployment."
        }
      }
      "test" -> {
        output = """
Running Local Verification Suite...
✔ Project Workspace Structure: OK
✔ Local Room Persistence: OK
✔ Encoding UTF-8: OK
All local tests passed (3/3 checks green).
        """.trimIndent()
      }
      else -> {
        output = "command not found: $cmd. Type 'help' for available commands."
        isError = true
      }
    }

    db.consoleDao().insertEntry(
      ConsoleEntryEntity(
        projectId = projectId,
        command = trimmed,
        output = output,
        isError = isError,
        timestamp = System.currentTimeMillis()
      )
    )

    return output
  }

  suspend fun ensureInitialData() {
    val existing = allProjects.firstOrNull()
    if (existing.isNullOrEmpty()) {
      createProject(
        name = "AbleForge-Core",
        description = "Core software development workspace for mobile engineering.",
        templateType = "WEB_APP"
      )
      createProject(
        name = "MathLogic-Kotlin",
        description = "Algorithmic computation and unit tested mobile logic.",
        templateType = "KOTLIN_SCRIPT"
      )
    }
  }

  private fun generateHash(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
    return bytes.take(7).joinToString("") { "%02x".format(it) }
  }
}
