package com.example.asknitt.data.local.DAOs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asknitt.data.local.Entities.DoubtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DoubtDao {
    @Query("SELECT * FROM doubts ORDER BY question_timestamp DESC")
    fun getRecentDoubts(): Flow<List<DoubtEntity>>

    @Query("SELECT * FROM doubts WHERE posted_username = :username ORDER BY question_timestamp DESC")
    fun getDoubtsByUsername(username: String): Flow<List<DoubtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubts(doubts: List<DoubtEntity>)

    @Query("DELETE FROM doubts")
    suspend fun clearAll()
}
