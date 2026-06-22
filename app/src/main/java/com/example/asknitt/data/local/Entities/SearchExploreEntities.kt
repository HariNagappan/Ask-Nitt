package com.example.asknitt.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_results")
data class SearchCacheEntity(
    @PrimaryKey val question_id: Int,
    val posted_username: String,
    val title: String,
    val question: String,
    val tags: List<String>,
    val question_timestamp: String,
    val status: String,
    val paths: List<String>
)

@Entity(tableName = "explore_users")
data class ExploreCacheEntity(
    @PrimaryKey val username: String
)
