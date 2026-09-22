package com.example.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.core.model.ConsoleEntryEntity
import com.example.core.model.FileEntity
import com.example.core.model.GitCommitEntity
import com.example.core.model.ProjectEntity

@Database(
  entities = [
    ProjectEntity::class,
    FileEntity::class,
    GitCommitEntity::class,
    ConsoleEntryEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class ForgeDatabase : RoomDatabase() {
  abstract fun projectDao(): ProjectDao
  abstract fun fileDao(): FileDao
  abstract fun gitDao(): GitDao
  abstract fun consoleDao(): ConsoleDao

  companion object {
    @Volatile
    private var INSTANCE: ForgeDatabase? = null

    fun getDatabase(context: Context): ForgeDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          ForgeDatabase::class.java,
          "able_forge.db"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
