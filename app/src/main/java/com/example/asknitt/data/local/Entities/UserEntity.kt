package com.example.asknitt.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.asknitt.data.ProfileVisibility

@Entity(tableName = "user_info")
data class UserEntity(
    @PrimaryKey val username: String,
    val people_helped: Int,
    val questions_asked: Int,
    val joined_on: String,
    val profile_visibility: ProfileVisibility
)
