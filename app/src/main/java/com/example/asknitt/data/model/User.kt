package com.example.asknitt.data.model

import com.example.asknitt.data.FriendRequestStatus
import com.example.asknitt.data.ProfileVisibility
import kotlinx.serialization.Serializable

data class User(val username: String, val password: String)

data class CurrentUserInfo(
    val username: String,
    val people_helped: Int,
    val questions_asked: Int,
    val joined_on: String,
    val profile_visibility: ProfileVisibility,
    val friend_status: FriendRequestStatus = FriendRequestStatus.NOT_SENT,
    val token: String = "",
    val error_msg: String = ""
)

data class OtherUserInfo(
    val username: String,
    val people_helped: Int?,
    val questions_asked: Int?,
    val joined_on: String?,
    val friend_status: FriendRequestStatus,
    val is_current_user_sender_of_request: Boolean,
    val is_private: Boolean,
    val profile_visibility: ProfileVisibility,
    val token: String = "",
    val error_msg: String = ""
)

@Serializable
data class GeneralUser(val username: String, val profile_visibility: ProfileVisibility = ProfileVisibility.PUBLIC)
