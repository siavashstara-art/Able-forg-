package com.example.core.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.core.model.ConsoleEntryEntity
import com.example.core.model.FileEntity
import com.example.core.model.GitCommitEntity
import com.example.core.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
  @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
  fun getAllProjects(): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
  suspend fun getProjectById(id: Long): ProjectEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: ProjectEntity): Long

  @Update
  suspend fun updateProject(project: ProjectEntity)

  @Delete
  suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface FileDao {
  @Query("SELECT * FROM files WHERE projectId = :projectId ORDER BY isDirectory DESC, name ASC")
  fun getFilesForProject(projectId: Long): Flow<List<FileEntity>>

  @Query("SELECT * FROM files WHERE id = :id LIMIT 1")
  suspend fun getFileById(id: Long): FileEntity?

  @Query("SELECT * FROM files WHERE projectId = :projectId AND path = :path LIMIT 1")
  suspend fun getFileByPath(projectId: Long, path: String): FileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFile(file: FileEntity): Long

  @Update
  suspend fun updateFile(file: FileEntity)

  @Delete
  suspend fun deleteFile(file: FileEntity)

  @Query("DELETE FROM files WHERE projectId = :projectId AND path = :path")
  suspend fun deleteByPath(projectId: Long, path: String)
}

@Dao
interface GitDao {
  @Query("SELECT * FROM git_commits WHERE projectId = :projectId ORDER BY timestamp DESC")
  fun getCommitsForProject(projectId: Long): Flow<List<GitCommitEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCommit(commit: GitCommitEntity): Long
}

@Dao
interface ConsoleDao {
  @Query("SELECT * FROM console_history ORDER BY timestamp ASC")
  fun getConsoleHistory(): Flow<List<ConsoleEntryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEntry(entry: ConsoleEntryEntity): Long

  @Query("DELETE FROM console_history")
  suspend fun clearHistory()
}
