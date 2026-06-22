package com.example.asknitt.data.repository

import com.example.asknitt.data.local.DAOs.UserDao
import com.example.asknitt.data.model.CurrentUserInfo
import com.example.asknitt.data.local.Entities.UserEntity
import com.example.asknitt.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val api: ApiService,
    private val userDao: UserDao
) {
    val cachedUserInfo: Flow<CurrentUserInfo?> = userDao.getCachedProfile().map { entity ->
        entity?.let {
            CurrentUserInfo(
                username = it.username,
                people_helped = it.people_helped,
                questions_asked = it.questions_asked,
                joined_on = it.joined_on,
                profile_visibility = it.profile_visibility
            )
        }
    }

    fun getUserInfo(username: String): Flow<CurrentUserInfo?> {
        return userDao.getUserInfo(username).map { entity ->
            entity?.let {
                CurrentUserInfo(
                    username = it.username,
                    people_helped = it.people_helped,
                    questions_asked = it.questions_asked,
                    joined_on = it.joined_on,
                    profile_visibility = it.profile_visibility
                )
            }
        }
    }

    suspend fun refreshCurrentUserInfo(): Result<CurrentUserInfo> {
        return try {
            val response = api.GetCurrentUserInfo()
            if (response.isSuccessful) {
                val info = response.body()
                if (info != null && info.error_msg.isNullOrEmpty()) {
                    userDao.insertUserInfo(
                        UserEntity(
                            username = info.username,
                            people_helped = info.people_helped,
                            questions_asked = info.questions_asked,
                            joined_on = info.joined_on,
                            profile_visibility = info.profile_visibility
                        )
                    )
                    Result.success(info)
                } else {
                    Result.failure(Exception(info?.error_msg ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
