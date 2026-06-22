package com.example.asknitt.data.local.DAOs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asknitt.data.local.Entities.AnswerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    @Query("SELECT * FROM answers WHERE question_id = :questionId ORDER BY answer_timestamp DESC")
    fun getAnswersByQuestionId(questionId: Int): Flow<List<AnswerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AnswerEntity>)

    @Query("DELETE FROM answers WHERE question_id = :questionId")
    suspend fun clearAnswersByQuestionId(questionId: Int)
}
