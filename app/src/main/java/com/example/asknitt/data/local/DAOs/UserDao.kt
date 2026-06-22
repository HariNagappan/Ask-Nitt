package com.example.asknitt.data.local.DAOs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asknitt.data.local.Entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_info LIMIT 1")
    fun getCachedProfile(): Flow<UserEntity?>

    @Query("SELECT * FROM user_info WHERE username = :username")
    fun getUserInfo(username: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfo: UserEntity)

    @Query("DELETE FROM user_info")
    suspend fun clearAll()
}
