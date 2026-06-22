package com.example.asknitt.data.local.DAOs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asknitt.data.local.Entities.FriendEntity
import com.example.asknitt.data.local.Entities.FriendRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    @Query("SELECT * FROM friends")
    fun getFriends(): Flow<List<FriendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>)

    @Query("DELETE FROM friends")
    suspend fun clearFriends()

    @Query("SELECT * FROM friend_requests WHERE type = :type")
    fun getFriendRequests(type: String): Flow<List<FriendRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequests(requests: List<FriendRequestEntity>)

    @Query("DELETE FROM friend_requests WHERE type = :type")
    suspend fun clearFriendRequests(type: String)
}
