package com.example.asknitt.data.repository

import com.example.asknitt.data.local.DAOs.AiChatDao
import com.example.asknitt.data.local.Entities.AiChatEntity
import com.example.asknitt.data.remote.AIChatRequest
import com.example.asknitt.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AiChatRepository(
    private val api: ApiService,
    private val aiChatDao: AiChatDao
) {
    val chatHistory: Flow<List<AiChatEntity>> = aiChatDao.getAllMessages()

    suspend fun askAI(prompt: String): Result<String> {
        return try {
            // Save user message to DB
            aiChatDao.insertMessage(AiChatEntity(text = prompt, isUser = true))
            
            val response = api.AskAI(AIChatRequest(prompt))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.error.isNullOrEmpty()) {
                    // Save AI response to DB
                    aiChatDao.insertMessage(AiChatEntity(text = body.response, isUser = false))
                    Result.success(body.response)
                } else {
                    Result.failure(Exception(body.error))
                }
            } else {
                Result.failure(Exception("Server error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearHistory(): Result<String> {
        return try {
            val response = api.DeleteAIHistory()
            if (response.isSuccessful && response.body() != null) {
                aiChatDao.clearHistory()
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Failed to delete history on server"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
