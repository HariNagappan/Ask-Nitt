package com.example.asknitt.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val username: String
)

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey val username: String,
    val type: String // "RECIEVED" or "SENT"
)
