package com.example.core.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val description: String,
  val templateType: String, // "WEB_APP", "KOTLIN_SCRIPT", "JSON_API", "MARKDOWN_DOC"
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val isFavorite: Boolean = false
)

@Entity(
  tableName = "files",
  foreignKeys = [
    ForeignKey(
      entity = ProjectEntity::class,
      parentColumns = ["id"],
      childColumns = ["projectId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("projectId")]
)
data class FileEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val projectId: Long,
  val path: String, // e.g., "index.html", "src/main.kt"
  val name: String,
  val isDirectory: Boolean = false,
  val content: String = "",
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "git_commits",
  foreignKeys = [
    ForeignKey(
      entity = ProjectEntity::class,
      parentColumns = ["id"],
      childColumns = ["projectId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("projectId")]
)
data class GitCommitEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val projectId: Long,
  val hash: String,
  val message: String,
  val author: String = "Mobile Developer <dev@ableforge.local>",
  val timestamp: Long = System.currentTimeMillis(),
  val branch: String = "main"
)

@Entity(tableName = "console_history")
data class ConsoleEntryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val projectId: Long = 0,
  val command: String,
  val output: String,
  val isError: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)
