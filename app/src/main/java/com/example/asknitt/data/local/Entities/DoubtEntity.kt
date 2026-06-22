package com.example.asknitt.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.asknitt.data.model.QuestionStatus

@Entity(tableName = "doubts")
data class DoubtEntity(
    @PrimaryKey val question_id: Int,
    val posted_username: String,
    val title: String,
    val question: String,
    val tags: List<String>,
    val question_timestamp: String,
    val status: QuestionStatus,
    val paths: List<String>
)
