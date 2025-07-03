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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ExploreUsersHome(mainViewModel: MainViewModel,navController: NavController,modifier: Modifier=Modifier){
    var cur_text by remember{ mutableStateOf("") }
    var should_search by remember { mutableStateOf(false) }
    Box(modifier=Modifier
        .fillMaxSize()
        .background(color= colorResource(R.color.black))){
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier= Modifier
                .fillMaxSize()
                .padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Explore Users",
                    fontSize = 32.sp,
                    color = colorResource(R.color.electric_gold),
                    fontFamily = FontFamily(Font(R.font.headings)),
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    navController.navigate(MainScreenRoutes.FRIENDS.name)
                }) {
                    Icon(
                        imageVector = Icons.Default.PeopleAlt,
                        contentDescription = "Friends",
                        tint = colorResource(R.color.electric_blue),
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier=Modifier.fillMaxWidth()
            ) {
                SearchTextField(
                    cur_text = cur_text,
                    placeholder_text = "Search for people",
                    singleLine = true,
                    onValueChanged = { new_text ->
                        cur_text = new_text
                    },
                    modifier = Modifier
                        .height(36.dp)
                        .weight(1f)
                        .background(
                            colorResource(R.color.dark_gray),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = colorResource(R.color.electric_pink),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(8.dp)
                )
                IconButton(onClick = {
                    should_search=true
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Users",
                        tint = colorResource(R.color.electric_green)
                    )
                }
            }
            if(mainViewModel.all_users.size>0) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    mainViewModel.all_users.forEach { general_user ->
                        UserCard(
                            generalUser = general_user,
                            navController = navController
                        )
                    }
                }
            }
            else{
                Box(modifier=Modifier.fillMaxSize()){
                    Text(
                        text="No Users Found",
                        fontSize=16.sp,
                        fontFamily = FontFamily(Font(R.font.foldable)),
                        color=colorResource(R.color.white),
                        modifier=Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        if(should_search){
            LoadingScreenWithToast(
                inside_launched_effect = {onResult->
                    mainViewModel.GetUsersByName(
                        username_search_text =cur_text,
                        onFinish = {success,msg->
                            onResult(success,msg)
                        }
                    )
                },
                navController=navController,
                success_message = "",
                should_show_success_toast = false,
                onSuccess = {should_search=false},
                onFailure = { should_search=false }
            )
        }
    }
}
@Composable
fun UserCard(generalUser: GeneralUser,navController: NavController,modifier:Modifier=Modifier){
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
            Row(modifier=Modifier.fillMaxWidth()) {
                Text(
                    text = generalUser.username,
                    color = colorResource(R.color.white),
                    fontSize = 20.sp,
                )
                Spacer(modifier=Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowRight,
                    contentDescription = null,
                    tint = colorResource(R.color.electric_gold)
                )
            }
        }
    }
}