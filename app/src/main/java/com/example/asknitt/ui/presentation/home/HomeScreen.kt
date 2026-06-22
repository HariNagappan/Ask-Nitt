package com.example.asknitt.ui.presentation.home

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.asknitt.ui.presentation.doubts.DoubtCard
import com.example.asknitt.viewmodels.MainViewModel
import com.example.asknitt.R
import com.example.asknitt.data.routes.AuthScreenRoutes
import com.example.asknitt.data.routes.MainScreenRoutes
import com.example.asknitt.viewmodels.DoubtsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(mainViewModel: MainViewModel,doubtsViewModel: DoubtsViewModel,navController: NavController, modifier: Modifier=Modifier){
    // Collect the StateFlow from ViewModel
    val recentDoubts by doubtsViewModel.recentDoubts.collectAsState()

    Box(modifier=Modifier
        .fillMaxSize()
        .background(color=Color.Black))
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier=Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(
                    R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))) {
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
                    text = "People Helped: ${mainViewModel.userQuestionsHelped}",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start),
                    color = colorResource(R.color.electric_green),
                )
                Text(
                    text = "Total Questions Asked: ${mainViewModel.userQuestionsAsked}",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start),
                    color = colorResource(R.color.electric_green),
                )
                Text(
                    text = "You joined on: ${mainViewModel.joinedOn}",
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
                items(recentDoubts) { doubt -> // Using the collected state
                    DoubtCard(navController = navController, doubt = doubt)
                }
            }
        }
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = colorResource(R.color.electric_gold),
            modifier=Modifier
                .align(Alignment.CenterStart)
                .clickable{
                    navController.navigate(MainScreenRoutes.SETTINGS.name)
                },
        )
    }
}

@Composable
fun HomeScreenIntermediate(mainViewModel: MainViewModel,doubtsViewModel: DoubtsViewModel, navController: NavController){
    var retrycount by remember { mutableStateOf(0) }
    var issuccess1 by remember { mutableStateOf(false) }
    var error_msg1 by remember { mutableStateOf("") }
    var issuccess2 by remember { mutableStateOf(false) }
    var error_msg2 by remember { mutableStateOf("") }
    val context=LocalContext.current
    
    val recentDoubts by doubtsViewModel.recentDoubts.collectAsState()

    LaunchedEffect(retrycount) {
        mainViewModel.getHomeScreenStuff(o1 ={success,msg->
            issuccess1=success
            error_msg1=msg
        },
            o2 ={success,msg->
                issuccess2=success
                error_msg2=msg
            }
        )
    }

    if(error_msg1== stringResource(R.string.expired_signature) || error_msg2== stringResource(R.string.expired_signature)) {
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
        // Modification: If we have cached data (offline), we can show it immediately 
        // even if the refresh is still loading!
        if(recentDoubts.isEmpty() && !(issuccess1 && issuccess2) && (error_msg1=="" && error_msg2=="")){
            CircularProgressIndicator(modifier=Modifier.align(Alignment.Center),color=colorResource(
                R.color.electric_green))
        }
        else if (recentDoubts.isEmpty() && !(issuccess1 && issuccess2)){
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
            HomeScreen(mainViewModel=mainViewModel,doubtsViewModel=doubtsViewModel,navController=navController)
        }
    }
}
