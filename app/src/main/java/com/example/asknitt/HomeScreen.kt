package com.example.asknitt

import android.R.attr.top
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(mainViewModel: MainViewModel,navController: NavController,modifier: Modifier=Modifier){
    Box(modifier=Modifier
        .fillMaxSize()
        .background(color=Color.Black))
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier=Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))) {
            Text(
                text="WELCOME ${mainViewModel.username}",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.headings)),
                modifier=Modifier.align(Alignment.Start),
                color= colorResource(R.color.electric_gold),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier=Modifier
                    .fillMaxWidth()) {
                Text(
                    text = "People Helped: ${mainViewModel.user_questions_helped}",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start),
                    color = colorResource(R.color.electric_green),
                )
                Text(
                    text = "Total Questions Asked: ${mainViewModel.user_questions_asked}",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start),
                    color = colorResource(R.color.electric_green),
                )
                Text(
                    text = "You joined on: ${mainViewModel.joined_on}",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start),
                    color = colorResource(R.color.electric_green),
                )

            }
            Spacer(modifier=Modifier.height(64.dp))
            Text(
                text="Trending Doubts:",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.headings)),
                modifier=Modifier.align(Alignment.Start),
                color= colorResource(R.color.electric_gold),
            )
            Spacer(modifier=Modifier.height(16.dp))
            LazyColumn(
                modifier=Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(mainViewModel.recent_doubts) {doubt->
                    DoubtCard(should_show_username = true,navController = navController, doubt = doubt)
                }
            }
        }
    }
}
@Composable
fun HomeScreenIntermediate(mainViewModel: MainViewModel,navController: NavController){
    var retrycount by remember { mutableStateOf(0) }
    var issuccess1 by remember { mutableStateOf(false) }
    var error_msg1 by remember { mutableStateOf("") }
    var issuccess2 by remember { mutableStateOf(false) }
    var error_msg2 by remember { mutableStateOf("") }
    val context=LocalContext.current
    LaunchedEffect(retrycount) {
        mainViewModel.GetCurrentUserInfo(
            onFinish ={success,msg->
                issuccess1=success
                error_msg1=msg
            }
        )
        mainViewModel.GetRecentDoubts(
            onFinish ={success,msg->
                issuccess2=success
                error_msg2=msg
            }
        )
    }
    if(error_msg1== stringResource(R.string.expired_signature) || error_msg1== stringResource(R.string.expired_signature)) {
        LaunchedEffect(Unit) {
            navController.navigate(AuthScreenRoutes.AUTH.name) {
                popUpTo(MainScreenRoutes.MAIN.name) {
                    inclusive = true
                }
            }
            Toast.makeText(
                context,
                "Session Expired,Please Login Again",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Box(modifier=Modifier.fillMaxSize()){
        if(!(issuccess1 && issuccess2) && (error_msg1=="" && error_msg2=="")){
            CircularProgressIndicator(modifier=Modifier.align(Alignment.Center),color=colorResource(R.color.electric_green))
        }
        else if (!(issuccess1 && issuccess2)){//(error_msg1!="" || error_msg2!="") is always true
            Column(modifier=Modifier.align(Alignment.Center).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text="$error_msg1,$error_msg2",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color=colorResource(R.color.electric_red)
                )
                Button(onClick = {
                    retrycount+=1
                    issuccess1=false
                    issuccess2=false
                    error_msg1=""
                    error_msg2=""
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
            HomeScreen(mainViewModel=mainViewModel,navController=navController)
        }
    }

}