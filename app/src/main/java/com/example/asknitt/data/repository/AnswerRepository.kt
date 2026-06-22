package com.example.asknitt.data.repository

import com.example.asknitt.data.local.DAOs.AnswerDao
import com.example.asknitt.data.local.Entities.AnswerEntity
import com.example.asknitt.data.model.Answer
import com.example.asknitt.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnswerRepository(
    private val api: ApiService,
    private val answerDao: AnswerDao
) {
    fun getAnswersForQuestion(questionId: Int): Flow<List<Answer>> {
        return answerDao.getAnswersByQuestionId(questionId).map { entities ->
            entities.map { entity ->
                Answer(
                    answer_id = entity.answer_id,
                    answered_username = entity.answered_username,
                    answer_timestamp = entity.answer_timestamp,
                    answer = entity.answer,
                    upvotes = entity.upvotes,
                    downvotes = entity.downvotes,
                    paths = entity.paths
                )
            }
        }
    }

    suspend fun refreshAnswers(questionId: Int): Result<Unit> {
        return try {
            val response = api.GetAnswers(questionId)
            if (response.isSuccessful) {
                val answers = response.body() ?: emptyList()
                answerDao.clearAnswersByQuestionId(questionId)
                answerDao.insertAnswers(answers.map {
                    AnswerEntity(
                        answer_id = it.answer_id,
                        question_id = questionId,
                        answered_username = it.answered_username,
                        answer_timestamp = it.answer_timestamp,
                        answer = it.answer,
                        upvotes = it.upvotes,
                        downvotes = it.downvotes,
                        paths = it.paths
                    )
                })
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
