package com.example.asknitt.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MarkQuestionSolvedItem(val question_id:Int)

@Serializable
enum class QuestionStatus{
    ANY,//to be used only for doubt filter
    SOLVED,
    PENDING
}
