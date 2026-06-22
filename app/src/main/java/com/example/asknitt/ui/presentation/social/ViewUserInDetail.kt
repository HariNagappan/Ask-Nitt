package com.example.asknitt.ui.presentation.social

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asknitt.R
import com.example.asknitt.data.functions.GetUtcInLocalTime
import com.example.asknitt.data.FriendRequestStatus
import com.example.asknitt.ui.components.LoadingScreenWithToast
import com.example.asknitt.viewmodels.ExploreViewModel
import com.example.asknitt.viewmodels.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewUserInDetail(exploreViewModel: ExploreViewModel, navController: NavController) {
    var showSendRequestLoading by remember { mutableStateOf(false) }
    var showAcceptRequestLoading by remember { mutableStateOf(false) }
    var showDeclineRequestLoading by remember { mutableStateOf(false) }

    val user = exploreViewModel.otherUserInfo ?: return

    Box(modifier = Modifier.fillMaxSize().background(colorResource(R.color.black))) {
        IconButton(
            onClick = { navController.navigateUp() },
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = dimensionResource(R.dimen.from_top_padding), start = dimensionResource(R.dimen.med_padding))
                .size(40.dp)
                .border(width = 2.dp, color = colorResource(R.color.electric_green), shape = CircleShape)
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
                .padding(horizontal = dimensionResource(R.dimen.large_padding))
                .padding(top = dimensionResource(R.dimen.from_top_padding) * 2)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = user.username,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.electric_gold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (user.is_private) {
                Spacer(modifier = Modifier.height(24.dp))
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "This profile is private",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "People Helped: ${user.people_helped ?: 0}",
                    fontSize = 18.sp,
                    color = colorResource(R.color.electric_green),
                )
                Text(
                    text = "Questions Asked: ${user.questions_asked ?: 0}",
                    fontSize = 18.sp,
                    color = colorResource(R.color.electric_green),
                )
                user.joined_on?.let {
                    Text(
                        text = "Joined On: ${GetUtcInLocalTime(it)}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Friend Request Actions
            when (user.friend_status) {
                FriendRequestStatus.NOT_SENT -> {
                    Button(
                        onClick = { showSendRequestLoading = true },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_gray)),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Send Friend Request", color = colorResource(R.color.electric_pink))
                    }
                }
                FriendRequestStatus.ACCEPTED -> {
                    Text(
                        text = "Already a Friend",
                        fontSize = 20.sp,
                        color = colorResource(R.color.electric_blue),
                        fontWeight = FontWeight.Bold
                    )
                }
                FriendRequestStatus.PENDING -> {
                    if (user.is_current_user_sender_of_request) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Request Sent", fontSize = 20.sp, color = colorResource(R.color.electric_gold))
                            IconButton(onClick = { showDeclineRequestLoading = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Cancel", tint = colorResource(R.color.electric_red))
                            }
                        }
                    } else {
                        Text(text = "User sent you a request", color = Color.White, fontSize = 16.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                            IconButton(
                                onClick = { showAcceptRequestLoading = true },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = colorResource(R.color.electric_green))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color.Black)
                            }
                            IconButton(
                                onClick = { showDeclineRequestLoading = true },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = colorResource(R.color.electric_red))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Decline", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Loading Screens
        if (showSendRequestLoading) {
            LoadingScreenWithToast(
                inside_launched_effect = { onResult ->
                    exploreViewModel.sendFriendRequest(user.username, onResult)
                },
                navController = navController,
                success_message = "Sent Friend Request",
                onSuccess = { 
                    showSendRequestLoading = false
                    exploreViewModel.getOtherUserInfo(user.username) { _, _ -> }
                },
                onFailure = { showSendRequestLoading = false }
            )
        }
        if (showAcceptRequestLoading) {
            LoadingScreenWithToast(
                inside_launched_effect = { onResult ->
                    exploreViewModel.acceptFriendRequest(user.username, onResult)
                },
                navController = navController,
                success_message = "Friend Request Accepted",
                onSuccess = { 
                    showAcceptRequestLoading = false
                    exploreViewModel.getOtherUserInfo(user.username) { _, _ -> }
                },
                onFailure = { showAcceptRequestLoading = false }
            )
        }
        if (showDeclineRequestLoading) {
            LoadingScreenWithToast(
                inside_launched_effect = { onResult ->
                    exploreViewModel.declineFriendRequest(user.username, onResult)
                },
                navController = navController,
                success_message = "Request Handled",
                onSuccess = { 
                    showDeclineRequestLoading = false
                    exploreViewModel.getOtherUserInfo(user.username) { _, _ -> }
                },
                onFailure = { showDeclineRequestLoading = false }
            )
        }
    }
}
