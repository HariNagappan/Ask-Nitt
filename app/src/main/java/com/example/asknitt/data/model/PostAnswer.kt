package com.example.asknitt.data.model

import kotlinx.serialization.Serializable

data class PostAnswerToDoubtItem(val question_id: Int,val answer: String,val answered_username: String)
