package com.example.asknitt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun FriendRequests(mainViewModel: MainViewModel,navController: NavController,modifier:Modifier=Modifier){
    val tabs=listOf("Received","Sent")
    var selected_option by remember { mutableStateOf(tabs[0]) }
    Box(modifier=Modifier.fillMaxSize().background(colorResource(R.color.black))) {
        IconButton(
            onClick = {
                navController.navigateUp()
            },
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = dimensionResource(R.dimen.from_top_padding),
                    start = dimensionResource(R.dimen.med_padding)
                )
                .size(40.dp)
                .border(
                    width = 2.dp,
                    color = colorResource(R.color.electric_green),
                    shape = CircleShape
                )
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go Back",
                tint = colorResource(R.color.electric_green)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(
                    top = dimensionResource(R.dimen.from_top_padding),
                    bottom = dimensionResource(R.dimen.large_padding),
                    start = dimensionResource(R.dimen.large_padding),
                    end = dimensionResource(R.dimen.large_padding))
        ) {
            Text(
                text="Friend\nRequests",
                color=colorResource(R.color.electric_gold),
                fontSize = 32.sp,
                lineHeight = 32.sp,
                fontFamily = FontFamily(Font(R.font.headings)),
                textAlign = TextAlign.Center,
                modifier= Modifier
                    .fillMaxWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier=Modifier.fillMaxWidth()
            ){
                tabs.forEach { tab_name->
                    val is_selected=(tab_name==selected_option)
                    Text(
                        text=tab_name,
                        fontSize = 16.sp,
                        color=if(is_selected) colorResource(R.color.electric_green) else colorResource(R.color.white),
                        textAlign = TextAlign.Center,
                        modifier=Modifier
                            .weight(1f)
                            .border(width=1.dp,color=if(is_selected) colorResource(R.color.electric_pink) else Color.Transparent, shape = RoundedCornerShape(100))
                            .clickable{
                                selected_option=tab_name
                            }
                    )
                }
            }
            when(selected_option){
                "Received"->{
                    LoadingScreenWithRetry(
                        inside_launched_effect = { onResult ->
                            mainViewModel.GetUserRecievedFriendRequests(
                                onFinish = { success, msg ->
                                    onResult(success, msg)
                                }
                            )
                        },
                        navController = navController,
                        should_verify_exp_sign = true,
                        to_show_on_success = {RecievedFriendRequests(mainViewModel=mainViewModel,navController=navController)},
                    )
                }
                "Sent"->{
                    LoadingScreenWithRetry(
                        inside_launched_effect = { onResult ->
                            mainViewModel.GetUserSentFriendRequests(
                                onFinish = { success, msg ->
                                    onResult(success, msg)
                                }
                            )
                        },
                        navController = navController,
                        should_verify_exp_sign = true,
                        to_show_on_success = {SentFriendRequests(mainViewModel=mainViewModel,navController=navController)},
                    )
                }
            }
        }
    }
}
@Composable
fun RecievedFriendRequests(mainViewModel: MainViewModel,navController: NavController,modifier: Modifier=Modifier){
    if(mainViewModel.user_friend_requests_recieved.isEmpty()){
        Box(modifier=Modifier.fillMaxSize()){
            Text(
                text="no requests recieved",
                color=colorResource(R.color.white),
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.foldable)),
                modifier=Modifier.align(Alignment.Center)
            )
        }
    }
    else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()) {
            items(mainViewModel.user_friend_requests_recieved) { friend_request ->
                FriendRequestCard(
                    generalUser = friend_request,
                    mainViewModel = mainViewModel,
                    navController = navController,
                    show_dustbin = false
                )
            }
        }
    }
}
@Composable
fun SentFriendRequests(mainViewModel: MainViewModel, navController: NavController, modifier: Modifier=Modifier){
    if(mainViewModel.user_friend_requests_sent.isEmpty()){
        Box(modifier=Modifier.fillMaxSize()){
            Text(
                text="no friend requests sent",
                color=colorResource(R.color.white),
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.foldable)),
                modifier=Modifier.align(Alignment.Center)
            )
        }
    }
    else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()) {
            items(mainViewModel.user_friend_requests_sent) { friend_request ->
                FriendRequestCard(
                    generalUser = friend_request,
                    mainViewModel = mainViewModel,
                    navController = navController,
                    show_dustbin = true
                )
            }
        }
    }
}

@Composable
fun FriendRequestCard(generalUser: GeneralUser, mainViewModel: MainViewModel, navController: NavController, show_dustbin:Boolean, modifier:Modifier=Modifier){
    var show_accept_request_loading by remember { mutableStateOf(false) }
    var show_decline_request_loading by remember { mutableStateOf(false) }
    Card(
        modifier=Modifier.fillMaxWidth(),
        colors= CardDefaults.cardColors(containerColor = colorResource(R.color.dark_gray))) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier=Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.med_padding))
                .clickable{
                    navController.navigate(generalUser)
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier=Modifier.fillMaxWidth()) {
                Text(
                    text = generalUser.username,
                    color = colorResource(R.color.white),
                    fontSize = 20.sp,
                )
                Spacer(modifier=Modifier.weight(1f))
                if(!show_dustbin) {
                    IconButton(
                        onClick = {
                            show_accept_request_loading = true
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorResource(
                                R.color.electric_green
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Accept Request",
                            tint = colorResource(R.color.black),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                IconButton(
                    onClick = {
                        show_decline_request_loading=true
                    },
                    colors= IconButtonDefaults.iconButtonColors(containerColor = colorResource(R.color.electric_red))
                ) {
                    Icon(
                        imageVector = if(show_dustbin) Icons.Default.Delete else Icons.Default.Close,
                        contentDescription = "Decline Request",
                        tint = colorResource(R.color.white)
                    )
                }
            }
        }
        if(show_accept_request_loading){
            LoadingScreenWithToast(
                inside_launched_effect = {onResult->
                    mainViewModel.AcceptFriendRequest(
                        other_username = generalUser.username,
                        onFinish = {success,msg->
                            onResult(success,msg)
                        }
                    )
                },
                navController=navController,
                success_message = "Friend Request Accepted",
                onSuccess = {
                    navController.navigateUp()
                    navController.navigate(MainScreenRoutes.FRIEND_REQUESTS.name)
                    show_accept_request_loading=false
                },
                onFailure = {
                    show_accept_request_loading=false
                }
            )
        }
        if(show_decline_request_loading){
            LoadingScreenWithToast(
                inside_launched_effect = {onResult->
                    mainViewModel.DeclineFriendRequest(
                        other_username = mainViewModel.other_user_info!!.username,
                        onFinish = {success,msg->
                            onResult(success,msg)
                        }
                    )
                },
                navController=navController,
                success_message = "Declined Friend Request",
                onSuccess = {
                    navController.navigateUp()
                    navController.navigate(MainScreenRoutes.FRIEND_REQUESTS.name)
                    show_decline_request_loading=false
                },
                onFailure = {
                    show_decline_request_loading=false
                }
            )
        }
    }
}