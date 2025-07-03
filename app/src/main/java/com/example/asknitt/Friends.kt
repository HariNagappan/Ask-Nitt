package com.example.asknitt

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun Friends(mainViewModel: MainViewModel,navController: NavController,modifier: Modifier=Modifier){
    Box(modifier=Modifier
        .fillMaxSize()
        .background(color= colorResource(R.color.black))){
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
            modifier= Modifier
                .fillMaxSize()
                .padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))
        ) {
            Text(
                text = "Friends",
                fontSize = 32.sp,
                color = colorResource(R.color.electric_gold),
                fontFamily = FontFamily(Font(R.font.headings)),
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )
            IconButton(onClick = {
                navController.navigate(MainScreenRoutes.FRIEND_REQUESTS.name)
            },
                modifier= Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Friend Requests",
                    tint=colorResource(R.color.electric_green),
                )
            }
            if(mainViewModel.users_friends.isEmpty()){
                Box(modifier=Modifier.fillMaxSize()){
                    Text(
                        text="You made no friends yet",
                        color=colorResource(R.color.white),
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.foldable)),
                        modifier=Modifier.align(Alignment.Center)
                    )
                }
            }
            else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(mainViewModel.users_friends) { user_friend ->
                        UserCard(
                            generalUser = user_friend,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}