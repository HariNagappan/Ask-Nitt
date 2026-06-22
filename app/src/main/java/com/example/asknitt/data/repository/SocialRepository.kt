package com.example.asknitt.data.repository

import com.example.asknitt.data.local.Entities.FriendEntity
import com.example.asknitt.data.local.Entities.FriendRequestEntity
import com.example.asknitt.data.local.Entities.ExploreCacheEntity
import com.example.asknitt.data.local.DAOs.SocialDao
import com.example.asknitt.data.local.DAOs.SearchExploreDao
import com.example.asknitt.data.model.GeneralUser
import com.example.asknitt.data.model.OtherUserInfo
import com.example.asknitt.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SocialRepository(
    private val api: ApiService,
    private val socialDao: SocialDao,
    private val searchExploreDao: SearchExploreDao
) {
    val friends: Flow<List<GeneralUser>> = socialDao.getFriends().map { entities ->
        entities.map { GeneralUser(it.username) }
    }

    val receivedRequests: Flow<List<GeneralUser>> = socialDao.getFriendRequests("RECIEVED").map { entities ->
        entities.map { GeneralUser(it.username) }
    }

    val sentRequests: Flow<List<GeneralUser>> = socialDao.getFriendRequests("SENT").map { entities ->
        entities.map { GeneralUser(it.username) }
    }

    val exploreUsers: Flow<List<GeneralUser>> = searchExploreDao.getExploreCache().map { entities ->
        entities.map { GeneralUser(it.username) }
    }

    suspend fun refreshFriends(): Result<Unit> {
        return try {
            val response = api.GetUsersFriends()
            if (response.isSuccessful) {
                val friends = response.body() ?: emptyList()
                socialDao.clearFriends()
                socialDao.insertFriends(friends.map { FriendEntity(it.username) })
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshReceivedRequests(): Result<Unit> {
        return try {
            val response = api.GetUserFriendRequestsRecieved()
            if (response.isSuccessful) {
                val requests = response.body() ?: emptyList()
                socialDao.clearFriendRequests("RECIEVED")
                socialDao.insertFriendRequests(requests.map { FriendRequestEntity(it.username, "RECIEVED") })
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshSentRequests(): Result<Unit> {
        return try {
            val response = api.GetUserFriendRequestsSent()
            if (response.isSuccessful) {
                val requests = response.body() ?: emptyList()
                socialDao.clearFriendRequests("SENT")
                socialDao.insertFriendRequests(requests.map { FriendRequestEntity(it.username, "SENT") })
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshExploreUsers(query: String): Result<Unit> {
        return try {
            val response = api.GetUsersByName(query)
            if (response.isSuccessful) {
                val users = response.body() ?: emptyList()
                if (query.isEmpty()) {
                    searchExploreDao.clearExploreCache()
                    searchExploreDao.insertExploreUsers(users.map { ExploreCacheEntity(it.username) })
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsersByName(query: String): Result<List<GeneralUser>> {
        return try {
            val response = api.GetUsersByName(query)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOtherUserInfo(otherUsername: String): Result<OtherUserInfo> {
        return try {
            val response = api.GetOtherUserInfo(otherUsername)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendFriendRequest(username: String): Result<Unit> {
        return try {
            val response = api.SendFriendRequest(GeneralUser(username))
            if (response.isSuccessful && response.body()?.error_msg.isNullOrEmpty()) {
                refreshSentRequests()
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error_msg ?: response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptFriendRequest(username: String): Result<Unit> {
        return try {
            val response = api.AcceptFriendRequest(GeneralUser(username))
            if (response.isSuccessful && response.body()?.error_msg.isNullOrEmpty()) {
                refreshFriends()
                refreshReceivedRequests()
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error_msg ?: response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun declineFriendRequest(username: String): Result<Unit> {
        return try {
            val response = api.DeclineFriendRequest(GeneralUser(username))
            if (response.isSuccessful && response.body()?.error_msg.isNullOrEmpty()) {
                refreshReceivedRequests()
                refreshSentRequests()
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error_msg ?: response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
