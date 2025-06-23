package com.example.asknitt

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("check_credentials")
    fun CheckUsernameAndPassword(@Query("username") username:String,@Query("password") password:String):Call<CheckUser>

    @POST("register_user")
    fun RegisterUser(@Body user: User): Call<CheckSuccess>

    @GET("user_doubts")
    fun GetMyDoubts(@Query("username") username: String):Call<List<MyDoubt>>

    @POST("post_doubt")
    fun PostDoubt(@Body postDoubtItem: PostDoubtItem): Call<CheckSuccess>

    @GET("tags")
    fun GetAllTags():Call<Tags>

    @GET("answers")
    fun GetAnswers(@Query("question_id") question_id:Int): Call<List<Answer>>

    @POST("vote")
    fun VoteAnswer(@Body vote: Vote):Call<CheckSuccess>

    @POST("post_answer")
    fun PostAnswer(@Body postAnswerToQuestion: PostAnswerToQuestionItem): Call<CheckSuccess>

}