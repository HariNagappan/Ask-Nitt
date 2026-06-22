package com.example.asknitt.data.model

import com.example.asknitt.data.local.Entities.DoubtEntity
import kotlinx.serialization.Serializable

@Serializable
data class Doubts(
    val posted_username: String,
    val question_id: Int,
    val title: String,
    val question: String,
    val tags: List<String>,
    val question_timestamp: String,
    var status: QuestionStatus,
    val paths: List<String>
)

fun Doubts.toEntity(): DoubtEntity {
    return DoubtEntity(
        question_id = question_id,
        posted_username = posted_username,
        title = title,
        question = question,
        tags = tags,
        question_timestamp = question_timestamp,
        status = status,
        paths = paths
    )
}

fun DoubtEntity.toModel(): Doubts {
    return Doubts(
        question_id = question_id,
        posted_username = posted_username,
        title = title,
        question = question,
        tags = tags,
        question_timestamp = question_timestamp,
        status = status,
        paths = paths
    )
}

data class PostDoubtItem(val username: String, val title: String, val question: String, val tags: List<String>)
