package com.example.asknitt.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Doubts(val posted_username:String, val question_id:Int, val title:String, val question:String, val tags:List<String>, val question_timestamp:String, var status: QuestionStatus, val paths:List<String>)

data class PostDoubtItem(val username: String,val title:String,val question: String,val tags:List<String>)