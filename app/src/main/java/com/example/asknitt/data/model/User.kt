package com.example.asknitt.data.model

data class User(val username:String, val password:String)
data class CurrentUserInfo(val username: String,val people_helped:Int,val questions_asked:Int,val joined_on:String,val token:String="",val error_msg: String="")
data class OtherUserInfo(val username: String, val people_helped:Int, val questions_asked:Int, val joined_on:String, val token:String="", val error_msg: String="",
                         var friend_status: FriendRequestStatus, var is_current_user_sender_of_request:Boolean)

data class GeneralUser(val username:String)
