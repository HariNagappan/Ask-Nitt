package com.example.asknitt

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    suspend fun Login(@Body user:User):Response<Token>

    @POST("signup")
    suspend fun SignUp(@Body user: User):Response<Token>


    @GET("user_doubts")
    suspend fun GetDoubts(@Query("username") username: String): Response<List<Doubt>>

    @POST("post_doubt")
    suspend fun PostDoubt(@Body postDoubtItem: PostDoubtItem): Response<CheckSuccess>

    @GET("tags")
    suspend fun GetAllTags(): Response<Tags>

    @GET("answers")
    suspend fun GetAnswers(@Query("question_id") question_id:Int): Response<List<Answer>>

    @POST("vote")
    suspend fun VoteAnswer(@Body vote: Vote):Response<CheckSuccess>

    @POST("post_answer")
    suspend fun PostAnswer(@Body postAnswerToQuestion: PostAnswerToDoubtItem): Response<CheckSuccess>

    @GET("recent_doubts")
    suspend fun GetRecentQuestions():Response<List<Doubt>>

    @GET("current_user_info")
    suspend fun GetCurrentUserInfo(): Response<CurrentUserInfo>

    @GET("questions_filter")
    suspend fun GetDoubtsByFilter(@Query("search_text") search_text: String,
                                  @Query("tags") tags: MutableList<String>?,
                                  @Query("from_date") from_date: String,
                                  @Query("to_date") to_date: String,
                                  @Query("status") status: String
    ):Response<List<Doubt>>
    @GET("get_users")
    suspend fun GetUsersByName(@Query("username_search_text") username_search_text:String):Response<List<GeneralUser>>

    @GET("user_info")
    suspend fun GetOtherUserInfo(@Query("other_username") other_username:String):Response<OtherUserInfo>

    @POST("send_friend_request")
    suspend fun SendFriendRequest(@Body generalUser: GeneralUser):Response<CheckSuccess>

    @POST("accept_friend_request")
    suspend fun AcceptFriendRequest(@Body generalUser: GeneralUser):Response<CheckSuccess>

    @POST("decline_friend_request")
    suspend fun DeclineFriendRequest(@Body generalUser: GeneralUser):Response<CheckSuccess>

    @GET("users_friends")
    suspend fun GetUsersFriends():Response<List<GeneralUser>>

    @GET("user_friend_request_recieved")
    suspend fun GetUserFriendRequestsRecieved():Response<List<GeneralUser>>

    @GET("user_friend_request_sent")
    suspend fun GetUserFriendRequestsSent():Response<List<GeneralUser>>

    @POST("mark_doubt_solved")
    suspend fun MarkQuestionSolved(@Body markQuestionSolvedItem: MarkQuestionSolvedItem):Response<CheckSuccess>

    @POST("upload_files_for_doubt")
    @Multipart
    suspend fun UploadFilesForDoubt(@Part files:List<MultipartBody.Part>):Response<CheckSuccess>

    @POST("upload_files_for_answer")
    @Multipart
    suspend fun UploadFilesForAnswer(@Part files:List<MultipartBody.Part>):Response<CheckSuccess>

}