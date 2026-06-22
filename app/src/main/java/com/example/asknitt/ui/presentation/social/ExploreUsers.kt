package com.example.asknitt.ui.presentation.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asknitt.R
import com.example.asknitt.data.model.GeneralUser
import com.example.asknitt.data.routes.MainScreenRoutes
import com.example.asknitt.ui.components.LoadingScreenWithToast
import com.example.asknitt.ui.components.SearchTextField
import com.example.asknitt.viewmodels.ExploreViewModel

@Composable
fun ExploreUsersHome(exploreViewModel: ExploreViewModel, navController: NavController, modifier: Modifier = Modifier) {
    var curText by remember { mutableStateOf("") }
    var shouldSearch by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.black))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = dimensionResource(R.dimen.from_top_padding),
                    bottom = dimensionResource(R.dimen.large_padding),
                    start = dimensionResource(R.dimen.large_padding),
                    end = dimensionResource(R.dimen.large_padding)
                )
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Explore Users",
                    fontSize = 32.sp,
                    color = colorResource(R.color.electric_gold),
                    fontFamily = FontFamily(Font(R.font.headings)),
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
            ) {
                SearchTextField(
                    cur_text = curText,
                    placeholder_text = "Search for people",
                    singleLine = true,
                    onValueChanged = { new_text ->
                        curText = new_text
                    },
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f)
                        .background(
                            colorResource(R.color.dark_gray),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { shouldSearch = true },
                    modifier = Modifier
                        .background(colorResource(R.color.electric_green), shape = RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Users",
                        tint = Color.Black
                    )
                }
            }
            
            val users = if (curText.isEmpty()) exploreViewModel.exploreUsers.collectAsState().value else exploreViewModel.allUsers
            
            if (users.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    users.forEach { generalUser ->
                        UserCard(
                            generalUser = generalUser,
                            navController = navController
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No Users Found",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.foldable)),
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        
        if (shouldSearch) {
            LoadingScreenWithToast(
                inside_launched_effect = { onResult ->
                    exploreViewModel.getUsersByName(
                        usernameSearchText = curText,
                        onFinish = { success, msg ->
                            onResult(success, msg)
                        }
                    )
                },
                navController = navController,
                success_message = "",
                should_show_success_toast = false,
                onSuccess = { shouldSearch = false },
                onFailure = { shouldSearch = false }
            )
        }
    }
}

@Composable
fun UserCard(generalUser: GeneralUser, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(generalUser) },
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.dark_gray))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = generalUser.username,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = null,
                tint = colorResource(R.color.electric_gold)
            )
        }
    }
}
