package com.example.asknitt.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.asknitt.data.remote.ApiService
import kotlinx.serialization.Serializable
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
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


val privacy_modes=listOf("PRIVATE","FRIENDS ONLY","PUBLIC")
var JWT_TOKEN=""
val SHARED_PREFS_FILENAME_ENCRYPTED="ASKNITT"
val SHARED_PREFS_FILENAME_NORMAL="ASKNITT_NORMAL"
val MULTIPARTBODY_FILE_KEY="files"
val BASE_URL="http://192.168.31.22:5000"
