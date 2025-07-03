package com.example.asknitt

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ViewUserInDetail(mainViewModel: MainViewModel,navController: NavController,modifier:Modifier=Modifier){
    var show_send_request_loading by remember{mutableStateOf(false)}
    var show_accept_request_loading by remember { mutableStateOf(false) }
    var show_decline_request_loading by remember { mutableStateOf(false) }



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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier= Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(top=dimensionResource(R.dimen.from_top_padding)*2,bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text=mainViewModel.other_user_info!!.username,
                fontSize = 24.sp,
                color=colorResource(R.color.electric_gold),
                textAlign = TextAlign.Center,
                modifier=Modifier.fillMaxWidth()
            )
            Text(
                text="People Helped: "+mainViewModel.other_user_info!!.people_helped,
                fontSize = 16.sp,
                color=colorResource(R.color.electric_green),
            )
            Text(
                text="Questions Asked: "+ mainViewModel.other_user_info!!.questions_asked,
                fontSize = 16.sp,
                color=colorResource(R.color.electric_green),
            )
            Text(
                text="Joined On: "+ GetUtcInLocalTime(mainViewModel.other_user_info!!.joined_on),
                fontSize = 16.sp,
                color=colorResource(R.color.electric_green),
            )
            if(mainViewModel.other_user_info!!.friend_status== FriendRequestStatus.NOT_SENT){
                Button(
                    onClick = {
                        show_send_request_loading=true
                    },
                    colors= ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_gray)),
                    modifier=Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text="Send Friend Request",
                        fontSize = 16.sp,
                        color=colorResource(R.color.electric_pink),
                    )
                }
            }
            else if(mainViewModel.other_user_info!!.friend_status== FriendRequestStatus.ACCEPTED){
                Text(
                    text="Already a Friend",
                    fontSize = 20.sp,
                    color=colorResource(R.color.electric_blue),
                    modifier=Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
            else if(mainViewModel.other_user_info!!.friend_status== FriendRequestStatus.PENDING){
                if(mainViewModel.other_user_info!!.is_current_user_sender_of_request) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Request Sent",
                            fontSize = 20.sp,
                            color = colorResource(R.color.electric_gold),
                            modifier = Modifier
                        )
                        IconButton(
                            onClick = {
                                show_decline_request_loading=true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Cancel Request",
                                tint = colorResource(R.color.electric_red)
                            )
                        }
                    }

                }
                else{
                    Text(
                        text = "This user sent you a request, do you wish to accept it?",
                        fontSize = 20.sp,
                        color = colorResource(R.color.white),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier= Modifier.fillMaxWidth()){
                        IconButton(
                                onClick = {
                                    show_accept_request_loading=true
                                },
                                colors= IconButtonDefaults.iconButtonColors(containerColor = colorResource(R.color.electric_green))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Accept Request",
                                    tint = colorResource(R.color.black),
                                )
                            }
                        Spacer(modifier=Modifier.width(32.dp))
                        IconButton(
                            onClick = {
                                show_decline_request_loading=true
                            },
                            colors= IconButtonDefaults.iconButtonColors(containerColor = colorResource(R.color.electric_red))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Decline Request",
                                tint = colorResource(R.color.white)
                            )
                        }
                    }
                }
            }
        }
        if(show_send_request_loading){
            LoadingScreenWithToast(
                inside_launched_effect = {onResult->
                    mainViewModel.SendFriendRequest(
                        other_username = mainViewModel.other_user_info!!.username,
                        onFinish = {success,msg->
                            onResult(success,msg)
                        }
                    )
                },
                navController=navController,
                success_message = "Sent Friend Request",
                onSuccess = {
                    navController.navigateUp()
                    navController.navigate(GeneralUser(username=mainViewModel.other_user_info!!.username))
//                    mainViewModel.other_user_info!!.friend_status= FriendRequestStatus.PENDING
//                    mainViewModel.other_user_info!!.is_current_user_sender_of_request=true
                show_send_request_loading=false
                            },
                onFailure = {
                    show_send_request_loading=false
                }
            )
        }
        if(show_accept_request_loading){
            LoadingScreenWithToast(
                inside_launched_effect = {onResult->
                    mainViewModel.AcceptFriendRequest(
                        other_username = mainViewModel.other_user_info!!.username,
                        onFinish = {success,msg->
                            onResult(success,msg)
                        }
                    )
                },
                navController=navController,
                success_message = "Friend Request Accepted",
                onSuccess = {
                    navController.navigateUp()
                    navController.navigate(GeneralUser(username=mainViewModel.other_user_info!!.username))//                    mainViewModel.other_user_info!!.friend_status= FriendRequestStatus.ACCEPTED
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
                    navController.navigate(GeneralUser(username=mainViewModel.other_user_info!!.username))//                    mainViewModel.other_user_info!!.friend_status= FriendRequestStatus.NOT_SENT
                    show_decline_request_loading=false
                },
                onFailure = {
                    show_decline_request_loading=false
                }
            )
        }
    }
}