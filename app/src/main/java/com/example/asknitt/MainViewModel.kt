package com.example.asknitt

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import kotlin.collections.map

class MainViewModel: ViewModel() {
    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var should_auto_login =false
    
    var user_doubts: MutableList<Doubt> =mutableStateListOf()
    var recent_doubts: MutableList<Doubt> =mutableStateListOf()

    val tags: MutableList<String> =mutableStateListOf() 
    var cur_question_tags: MutableList<String> =mutableStateListOf()
    var cur_question_answers: MutableList<Answer> =mutableStateListOf()
    var user_questions_asked by mutableStateOf(0)
    var user_questions_helped by mutableStateOf(0)
    fun SetUsername(new_username: String) {
        username = new_username
    }

    fun SetPassword(new_password: String) {
        password = new_password
    }

    fun RegisterNewUser() {
        val call = api.RegisterUser(User(username = username, password = password))
        call.enqueue(object : Callback<CheckSuccess> {
            override fun onResponse(call: Call<CheckSuccess>, response: Response<CheckSuccess>) {
                //do something
            }
            override fun onFailure(call: Call<CheckSuccess>, t: Throwable) {
                Log.d("apifailure", "error_msg:${t.message}")
            }
        })
    }
    fun SaveAutoLogin(auto_login:Boolean, context: Context){
        should_auto_login=auto_login
        val prefs=context.getSharedPreferences(shared_prefs_filename, Context.MODE_PRIVATE)
        val edit=prefs.edit()
        edit.putBoolean("auto_login",should_auto_login)
        edit.putString("username",username)
        edit.putString("password",password)
        edit.apply()
    }
    fun GetDoubts(username:String, onFinish: (Boolean, String) -> Unit){
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
    fun PostUserDoubt(title:String, question:String,onResult:(Boolean,String) ->Unit):Boolean{
        val call=api.PostDoubt(PostDoubtItem(username=username, title = title,question=question,tags=cur_question_tags))
        Log.d("general","from PostUerDoubt:tags:$tags")
        var success=false
        call.enqueue(object:Callback<CheckSuccess>{
            override fun onResponse(
                call: Call<CheckSuccess?>,
                response: Response<CheckSuccess?>
            ) {
                if(response.isSuccessful){
                    onResult(true,"Successfully Posted Question")
                    ClearCurrentQuestionTags()
                }
                else{
                    onResult(false,"Error Posting Question:${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<CheckSuccess?>,
                t: Throwable
            ) {
                onResult(false,"Server error, please try again later")
            }
        })
        return success
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
    fun Vote(answer_id:Int,should_do_upvote:Boolean,is_up_voted:Boolean,is_down_voted:Boolean,changeUpVote:(Int)->Unit,changeDownVote:(Int)->Unit){
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
                    Log.d("apisuccess","From Vote(): Successfully Voted")
                }
                else{
                    Log.d("apifailure","From Vote(): ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CheckSuccess?>, t: Throwable) {
                Log.d("apifailure","From Vote(): ${t.message}")
            }
        })
    }
    fun PostAnswer(question_id: Int,answer: String,onFinish: (Boolean, String) -> Unit){
        val call=api.PostAnswer(PostAnswerToDoubtItem(question_id=question_id,answer=answer, answered_username = username))
        call.enqueue(object:Callback<CheckSuccess>{
            override fun onResponse(call: Call<CheckSuccess?>, response: Response<CheckSuccess?>) {
                if(response.isSuccessful){
                    Log.d("apisuccess","From PostAnswer(): Successfully Posted Answer")
                    onFinish(true,"")
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
                    }
                    onFinish(true,"success")
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
    fun GetUserInfo(username: String, onFinish: (Boolean, String) -> Unit){
        val call=api.GetUserInfo(username)
        call.enqueue(object:Callback<UserInfo>{
            override fun onResponse(call: Call<UserInfo?>, response: Response<UserInfo?>) {
                if(response.isSuccessful){
                    val info=response.body()
                    if(info!=null) {
                        user_questions_asked = info.questions_asked
                        user_questions_helped=info.people_helped
                    }
                    onFinish(true,"")
                }
                else{
                    Log.d("apifailure","this is from GetResponse(), ${response.message()}")
                    onFinish(false,"Error:${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserInfo?>, t: Throwable) {
                Log.d("apifailure","this is from GetUserInfo(), ${t.message}")
                onFinish(false,"Error:${t.message}")
            }

        })
    }
}