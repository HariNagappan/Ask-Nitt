package com.example.asknitt.data.local.DAOs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asknitt.data.local.Entities.AiChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiChatDao {
    @Query("SELECT * FROM ai_chat_history ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<AiChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AiChatEntity)

    @Query("DELETE FROM ai_chat_history")
    suspend fun clearHistory()
}
