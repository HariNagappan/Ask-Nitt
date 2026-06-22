package com.example.asknitt.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answers")
data class AnswerEntity(
    @PrimaryKey val answer_id: Int,
    val question_id: Int, // Foreign key-like relationship
    val answered_username: String,
    val answer_timestamp: String,
    val answer: String,
    val upvotes: Int,
    val downvotes: Int,
    val paths: List<String>
)
