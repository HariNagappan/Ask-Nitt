package com.example.asknitt

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewDoubtInDetail(doubt: Doubt, navController: NavController,mainViewModel: MainViewModel){
    //TODO add edit doubt later
    var should_show_post_answer by remember{mutableStateOf(false)}
    var scrollstate = rememberScrollState()
    var answer_text by remember { mutableStateOf("") }
    LaunchedEffect(answer_text) {
        if(answer_text!="")
            scrollstate.animateScrollTo(scrollstate.maxValue)
    }
    Box(modifier=Modifier.fillMaxSize().background(colorResource(R.color.black))){
        IconButton(onClick = {
            navController.navigateUp()
        },
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
            modifier= Modifier
                .align(Alignment.TopStart)
                .padding(top=dimensionResource(R.dimen.from_top_padding),start=dimensionResource(R.dimen.med_padding))
                .size(40.dp)
                .border(width = 2.dp, color = colorResource(R.color.electric_green), shape = CircleShape)
                .clip(CircleShape)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go Back",
                tint=colorResource(R.color.electric_green)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier= Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(top=dimensionResource(R.dimen.from_top_padding)*2,bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))
                .verticalScroll(scrollstate)
        ) {
            Text(
                text=doubt.title,
                lineHeight =50.sp,
                color=colorResource(R.color.electric_gold),
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.headings)),
                modifier=Modifier
                    .fillMaxWidth()
            )
            Column(modifier=Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.med_padding))) {
                Text(
                    text = "QUESTION:",
                    lineHeight = 36.sp,
                    color = colorResource(R.color.white),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.stripes)),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = doubt.question,
                    lineHeight = 36.sp,
                    color = colorResource(R.color.white),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier=Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TAGS:",
                    fontSize = 16.sp,
                    color=colorResource(R.color.electric_red),
                    fontFamily = FontFamily(Font(R.font.stripes)),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                CustomTagsPlainShower(tags = doubt.tags)
            }
            if(doubt.posted_username!=mainViewModel.username) {
                Text(
                    text = "Asked by ${doubt.posted_username}",
                    fontSize = 16.sp,
                    color = colorResource(R.color.electric_pink),
                    //fontFamily = FontFamily(Font(R.font.stripes)),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Text(
                text = "Posted On: ${GetUtcInLocalTime(doubt.question_timestamp)}",
                fontSize = 16.sp,
                color=colorResource(R.color.electric_green),
                //fontFamily = FontFamily(Font(R.font.stripes)),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier=Modifier.height(32.dp))
            Text(
                text="ANSWERS",
                color=colorResource(R.color.electric_gold),
                fontFamily = FontFamily(Font(R.font.foldable)),
                fontSize = 32.sp
                )
            Spacer(modifier=Modifier.height(16.dp))
            if(mainViewModel.cur_question_answers.isEmpty()){
                Spacer(modifier=Modifier.height(16.dp))
                Text(
                    text = "No Answers Yet",
                    color = colorResource(R.color.white),
                    fontFamily = FontFamily(Font(R.font.foldable)),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier=Modifier.fillMaxWidth()
                )
            }
            else{
                Text(
                    text = "${mainViewModel.cur_question_answers.size} Answers",
                    color = colorResource(R.color.electric_pink),
                    //fontFamily = FontFamily(Font(R.font.foldable)),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Left,
                    modifier=Modifier.fillMaxWidth()
                )
                //Spacer(modifier=Modifier.height(16.dp))
                for(answer in mainViewModel.cur_question_answers){
                    AnswerCard(
                        answer=answer,
                        mainViewModel=mainViewModel,
                        navController=navController
                    )
                }
            }
            Spacer(modifier=Modifier.height(32.dp))
            AnimatedVisibility(!should_show_post_answer) {
                Button(
                    onClick = {
                        should_show_post_answer = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.electric_green))
                ) {
                    Text(
                        text = "ADD AN ANSWER +",
                        color = colorResource(R.color.dark_gray),
                        fontFamily = FontFamily(Font(R.font.stripes)),
                        fontSize = 24.sp
                    )
                }
            }
            AnimatedVisibility(should_show_post_answer) {
                Log.d("general","showing add answer")
                AddAnswer(
                    question_id = doubt.question_id,
                    mainViewModel=mainViewModel,
                    answer_text = answer_text,
                    onClose = {
                        should_show_post_answer=false
                        navController.navigateUp()
                        navController.navigate(doubt)
                    },
                    navController = navController,
                    onValueChanged = {new_text->
                        if(new_text.length<=MAX_ANSWER_LENGTH) {
                            answer_text = new_text
                        }
                    }
                )
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnswerCard(answer: Answer, mainViewModel: MainViewModel,navController: NavController){
    Card(
        colors= CardDefaults.cardColors(containerColor = colorResource(R.color.dark_gray)),
        modifier=Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier=Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.med_padding))
        ){
            Text(
                text=answer.answer,
                fontSize = 16.sp,
                lineHeight = 36.sp,
                color=colorResource(R.color.white)
            )
            Spacer(modifier=Modifier.height(24.dp))
            Text(
                text="Answered by: ${answer.answered_username}",
                color=colorResource(R.color.electric_blue),
                fontSize = 14.sp,
                modifier=Modifier.align(Alignment.End)
            )
            Text(
                text="At ${GetUtcInLocalTime(answer.answer_timestamp)}",
                color=colorResource(R.color.electric_green),
                fontSize = 12.sp,
                modifier=Modifier.align(Alignment.End)
            )
            Spacer(modifier=Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier=Modifier
                .fillMaxWidth()){
                UpvoteDownVote(answer_id = answer.answer_id, upvotes = answer.upvotes, downvotes = answer.downvotes,mainViewModel=mainViewModel,navController=navController)
            }
        }
    }
}
@Composable
fun UpvoteDownVote(answer_id:Int,upvotes:Int,downvotes:Int,mainViewModel: MainViewModel,navController: NavController,modifier:Modifier=Modifier){
    var is_upvoted by remember { mutableStateOf(false) }
    var is_downvoted by remember { mutableStateOf(false) }
    var total_upvotes by remember { mutableStateOf(upvotes) }
    var total_downvotes by remember { mutableStateOf(downvotes) }
    val context= LocalContext.current
    var exp_sig by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = {
                mainViewModel.Vote(
                    answer_id=answer_id,
                    is_up_voted=is_upvoted,
                    is_down_voted=is_downvoted,
                    should_do_upvote = true,
                    changeUpVote = {to_be_added->
                        total_upvotes+=to_be_added
                    },
                    onFinish = {success,msg->
                        if(success){
                            Toast.makeText(context,"Successfully Voted", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            if(msg==context.getString(R.string.expired_signature)){
                                exp_sig=true
                            }
                            else{
                                Toast.makeText(context,"Error:$msg", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    changeDownVote = {to_be_added->
                        total_downvotes+=to_be_added
                    }
                )
                is_upvoted=true
                is_downvoted=false
            },
            shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.electric_green))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ArrowUpward,
                    contentDescription = "Upvote",
                    tint = colorResource(R.color.black)
                )
                Spacer(modifier=Modifier.width(8.dp))
                Text(
                    text=total_upvotes.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color=colorResource(R.color.black),
                )
            }
        }
        Button(
            onClick = {
                mainViewModel.Vote(
                    answer_id=answer_id,
                    is_up_voted=is_upvoted,
                    is_down_voted=is_downvoted,
                    should_do_upvote = false,
                    onFinish = {success,msg->
                        if(success){
                            Toast.makeText(context,"Successfully Voted", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            if(msg==context.getString(R.string.expired_signature)){
                                exp_sig=true
                            }
                            else{
                                Toast.makeText(context,"Error:$msg", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    changeUpVote = {to_be_added->
                        total_upvotes+=to_be_added
                    },
                    changeDownVote = {to_be_added->
                        total_downvotes+=to_be_added
                    }
                )
                is_upvoted=false
                is_downvoted=true
            },
            shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.electric_red))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ArrowDownward,
                    contentDescription = "Downvote",
                    tint = colorResource(R.color.white)
                )
                Spacer(modifier=Modifier.width(8.dp))
                Text(
                    text=total_downvotes.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color=colorResource(R.color.white),
                )
            }
        }
    }
    LaunchedEffect(exp_sig) {
        if(exp_sig){
            navController.navigate(AuthScreenRoutes.AUTH.name){
                popUpTo(MainScreenRoutes.MAIN.name){
                    inclusive=true
                }
            }
            Toast.makeText(context,"Session Expired,Please Login Again",Toast.LENGTH_LONG).show()
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewDoubtInDetailIntermediate(doubt: Doubt,navController: NavController,mainViewModel: MainViewModel){
    var issuccess by remember{ mutableStateOf(false) }
    var msg by remember{ mutableStateOf("") }
    var retry_number by remember{mutableStateOf(0)}
    LaunchedEffect(retry_number) {
        mainViewModel.GetAnswersByQuestionId(
            question_id = doubt.question_id,
            onFinish = {success,new_msg->
                issuccess=success
                msg=new_msg
                Log.d("apisuccess","executed")
            })
    }
    Box(modifier=Modifier.fillMaxSize()){
        if(!issuccess && msg==""){
            CircularProgressIndicator(modifier=Modifier.align(Alignment.Center),color=colorResource(R.color.electric_green))
        }
        else if (!issuccess && msg!=""){
            Column(modifier=Modifier.align(Alignment.Center).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text="$msg",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.foldable)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color=colorResource(R.color.electric_red)
                )
                Button(onClick = {
                    retry_number+=1
                    issuccess=false
                    msg=""
                },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.electric_green))
                ) {
                    Text(
                        text="RETRY",
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.foldable)),
                        fontWeight = FontWeight.Bold,
                        color=colorResource(R.color.dark_gray)
                    )
                }
            }
        }
        else{
            ViewDoubtInDetail(
                doubt = doubt,
                navController=navController,
                mainViewModel=mainViewModel
            )
        }
    }
}