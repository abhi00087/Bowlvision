package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BowlingDao {
  @Query("SELECT * FROM bowling_analyses ORDER BY timestamp DESC")
  fun getAllAnalyses(): Flow<List<BowlingAnalysisEntity>>

  @Query("SELECT * FROM bowling_analyses WHERE id = :id")
  suspend fun getAnalysisById(id: Long): BowlingAnalysisEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAnalysis(analysis: BowlingAnalysisEntity): Long

  @Delete
  suspend fun deleteAnalysis(analysis: BowlingAnalysisEntity)

  @Query("DELETE FROM bowling_analyses")
  suspend fun clearAll()
}
