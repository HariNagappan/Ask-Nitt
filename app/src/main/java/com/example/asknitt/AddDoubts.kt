package com.example.asknitt

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@Composable
fun AddDoubtScreen(mainViewModel: MainViewModel,navController: NavController,modifier: Modifier=Modifier){
    var title_text by remember{mutableStateOf("")}
    var question_text by remember { mutableStateOf("") }
    var tag_search_text by remember { mutableStateOf("") }
    val scollState=rememberScrollState()

    var should_show_search_field by remember{mutableStateOf(false)}
    var tag_search_focused by remember { mutableStateOf(false) }

    var show_title_question_error_dialog by remember{mutableStateOf(false)}
    var show_title_error_dialog by remember{mutableStateOf(false)}
    var show_question_error_dialog by remember{mutableStateOf(false)}
    var should_show_intermediate_screen by remember { mutableStateOf(false) }
    var can_edit_title by remember { mutableStateOf(true) }
    var can_edit_question by remember { mutableStateOf(true) }
    var can_edit_tags by remember { mutableStateOf(true) }

    val context=LocalContext.current
    LaunchedEffect(question_text) {
        scollState.animateScrollTo(scollState.maxValue)
    }
    Box(modifier=Modifier
        .fillMaxSize()
        .background(color=Color.Black)){
        Column(verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier=Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))
                .imePadding()
                .verticalScroll(scollState)
        ) {
            Row(modifier=Modifier.fillMaxWidth()){
                IconButton(onClick = {
                    navController.navigateUp()
                    mainViewModel.ClearCurrentQuestionTags()
                },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                    modifier= Modifier
                        .size(40.dp)
                        .border(width = 2.dp, color = colorResource(R.color.electric_green), shape = CircleShape)
                        .clip(CircleShape)) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        tint=colorResource(R.color.electric_green)
                    )
                }
                Spacer(modifier=Modifier.width(16.dp))
                Text(
                    text="POST QUESTION",
                    fontSize = 32.sp,
                    color=colorResource(R.color.electric_gold),
                    fontFamily = FontFamily(Font(R.font.headings)),
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    modifier=Modifier.offset(x=-16.dp)//TODO dont use offset(this is temporary)
                )
            }
            Column(modifier=Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TITLE",
                    fontSize = 16.sp,
                    color = colorResource(R.color.electric_red),
                    fontFamily = FontFamily(Font(R.font.stripes)),
                    textAlign = TextAlign.Center,
                )
                CustomOutlineTextField(
                    cur_text = title_text,
                    enabled = can_edit_title,
                    singleLine=true,
                    onValueChanged = {new_text->
                        if(new_text.length<=MAX_TITLE_LENGTH){
                            title_text=new_text
                        }
                    },
                    modifier=Modifier.fillMaxWidth())
            }
            Column(modifier=Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "QUESTION",
                    fontSize = 16.sp,
                    color = colorResource(R.color.electric_red),
                    fontFamily = FontFamily(Font(R.font.stripes)),
                    textAlign = TextAlign.Center,
                )
                CustomOutlineTextField(
                    cur_text = question_text,
                    enabled = can_edit_question,
                    singleLine=false,
                    onValueChanged = {new_text->
                        if(new_text.length<=MAX_QUESTION_LENGTH){
                            question_text=new_text
                        }
                    },
                    modifier=Modifier.fillMaxWidth())
            }
            Column(modifier=Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_padding)),modifier=Modifier.fillMaxWidth()) {
                    Text(
                        text = "TAGS",
                        fontSize = 16.sp,
                        color = colorResource(R.color.electric_red),
                        fontFamily = FontFamily(Font(R.font.stripes)),
                        textAlign = TextAlign.Center,
                    )
                    AnimatedVisibility(should_show_search_field) {
                        SearchTextField(
                            cur_text = tag_search_text,
                            singleLine = true,
                            onValueChanged = { new_text ->
                                if (new_text.length <= MAX_TAG_LENGTH) {
                                    tag_search_text = new_text
                                }
                            },
                            modifier = Modifier
                                .height(36.dp)
                                .background(
                                    colorResource(R.color.dark_gray),
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .border(
                                    width = 3.dp,
                                    color = colorResource(R.color.electric_pink),
                                    shape = RoundedCornerShape(32.dp)
                                )
                                .padding(8.dp)
                                .focusRequester(FocusRequester())
                                .onFocusChanged { cur_focus_state ->
                                    tag_search_focused = cur_focus_state.isFocused
                                }
                        )
                    }


                    if(!should_show_search_field){
                        Spacer(modifier=Modifier.weight(1f))
                    }
                    IconButton(onClick = {
                        should_show_search_field=!should_show_search_field
                    },
                        enabled = can_edit_tags,
                        modifier=Modifier.size(24.dp).zIndex(2f)
                    ) {
                        Icon(
                            imageVector = if(!should_show_search_field) Icons.Default.Search else Icons.Default.SearchOff,
                            contentDescription = "Search",
                            tint = colorResource(R.color.electric_green)
                        )
                    }
                }
                AnimatedVisibility(tag_search_focused) {
                    CustomTagsSuggestionShower(
                        cur_text = tag_search_text,
                        mainViewModel = mainViewModel,
                        exclude = mainViewModel.cur_question_tags
                    )
                }
            }
            CustomTagsShowerRemovable(mainViewModel=mainViewModel)

            Row(modifier=Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                Button(onClick = {
                    if(title_text.trim().isEmpty() && question_text.trim().isEmpty()){
                        show_title_question_error_dialog=true
                    }
                    else if(title_text.trim().isEmpty()){
                        show_title_error_dialog=true
                    }
                    else if(question_text.trim().isEmpty()){
                        show_question_error_dialog=true
                    }
                    else{
                        should_show_intermediate_screen=true
                    }
                },
                    colors= ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.electric_green)
                    )
                ){
                    Text(
                        text="SUBMIT QUESTION",
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.foldable)),
                        fontWeight = FontWeight.Bold,
                        color=colorResource(R.color.dark_gray)
                    )
                }
            }
        }
    }
    if(show_title_question_error_dialog){
        ErrorDialogDismissOnly(
            title = "Error",
            body = "Please enter a valid question and title",
            onDismiss = {
                show_title_question_error_dialog=false
            }
        )
    }
    if(show_title_error_dialog){
        ErrorDialogDismissOnly(
            title = "Error",
            body = "Please enter a valid title",
            onDismiss = {
                show_title_error_dialog=false
            }
        )
    }
    if(show_question_error_dialog){
        ErrorDialogDismissOnly(
            title = "Error",
            body = "Please enter a valid question",
            onDismiss = {
                show_question_error_dialog=false
            }
        )
    }
    if(should_show_intermediate_screen){
        Log.d("apisuccess","from AddDoubtScreen: got to intermediate screen")
        can_edit_title=false
        can_edit_question=false
        can_edit_tags=false
        PostDoubtScreenIntermediate(
            title=title_text,
            question=question_text,
            mainViewModel=mainViewModel,
            onSuccess = {msg->
                navController.navigateUp()//alreasy cleared question tags in miainviewmodel.PostUserDoubt
                Toast.makeText(context,"Successfully Posted Question", Toast.LENGTH_LONG).show()
                Log.d("apisuccess","from AddDoubtScreen: success posting question")
                should_show_intermediate_screen=false
            },
            onFailure = {msg->
                Toast.makeText(context,"Error:$msg", Toast.LENGTH_LONG).show()
                Log.d("apifailure","from AddDoubtScreen: error posting question")
                should_show_intermediate_screen=false
                can_edit_title=true
                can_edit_question=true
                can_edit_tags=true
            }

        )
    }
}
@Composable
fun PostDoubtScreenIntermediate(title:String,question:String,mainViewModel: MainViewModel,onSuccess:(String)->Unit,onFailure:(String)->Unit,modifier:Modifier=Modifier){
    var should_show_loading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        mainViewModel.PostUserDoubt(
            title=title,
            question=question,
            onResult = {success,msg->
                if(success){
                    onSuccess(msg)
                }
                else{
                    onFailure(msg)
                }
                should_show_loading=false
            }
        )
    }
    Box(modifier=Modifier.fillMaxSize()) {
        if (should_show_loading) {
            CircularProgressIndicator(color = colorResource(R.color.electric_green),
                modifier=Modifier
                    .align(Alignment.Center))
        }
    }
}
@Composable
fun AddDoubtScreenIntermediate(mainViewModel: MainViewModel,navController: NavController,modifier:Modifier=Modifier){
    var issuccess by remember{ mutableStateOf(false) }
    var msg by remember{ mutableStateOf("") }
    var retry_number by remember{mutableStateOf(0)}

    LaunchedEffect(retry_number) {
        mainViewModel.GetTags(
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
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
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
            AddDoubtScreen(mainViewModel=mainViewModel,navController=navController)
        }
    }
}