package com.example.asknitt.data.repository

import com.example.asknitt.data.local.DAOs.DoubtDao
import com.example.asknitt.data.local.DAOs.SearchExploreDao
import com.example.asknitt.data.local.Entities.SearchCacheEntity
import com.example.asknitt.data.model.Doubts
import com.example.asknitt.data.model.MarkQuestionSolvedItem
import com.example.asknitt.data.model.PostDoubtItem
import com.example.asknitt.data.model.toEntity
import com.example.asknitt.data.model.toModel
import com.example.asknitt.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody

class DoubtRepository(
    private val api: ApiService,
    private val doubtDao: DoubtDao,
    private val searchExploreDao: SearchExploreDao
) {
    val recentDoubts: Flow<List<Doubts>> = doubtDao.getRecentDoubts().map { entities ->
        entities.map { it.toModel() }
    }

    val searchResults: Flow<List<Doubts>> = searchExploreDao.getSearchCache().map { entities ->
        entities.map { entity ->
            Doubts(
                posted_username = entity.posted_username,
                question_id = entity.question_id,
                title = entity.title,
                question = entity.question,
                tags = entity.tags,
                question_timestamp = entity.question_timestamp,
                status = com.example.asknitt.data.model.QuestionStatus.valueOf(entity.status),
                paths = entity.paths
            )
        }
    }

    fun getUserDoubts(username: String): Flow<List<Doubts>> = 
        doubtDao.getDoubtsByUsername(username).map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun refreshRecentDoubts(): Result<Unit> {
        return try {
            val response = api.GetRecentQuestions()
            if (response.isSuccessful) {
                val doubts = response.body() ?: emptyList()
                doubtDao.insertDoubts(doubts.map { it.toEntity() })
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshUserDoubts(username: String): Result<Unit> {
        if (username.isEmpty()) return Result.failure(Exception("Username is empty"))
        return try {
            val response = api.GetDoubts(username)
            if (response.isSuccessful) {
                val doubts = response.body() ?: emptyList()
                doubtDao.insertDoubts(doubts.map { it.toEntity() })
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun performSearch(
        searchText: String,
        tags: List<String>?,
        fromDate: String,
        toDate: String,
        status: String
    ): Result<Unit> {
        return try {
            val response = api.GetDoubtsByFilter(searchText, tags?.toMutableList(), fromDate, toDate, status)
            if (response.isSuccessful) {
                val results = response.body() ?: emptyList()
                searchExploreDao.clearSearchCache()
                searchExploreDao.insertSearchResults(results.map {
                    SearchCacheEntity(
                        question_id = it.question_id,
                        posted_username = it.posted_username,
                        title = it.title,
                        question = it.question,
                        tags = it.tags,
                        question_timestamp = it.question_timestamp,
                        status = it.status.name,
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

    suspend fun postDoubt(username: String, title: String, question: String, tags: List<String>): Result<Unit> {
        return try {
            val response = api.PostDoubt(PostDoubtItem(username, title, question, tags))
            if (response.isSuccessful && response.body()?.error_msg.isNullOrEmpty()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error_msg ?: "Post failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadFiles(files: List<MultipartBody.Part>): Result<Unit> {
        return try {
            val response = api.UploadFilesForDoubt(files)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception(response.message()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markQuestionSolved(questionId: Int): Result<Unit> {
        return try {
            val response = api.MarkQuestionSolved(MarkQuestionSolvedItem(questionId))
            if (response.isSuccessful && response.body()?.error_msg.isNullOrEmpty()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error_msg ?: response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
