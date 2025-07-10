package com.example.asknitt

import android.R.attr.data
import android.R.attr.level
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

enum class MainScreenRoutes{
                    MAIN,
                        HOME,
                        SETTINGS,

                        MY_DOUBTS,
                            MY_DOUBTS_LIST,
                            ADD_DOUBT,

                        SEARCH_STUFF,
                            SEARCH,

                        EXPLORE_USERS_STUFF,
                            EXPLORE_USERS_HOME,
                            FRIENDS,
                            FRIEND_REQUESTS

}
enum class AuthScreenRoutes{
    AUTH,
        LOGIN,
        SIGN_UP,
}
enum class LoginType{
    LOGIN,
    SIGN_UP
}
enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    NOT_SENT
}
enum class QuestionStatus{
    ANY,//to be used only for doubt filter
    SOLVED,
    PENDING
}

val MAX_TITLE_LENGTH=100
val MAX_QUESTION_LENGTH=5000
val MAX_TAG_LENGTH=50
val MAX_ANSWER_LENGTH=5000

@RequiresApi(Build.VERSION_CODES.O)
fun GetUtcInLocalTime(utc_time:String):String{
    val formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val utcDateTime = LocalDateTime.parse(utc_time, formatter).atZone(ZoneOffset.UTC)
    val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
    val displayFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
    return localDateTime.format(displayFormat)
}

@RequiresApi(Build.VERSION_CODES.O)
fun GetLocalInUTC(local: String,start_of_day:Boolean): String {
    val systemZone = ZoneId.systemDefault()
    val formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val return_format= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val localdate=LocalDate.parse(local,formatter)
    val localdatetime=if(start_of_day) localdate.atStartOfDay() else localdate.plusDays(1).atStartOfDay()
    val localzone = localdatetime.atZone(systemZone)
    val utczoned =localzone.withZoneSameInstant(ZoneOffset.UTC)
    return utczoned.format(return_format)

}

sealed class UiState{
    object Loading: UiState()
    object Success:UiState()
    data class Failure(val msg:String): UiState()
}

data class User(val username:String, val password:String)
data class CheckSuccess(val success: Boolean,val token:String?="",val error_msg:String?="")
data class AllScreensNamesItem(val route:String,val label:String,val icon: ImageVector)
data class Token(val token:String,val msg:String)
data class PostDoubtItem(val username: String,val title:String,val question: String,val tags:List<String>)
data class Tags(val tags:List<String>)
data class Answer(val answer_id:Int,val answered_username:String,val answer_timestamp: String,val answer: String,val upvotes:Int,val downvotes:Int)
data class Vote(val add_to_upvote:Int=0,val add_to_downvote:Int=0,val answer_id: Int)
data class PostAnswerToDoubtItem(val question_id: Int,val answer: String,val answered_username: String)
@Serializable
data class Doubt(val posted_username:String, val question_id:Int, val title:String, val question:String, val tags:List<String>, val question_timestamp:String,
                 var status: QuestionStatus)
data class CurrentUserInfo(val username: String,val people_helped:Int,val questions_asked:Int,val joined_on:String,val token:String="",val error_msg: String="")
data class OtherUserInfo(val username: String, val people_helped:Int, val questions_asked:Int, val joined_on:String, val token:String="", val error_msg: String="",
                         var friend_status: FriendRequestStatus, var is_current_user_sender_of_request:Boolean)
data class FilterItem(val idx:Int,val name:String)
@Serializable
data class GeneralUser(val username:String)
data class MarkQuestionSolvedItem(val question_id:Int)
data class UploadFileItem(val multipartBody: MultipartBody.Part,val filename:String)

val privacy_modes=listOf("PRIVATE","FRIENDS ONLY","PUBLIC")
var JWT_TOKEN=""
var SHARED_PREFS_FILENAME_ENCRYPTED="ASKNITT"
var SHARED_PREFS_FILENAME_NORMAL="ASKNITT_NORMAL"

val authinterceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("Authorization", JWT_TOKEN)
        .build()
    chain.proceed(request)
}
val client= OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .addInterceptor(authinterceptor)
    .build()

val retrofit= Retrofit.Builder()
    .baseUrl("http://192.168.1.36:5000")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val api=retrofit.create(ApiService::class.java)