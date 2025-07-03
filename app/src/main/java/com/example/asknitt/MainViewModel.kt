package com.example.asknitt

import android.R.attr.data
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import gen._base._base_java__assetres.srcjar.R.id.info
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate
import kotlin.collections.map
import kotlin.to

@SuppressLint("NewApi")
class MainViewModel: ViewModel() {
    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    val all_users:MutableList<GeneralUser> =mutableStateListOf()
    var other_user_info: OtherUserInfo?=null

    var user_doubts: MutableList<Doubt> =mutableStateListOf()
    var recent_doubts: MutableList<Doubt> =mutableStateListOf()
    var filtered_doubts:MutableList<Doubt> =mutableStateListOf()
    var users_friends:MutableList<GeneralUser> =mutableStateListOf()
    var user_friend_requests_recieved:MutableList<GeneralUser> =mutableStateListOf()
    var user_friend_requests_sent:MutableList<GeneralUser> =mutableStateListOf()


    val tags: MutableList<String> =mutableStateListOf() 
    var cur_question_tags: MutableList<String> =mutableStateListOf()
    var search_question_tags: MutableList<String> =mutableStateListOf()

    var cur_question_answers: MutableList<Answer> =mutableStateListOf()
    var user_questions_asked by mutableStateOf(0)
    var user_questions_helped by mutableStateOf(0)

    var from_date by mutableStateOf(LocalDate.now())
    var to_date by mutableStateOf(LocalDate.now())
    var joined_on by mutableStateOf("")


    fun SetUsername(new_username: String) {
        username = new_username
    }

    fun SetPassword(new_password: String) {
        password = new_password
    }

    fun LoginUser(context: Context, onFinish: (Boolean, String) -> Unit){
        val call=api.Login(User(username=username,password=password))
        call.enqueue(object:Callback<Token>{
            override fun onResponse(call: Call<Token?>, response: Response<Token?>) {
                if(response.isSuccessful){
                    val data=response.body()
                    if(data!=null) {
                        JWT_TOKEN = data.token
                        if(JWT_TOKEN!="") {
                            SaveJWTToken(context = context)
                            Log.d("apisuccess", "From LoginUser():${data.msg}")
                            onFinish(true, "")
                        }
                        else {
                            onFinish(false, data.msg)
                        }
                    }
                }
                else{
                    Log.d("apifailure", "From LoginUser():${response.message()}")
                    onFinish(false,response.message())
                }
            }

            override fun onFailure(call: Call<Token?>, t: Throwable) {
                Log.d("apifailure", "From LoginUser():${t.message}")
                onFinish(false,t.message.toString())
            }

        })
    }
    fun SignUpUser(context: Context, onFinish: (Boolean, String) -> Unit) {
        val call = api.SignUp(User(username = username, password = password))
        call.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if(response.isSuccessful){
                    val data=response.body()
                    if(data!=null) {
                        if(data.token==""){
                            onFinish(false, "Username already exists")
                        }
                        else {
                            JWT_TOKEN = data.token
                            SaveJWTToken(context = context)
                            Log.d("apisuccess", "From RegisterNewUser():${data.msg}")
                            onFinish(true, "")
                        }
                    }
                }
                else{
                    Log.d("apifailure", "From RegisterNewUser():${response.message()}")
                    onFinish(false,response.message())
                }
            }
            override fun onFailure(call: Call<Token>, t: Throwable) {
                Log.d("apifailure", "From RegisterNewUser():${t.message}")
                onFinish(false,t.message.toString())
            }
        })
    }
    fun Logout(context: Context,onFinish: (Boolean, String) -> Unit){
        DeleteJWTToken(context=context)
        onFinish(true,"")
    }
    fun GetCurrentUserInfo(onFinish: (Boolean, String) -> Unit){
        val call=api.GetCurrentUserInfo()
        call.enqueue(object:Callback<CurrentUserInfo>{
            override fun onResponse(call: Call<CurrentUserInfo>, response: Response<CurrentUserInfo>) {
                if(response.isSuccessful){
                    val info=response.body()
                    if(info!=null) {
                        if (info.error_msg == "" || info.error_msg==null) {
                            user_questions_asked = info.questions_asked
                            user_questions_helped = info.people_helped
                            username = info.username
                            joined_on=GetUtcInLocalTime(info.joined_on)
                            onFinish(true, "")
                        } else {
                            onFinish(false, info.error_msg)
                        }
                    }
                    else{
                        onFinish(false, "Error, could not fetch info")
                    }
                }
                else{
                    Log.d("apifailure","this is from GetCurrentUserInfo(), ${response.message()}")
                    onFinish(false,"Error:${response.message()}")
                }
            }

            override fun onFailure(call: Call<CurrentUserInfo>, t: Throwable) {
                Log.d("apifailure","this is from GetCurrentUserInfo(), ${t.message}")
                onFinish(false,"Error:${t.message}")
            }

        })
    }
    fun GetUsersByName(username_search_text:String, onFinish: (Boolean, String) -> Unit){
        val call=api.GetUsersByName(username_search_text = username_search_text)
        call.enqueue(object:Callback<List<GeneralUser>>{
            override fun onResponse(call: Call<List<GeneralUser>?>, response: Response<List<GeneralUser>?>) {
                if(response.isSuccessful){
                    val lst=response.body()
                    all_users.clear()
                    if(lst!=null){
                        all_users.addAll(lst)
                        onFinish(true,"")
                    }
                    else{
                        onFinish(false,"Error,Could not fetch users data")
                    }
                }
                else{
                    Log.d("apifailure","this is from GetUsersByName(), ${response.message()}")
                    onFinish(false,"Error:${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<GeneralUser>?>, t: Throwable) {
                Log.d("apifailure","this is from GetUsersByName(), ${t.message}")
                onFinish(false,"Error:${t.message}")
            }
        })
    }
    fun GetOtherUserInfo(other_username:String, onFinish: (Boolean, String) -> Unit){
        val call=api.GetOtherUserInfo(other_username = other_username)
        call.enqueue(object:Callback<OtherUserInfo>{
            override fun onResponse(call: Call<OtherUserInfo?>, response: Response<OtherUserInfo?>) {
                if(response.isSuccessful){
                    val info=response.body()
                    if(info!=null) {
                        if (info.error_msg == "" || info.error_msg==null) {
                            other_user_info=info
                            onFinish(true, "")
                        } else {
                            onFinish(false, info.error_msg)
                        }
                    }
                    else{
                        onFinish(false, "Error, could not fetch info")
                    }
                }
            }
            override fun onFailure(call: Call<OtherUserInfo?>, t: Throwable) {
                Log.d("apifailure","this is from GetOtherOtherUserInfo(), ${t.message}")
                onFinish(false,"Error:${t.message}")
            }
        })
    }

    fun GetDoubtsByUsername(username:String, onFinish: (Boolean, String) -> Unit){
        val cal=api.GetDoubts(username)
        cal.enqueue(object:Callback<List<Doubt>>{
            override fun onResponse(call: Call<List<Doubt>?>, response: Response<List<Doubt>?>) {
                    if(response.isSuccessful){
                        val lst=response.body()
                        user_doubts.clear()
                        if(lst!=null) {
                            user_doubts.addAll(lst)
                            onFinish(true,"")
                            Log.d("apisuccess","this is from GetUserDoubts(), Successfully got user doubts,$user_doubts")

                        }
                        else{
                            Log.d("apifailure","this is from GetUserDoubts(), message:response.body is empty")
                            onFinish(false,response.message())
                        }
                    }
            }
            override fun onFailure(call: Call<List<Doubt>?>,t: Throwable) {
                Log.d("apifailure","this is from GetUserDoubts(), message:server error")
                onFinish(false,"${t.message}")
            }
        })
    }
    fun PostUserDoubt(title:String, question:String,onFinish: (Boolean, String) -> Unit):Boolean{
        val call=api.PostDoubt(PostDoubtItem(username=username, title = title,question=question,tags=cur_question_tags))
        Log.d("general","from PostUerDoubt:tags:$tags")
        var success=false
        call.enqueue(object:Callback<CheckSuccess>{
            override fun onResponse(
                call: Call<CheckSuccess?>,
                response: Response<CheckSuccess?>
            ) {
                if(response.isSuccessful){
                    onFinish(true,"Successfully Posted Question")
                    ClearCurrentQuestionTags()
                }
                else{
                    onFinish(false,"Error Posting Question:${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<CheckSuccess?>,
                t: Throwable
            ) {
                onFinish(false,"Server error, please try again later")
            }
        })
        return success
    }
    fun GetRecentDoubts(onFinish: (Boolean, String) -> Unit){
        val call=api.GetRecentQuestions()
        call.enqueue(object : Callback<List<Doubt>>{
            override fun onResponse(call: Call<List<Doubt>?>, response: Response<List<Doubt>?>) {
                if(response.isSuccessful){
                    val lst=response.body()
                    recent_doubts.clear()
                    if(lst!=null) {
                        recent_doubts.addAll(lst)

                    }
                    Log.d("apisuccess","successfully retrieved recent_doubts:.$recent_doubts")
                    onFinish(true,"")
                }
                else{
                    Log.d("apifailure","this is from GetResponse(), ${response.message()}")
                    onFinish(false,"Error:${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Doubt>?>, t: Throwable) {
                Log.d("apifailure","this is from GetResponse(), ${t.message}")
                onFinish(false,"Error:${t.message}")
            }

        })
    }
    fun SearchDoubts(search_text:String, onFinish: (Boolean, String) -> Unit){
        val call=api.GetDoubtsByFilter(
            search_text=search_text,
            tags=search_question_tags,
            from_date=GetLocalInUTC(from_date.toString(), start_of_day = true),
            to_date=GetLocalInUTC(to_date.toString(), start_of_day = true
            ))
        call.enqueue(object:Callback<List<Doubt>>{
            override fun onResponse(call: Call<List<Doubt>>, response: Response<List<Doubt>>) {
                if(response.isSuccessful){
                    val lst=response.body()
                    if(lst!=null){
                        filtered_doubts.clear()
                        filtered_doubts.addAll(lst)
                        onFinish(true,"")
                        Log.d("apisuccess","Successfully searched doubts:$filtered_doubts")
                    }
                    else{
                        onFinish(false,"Could not get data,Please try again later")
                        Log.d("apifailure","this is from SearchDoubts(), ${response.message()}")
                    }
                }
                else{
                    onFinish(false,response.message())
                    Log.d("apifailure","this is from SearchDoubts(), ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Doubt>>, t: Throwable) {
                onFinish(false,"${t.message}")
                Log.d("apifailure","this is from SearchDoubts(), ${t.message}")
            }
        })
    }


    fun ClearCurrentQuestionTags(){
        cur_question_tags.clear()
    }
    fun GetTags(onFinish: (Boolean, String) -> Unit){
        val call=api.GetAllTags()
        call.enqueue(object: Callback<Tags>{
            override fun onResponse(call: Call<Tags>, response: Response<Tags>
            ) {
                if(response.isSuccessful){
                    val tmp=response.body()
                    val all_tags=tmp?.tags
                    tags.clear()
                    tags.addAll(all_tags!!)
                    onFinish(true,"")
                }
                else{
                    Log.d("apifailure","From GetTags(): failed all tags,no clear response")
                    onFinish(false,response.message())
                }
            }

            override fun onFailure(call: Call<Tags>, t: Throwable
            ) {
                Log.d("apifailure","From GetTags(): failed all tags,server error,${t.message}")
                onFinish(false,"${t.message}")
            }

        })
    }
    fun Vote(answer_id:Int,should_do_upvote:Boolean,is_up_voted:Boolean,is_down_voted:Boolean,changeUpVote:(Int)->Unit,changeDownVote:(Int)->Unit,onFinish: (Boolean, String) -> Unit){
        var add_to_upvote=0
        var add_to_downvote=0
        if(is_up_voted){
            if(should_do_upvote==false){
                add_to_downvote=1
                add_to_upvote=-1
            }
        }
        else if(is_down_voted){
            if(should_do_upvote){
                add_to_downvote=-1
                add_to_upvote=1
            }
        }
        else{
            if(should_do_upvote)
                add_to_upvote=1
            else
                add_to_downvote=1
        }
        changeUpVote(add_to_upvote)
        changeDownVote(add_to_downvote)
        val call=api.VoteAnswer(Vote(answer_id=answer_id, add_to_upvote = add_to_upvote, add_to_downvote = add_to_downvote))
        call.enqueue(object:Callback<CheckSuccess>{
            override fun onResponse(call: Call<CheckSuccess?>, response: Response<CheckSuccess?>) {
                if(response.isSuccessful){
                    val tmp=response.body()
                    if(tmp!=null) {
                        if(tmp.error_msg=="" || tmp.error_msg==null) {
                            Log.d("apisuccess", "From Vote(): Successfully Voted")
                            onFinish(true,"")
                        }
                        else{
                            onFinish(false,tmp.error_msg)
                        }
                    }
                    else{
                        onFinish(false,"got null response")
                    }
                }
                else{
                    onFinish(false,response.message())
                    Log.d("apifailure","From Vote(): ${response.message()}")
                }
            }
            override fun onFailure(call: Call<CheckSuccess?>, t: Throwable) {
                Log.d("apifailure","From Vote(): ${t.message}")
                onFinish(false,"${t.message}")
            }
        })
    }

    fun PostAnswer(question_id: Int,answer: String,onFinish: (Boolean, String) -> Unit){
        val call=api.PostAnswer(PostAnswerToDoubtItem(question_id=question_id,answer=answer, answered_username = username))
        call.enqueue(object:Callback<CheckSuccess>{
            override fun onResponse(call: Call<CheckSuccess?>, response: Response<CheckSuccess?>) {
                if(response.isSuccessful){
                    val tmp=response.body()
                    if(tmp!=null) {
                        if (tmp.error_msg == "" || tmp.error_msg == null) {
                            Log.d("apisuccess", "From PostAnswer(): Successfully Posted Answer")
                            onFinish(true, "")
                        }
                        else{
                            onFinish(false,tmp.error_msg)
                        }
                    }
                    else{
                        onFinish(false,"got null response")
                    }
                }
                else{
                    Log.d("apifailure","From PostAnswer(): ${response.message()}")
                    onFinish(false,response.message())
                }
            }
            override fun onFailure(call: Call<CheckSuccess?>, t: Throwable) {
                Log.d("apifailure","From PostAnswer(): ${t.message}")
                onFinish(false,"${t.message}")
            }
        })
    }
    fun GetAnswersByQuestionId(question_id:Int,onFinish:(Boolean,String)->Unit){
        val call=api.GetAnswers(question_id)
        call.enqueue(object:Callback<List<Answer>>{
            override fun onResponse(call: Call<List<Answer>?>, response: Response<List<Answer>?>) {
                if(response.isSuccessful){
                    val lst=response.body()
                    cur_question_answers.clear()
                    if(lst!=null) {
                        cur_question_answers.addAll(lst)
                        onFinish(true,"success")
                    }
                    else{
                        onFinish(false,"could not get questions")
                    }

                    Log.d("apisuccess","this is from GetAnswers(), successfully gotten answers")

                }
                else{
                    Log.d("apifailure","this is from GetAnswers(), message:response.body is empty")
                    onFinish(false,"Error:${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Answer>?>, t: Throwable) {
                Log.d("apifailure","this is from GetAnswers(), message:${t.message}")
                onFinish(false,"Error:${t.message}")
            }

        })
    }

    fun GetUserFriends(onFinish: (Boolean, String) -> Unit){
        val call=api.GetUsersFriends()
        call.enqueue(object:Callback<List<GeneralUser>>{
            override fun onResponse(call: Call<List<GeneralUser>?>, response: Response<List<GeneralUser>?>) {
                if(response.isSuccessful){
                    val lst=response.body()
                    users_friends.clear()
                    if(lst!=null) {
                        users_friends.addAll(lst)
                        onFinish(true,"success")
                    }
                    else{
                        onFinish(false,"Could not get friends")
                    }
                    Log.d("apisuccess","this is from GetUserFriends(), successfully gotten friends")

                }
                else{
                    Log.d("apifailure","this is from GetUserFriends(), message:response.body is empty")
                    onFinish(false,"Error:${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<GeneralUser>?>, t: Throwable) {
                onFinish(false,"${t.message}")
            }
        })
    }
    fun GetUserRecievedFriendRequests(onFinish: (Boolean, String) -> Unit){
        val call=api.GetUserFriendRequestsRecieved()
        call.enqueue(object:Callback<List<GeneralUser>>{
            override fun onResponse(call: Call<List<GeneralUser>?>, response: Response<List<GeneralUser>?>) {
                if(response.isSuccessful){
                    val lst=response.body()
                    user_friend_requests_recieved.clear()
                    if(lst!=null) {
                        user_friend_requests_recieved.addAll(lst)
                        onFinish(true,"success")
                    }
                    else{
                        onFinish(false,"Could not get requests")
                    }
                    Log.d("apisuccess","this is from GetUserRecievedFriendRequests(), successfully gotten requests")

                }
                else{
                    Log.d("apifailure","this is from GetUserRecievedFriendRequests(), message:response.body is empty")
                    onFinish(false,"Error:${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<GeneralUser>?>, t: Throwable) {
                onFinish(false,"Error:${t.message}")
            }
        })
    }
    fun GetUserSentFriendRequests(onFinish: (Boolean, String) -> Unit){
        val call=api.GetUserFriendRequestsSent()
        call.enqueue(object:Callback<List<GeneralUser>>{
            override fun onResponse(call: Call<List<GeneralUser>?>,response: Response<List<GeneralUser>?>) {
                if(response.isSuccessful){
                    val lst=response.body()
                    user_friend_requests_sent.clear()
                    if(lst!=null) {
                        user_friend_requests_sent.addAll(lst)
                        onFinish(true,"success")
                    }
                    else{
                        onFinish(false,"Could not get requests")
                    }
                    Log.d("apisuccess","this is from GetUserSentFriendRequests(), successfully gotten requests")
                }
                else{
                    Log.d("apifailure","this is from GetUserSentFriendRequests(), message:response.body is empty")
                    onFinish(false,"Error:${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<GeneralUser>?>, t: Throwable) {
                onFinish(false,"Error:${t.message}")
            }
        })
    }
    fun SendFriendRequest(other_username: String,onFinish: (Boolean, String) -> Unit){
        val call=api.SendFriendRequest(GeneralUser(other_username))
        call.enqueue(object:Callback<CheckSuccess>{
            override fun onResponse(call: Call<CheckSuccess?>, response: Response<CheckSuccess?>) {
                if (response.isSuccessful) {
                    val checkSuccess = response.body()
                    if (checkSuccess != null) {
                        if (checkSuccess.error_msg == "" || checkSuccess.error_msg == null) {
                            onFinish(true, "")
                        } else {
                            onFinish(false, checkSuccess.error_msg)
                        }
                    } else {
                        onFinish(false, "Error, could not fetch checkSuccess")
                    }
                }
                else{
                    onFinish(false,response.message())
                }
            }
            override fun onFailure(call: Call<CheckSuccess?>, t: Throwable) {
                Log.d("apifailure", "From SendFriendRequest:${t.message}")
                onFinish(false,"${t.message}")
            }
        })
    }
    fun AcceptFriendRequest(other_username: String,onFinish: (Boolean, String) -> Unit){
        val call=api.AcceptFriendRequest(GeneralUser(username=other_username))
        call.enqueue(object:Callback<CheckSuccess>{
            override fun onResponse(call: Call<CheckSuccess?>, response: Response<CheckSuccess?>) {
                if (response.isSuccessful) {
                    val checkSuccess = response.body()
                    if (checkSuccess != null) {
                        if (checkSuccess.error_msg == "" || checkSuccess.error_msg == null) {
                            onFinish(true, "")
                        } else {
                            onFinish(false, checkSuccess.error_msg)
                        }
                    } else {
                        onFinish(false, "Error, could not fetch checkSuccess")
                    }
                }
                else{
                    onFinish(false,response.message())
                }
            }
            override fun onFailure(call: Call<CheckSuccess?>,t: Throwable) {
                Log.d("apifailure", "From AcceptFriendRequest:${t.message}")
                onFinish(false,"${t.message}")
            }
        })
    }
    fun DeclineFriendRequest(other_username: String,onFinish: (Boolean, String) -> Unit){
        val call=api.DeclineFriendRequest(GeneralUser(username=other_username))
        call.enqueue(object : Callback<CheckSuccess>{
            override fun onResponse(call: Call<CheckSuccess?>, response: Response<CheckSuccess?>) {
                if (response.isSuccessful) {
                    val checkSuccess = response.body()
                    if (checkSuccess != null) {
                        if (checkSuccess.error_msg == "" || checkSuccess.error_msg == null) {
                            onFinish(true, "")
                        } else {
                            onFinish(false, checkSuccess.error_msg)
                        }
                    } else {
                        onFinish(false, "Error, could not fetch checkSuccess")
                    }
                }
                else{
                    onFinish(false,response.message())
                }
            }

            override fun onFailure(call: Call<CheckSuccess?>, t: Throwable) {
                Log.d("apifailure", "From DeclineFriendRequest:${t.message}")
                onFinish(false,"${t.message}")
            }

        })
    }


    fun SaveJWTToken(context: Context){
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val prefs = EncryptedSharedPreferences.create(
            SHARED_PREFS_FILENAME, // filename
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val edit=prefs.edit()
        edit.putString("JWTToken",JWT_TOKEN)
        edit.apply()
    }
    fun DeleteJWTToken(context: Context){
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val prefs = EncryptedSharedPreferences.create(
            SHARED_PREFS_FILENAME, // filename
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val edit=prefs.edit()
        edit.putString("JWTToken","")
        edit.apply()
    }
}