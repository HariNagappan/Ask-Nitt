package com.example.asknitt.data.model

import kotlinx.serialization.Serializable
data class Vote(val add_to_upvote:Int=0,val add_to_downvote:Int=0,val answer_id: Int)
