package com.example.asknitt

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoubtsScreen(mainViewModel: MainViewModel,navController: NavController ,modifier: Modifier=Modifier){
    Box(modifier=Modifier
        .fillMaxSize()
        .background(color=Color.Black)){
        Column(horizontalAlignment = Alignment.CenterHorizontally,modifier=Modifier.fillMaxSize().align(Alignment.Center).padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))) {
            Text(
                text="YOUR DOUBTS",
                color= colorResource(R.color.electric_gold),
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.headings))
                )
            Spacer(modifier=Modifier.width(32.dp))
            if(mainViewModel.user_doubts.isEmpty()){
                Box(modifier=Modifier.fillMaxSize()){
                    Text(
                        text="You didn't ask any questions yet!! ",
                        fontSize = 16.sp,
                        color=colorResource(R.color.white),
                        fontFamily = FontFamily(Font(R.font.foldable)),
                        modifier= Modifier
                            .align(Alignment.Center)
                    )
                }
            }
            else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(mainViewModel.user_doubts){doubt->
                        DoubtCard(doubt = doubt, should_show_username = false,navController = navController)
                    }
                }
            }
        }
        FloatingActionButton(onClick = {
            navController.navigate(MainScreenRoutes.ADD_DOUBT.name)
        },
            containerColor = colorResource(R.color.electric_green).copy(alpha=0.85f),
            shape = CircleShape,
            modifier=Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PostAdd,
                contentDescription = "Post Questions",
                tint=colorResource(R.color.electric_pink),
                modifier= Modifier
                    .size(32.dp)
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoubtCard(should_show_username:Boolean ,navController: NavController,doubt: Doubt){
    Card(
        colors=CardDefaults.cardColors(containerColor = colorResource(R.color.dark_gray)),
        modifier=Modifier
            .fillMaxWidth()
            .clickable{
                navController.navigate(doubt)
            }
    ) {
        Box{
            Column(modifier = Modifier.fillMaxSize().align(Alignment.Center).padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier=Modifier.fillMaxWidth()) {
                    Text(
                        text = "Question id: ${doubt.question_id}",
                        fontSize = 16.sp,
                        color = colorResource(R.color.electric_gold)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if(should_show_username) {
                        Text(
                            text = "Asked by: ${doubt.posted_username}",
                            fontSize = 16.sp,
                            color = colorResource(R.color.electric_pink)
                        )
                    }
                }
                Text(
                    text=doubt.title,
                    fontSize=16.sp,
                    color=colorResource(R.color.electric_gold)
                )
                Row (verticalAlignment = Alignment.CenterVertically,modifier=Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())){
                    Text(
                        text="Tags:",
                        color= colorResource(R.color.electric_red),
                        fontSize = 16.sp,
                    )
                    CustomTagsPlainShower(
                        tags = doubt.tags)
                }
                Text(
                    text="Posted on: ${GetUtcInLocalTime(doubt.question_timestamp)}",
                    color= colorResource(R.color.electric_green),
                    fontSize = 16.sp,
                )
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoubtScreenIntermediate(mainViewModel: MainViewModel,navController: NavController,modifier:Modifier=Modifier){
    var retrycount by remember { mutableStateOf(0) }
    var issuccess by remember { mutableStateOf(false) }
    var error_msg by remember { mutableStateOf("") }
    LaunchedEffect(retrycount) {
        mainViewModel.GetDoubtsByUsername(
            username = mainViewModel.username,
            onFinish ={success,msg->
                issuccess=success
                error_msg=msg
            }
        )
    }
    Box(modifier=Modifier.fillMaxSize()){
        if(!issuccess && error_msg==""){
            CircularProgressIndicator(modifier=Modifier.align(Alignment.Center),color=colorResource(R.color.electric_green))
        }
        else if (!issuccess && error_msg!=""){
            Column(modifier=Modifier.align(Alignment.Center).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text="$error_msg",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color=colorResource(R.color.electric_red)
                )
                Button(onClick = {
                    retrycount+=1
                    issuccess=false
                    error_msg=""
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
            DoubtsScreen(mainViewModel=mainViewModel,navController=navController)
        }
    }
}


@Composable
fun CustomTagsSuggestionShower(cur_text:String, add_to_lst: MutableList<String>, mainViewModel: MainViewModel, exclude:List<String>, modifier:Modifier=Modifier){
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp),modifier=modifier.horizontalScroll(rememberScrollState())) {
        val lst=mainViewModel.tags.filter { it.startsWith(cur_text) && !(it in exclude)}.take(10)
        lst.forEach { tag->
            TagItem(
                text=tag,
                should_show_cross=false,
                onClickText = {
                    add_to_lst.add(tag)
                    //Log.d("general","mainviewmodel.curquestiontags:${mainViewModel.cur_question_tags}")
                },
                onClickCross = {}
                )
        }
    }
}
@Composable
fun CustomTagsShowerRemovable(mainViewModel: MainViewModel,modifier:Modifier=Modifier) {
    var tags_to_remove = remember { mutableStateListOf<String>() }
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        mainViewModel.cur_question_tags.forEach { tag->
            TagItem(
                text=tag,
                should_show_cross = true,
                onClickText = {},
                onClickCross = {
                    tags_to_remove.add(tag)
                }
            )
        }
    }
    for(tag in tags_to_remove){
        mainViewModel.cur_question_tags.remove(tag)
    }
}
@Composable
fun CustomTagsPlainShower(tags:List<String>,modifier:Modifier=Modifier){
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier=modifier){
        Log.d("general","$tags")
        tags.forEach { tag->
            TagItem(
                text=tag,
                should_show_cross = false,
                onClickText = {},
                onClickCross = {}
            )
        }
    }
}
@Composable
fun TagItem(
    text: String,
    should_show_cross: Boolean,
    onClickText: () -> Unit,
    onClickCross: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier= Modifier
        .background(
            color = colorResource(R.color.electric_blue),
            shape = RoundedCornerShape(32.dp)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier= Modifier
                .padding(start=8.dp,end=8.dp)
        ) {
            Text(
                text = text,
                color = if(should_show_cross) colorResource(R.color.electric_green) else colorResource(R.color.white),
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable {
                        onClickText()
                    }
            )
            if (should_show_cross) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = colorResource(R.color.electric_red),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            onClickCross()
                        }
                )
            }
        }
    }
}