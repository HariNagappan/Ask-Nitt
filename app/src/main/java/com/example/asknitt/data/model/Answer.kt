package com.example.asknitt.data.model

import kotlinx.serialization.Serializable

data class Answer(val answer_id:Int,val answered_username:String,val answer_timestamp: String,val answer: String,val upvotes:Int,val downvotes:Int,val paths:List<String>)
