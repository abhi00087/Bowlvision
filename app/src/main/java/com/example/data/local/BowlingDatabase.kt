package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BowlingAnalysisEntity::class], version = 1, exportSchema = false)
abstract class BowlingDatabase : RoomDatabase() {
  abstract fun bowlingDao(): BowlingDao

  companion object {
    @Volatile
    private var INSTANCE: BowlingDatabase? = null

    fun getDatabase(context: Context): BowlingDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          BowlingDatabase::class.java,
          "bowling_vision_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
