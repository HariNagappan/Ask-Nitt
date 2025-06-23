package com.example.asknitt

import android.R.attr.data
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

enum class MainScreenRoutes{
                    MAIN,
                        HOME,
                        SETTINGS,

                        MY_DOUBTS,
                            MY_DOUBTS_LIST,
                            ADD_DOUBT,
                            VIEW_DOUBT_IN_DETAIL
}
enum class AuthScreenRoutes{
    AUTH,
        LOGIN,
        SIGN_UP,
}
//enum class MainScreensNames(val label:String){
//    HOME("Home"),
//    SETTINGS("Settings"),
//    MY_DOUBTS("My Doubts"),
//    MY_DOUBTS_LIST("My Doubts List"),
//    ADD_DOUBT("Add Doubts")
//}
enum class LoginType{
    LOGIN,
    SIGN_UP
}

val shared_prefs_filename="main_prefs"
val MAX_TITLE_LENGTH=100
val MAX_QUESTION_LENGTH=5000
val MAX_TAG_LENGTH=50
val MAX_ANSWER_LENGTH=5000

data class User(val username:String, val password:String)
data class CheckUser(val user_exists:Boolean,val error_msg:String)
data class CheckSuccess(val success: Boolean)
data class AllScreensNamesItem(val route:String,val label:String,val icon: ImageVector)

@Serializable
data class MyDoubt(val question_id:Int,val title:String,val question:String,val tags:List<String>,val timestamp:String)
data class PostDoubtItem(val username: String,val title:String,val question: String,val tags:List<String>)
data class Tags(val tags:List<String>)
data class Answer(val answer_id:Int,val answered_username:String,val answer_timestamp: String,val answer: String,val upvotes:Int,val downvotes:Int)
data class Vote(val add_to_upvote:Int=0,val add_to_downvote:Int=0,val answer_id: Int)
data class PostAnswerToQuestionItem(val question_id: Int,val answer: String,val answered_username: String)
@Serializable
data class Doubt(val username:String,val question_id:Int,val title:String,val question:String,val tags:List<String>,val timestamp:String)

val client= OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()


val retrofit= Retrofit.Builder()
    .baseUrl("http://192.168.29.195:5000")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val api=retrofit.create(ApiService::class.java)