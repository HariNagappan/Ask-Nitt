package com.example.asknitt.data


enum class LoginType{
    LOGIN,
    SIGN_UP
}
enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    NOT_SENT
}

enum class ProfileVisibility {
    PUBLIC,
    FRIENDS_ONLY,
    PRIVATE
}


val MAX_TITLE_LENGTH=100
val MAX_QUESTION_LENGTH=5000
val MAX_TAG_LENGTH=50
val MAX_ANSWER_LENGTH=5000


val privacy_modes=listOf("PRIVATE","FRIENDS ONLY","PUBLIC")
var JWT_TOKEN=""
val SHARED_PREFS_FILENAME_ENCRYPTED="ASKNITT"//to change in prod
val MULTIPARTBODY_FILE_KEY="files"//to change in prod
val BASE_URL="http://10.123.100.184:5000"
