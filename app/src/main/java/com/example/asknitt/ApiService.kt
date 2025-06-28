package com.example.asknitt

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    fun Login(@Body user:User):Call<Token>

    @POST("signup")
    fun SignUp(@Body user: User):Call<Token>


    @GET("user_doubts")
    fun GetDoubts(@Query("username") username: String):Call<List<Doubt>>

    @POST("post_doubt")
    fun PostDoubt(@Body postDoubtItem: PostDoubtItem): Call<CheckSuccess>

    @GET("tags")
    fun GetAllTags():Call<Tags>

    @GET("answers")
    fun GetAnswers(@Query("question_id") question_id:Int): Call<List<Answer>>

    @POST("vote")
    fun VoteAnswer(@Body vote: Vote):Call<CheckSuccess>

    @POST("post_answer")
    fun PostAnswer(@Body postAnswerToQuestion: PostAnswerToDoubtItem): Call<CheckSuccess>

    @GET("recent_doubts")
    fun GetRecentQuestions():Call<List<Doubt>>

    @GET("user_info")
    fun GetUserInfo(): Call<UserInfo>

    @GET("questions_filter")
    fun GetDoubtsByFilter(@Query("search_text") search_text:String,
                     @Query("tags") tags:List<String>,
                     @Query("from_date") from_date:String,
                     @Query("to_date") to_date:String):Call<List<Doubt>>


}