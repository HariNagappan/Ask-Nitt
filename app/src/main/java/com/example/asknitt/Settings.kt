package com.example.asknitt

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.MainCoroutineDispatcher

@Composable
fun SettingsScreen(mainViewModel: MainViewModel,navController: NavController,modifier: Modifier=Modifier){
    val context= LocalContext.current
    var show_loading_screen by remember { mutableStateOf(false) }
    Box(modifier=Modifier
        .fillMaxSize()
        .background(color=Color.Black)){
        Column(modifier=Modifier.fillMaxSize().align(Alignment.Center).padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))) {
            Text(
                text="LOGOUT ->",
                fontSize = 24.sp,
                textAlign = TextAlign.Left,
                fontFamily = FontFamily(Font(R.font.headings)),
                color= colorResource(R.color.electric_green),
                modifier=Modifier
                    .fillMaxWidth()
                    .clickable{
                        show_loading_screen=true
                    }
            )
            Spacer(modifier=Modifier.weight(1f))
        }
        if(show_loading_screen){
            LogoutLoadingScreen(
                context=context,
                mainViewModel=mainViewModel,
                navController=navController
            )
        }
    }
}
@Composable
fun LogoutLoadingScreen(context: Context, mainViewModel: MainViewModel,navController: NavController,modifier: Modifier=Modifier){
    var success by remember { mutableStateOf(false) }
    var error_msg by remember { mutableStateOf("") }
    val context=LocalContext.current
    LaunchedEffect(Unit) {
        mainViewModel.Logout(
            context=context,
            onFinish = {_success,_msg->
                success= _success
                error_msg= _msg
            })
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (!success && error_msg == "") {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(R.color.electric_green)
            )
        } else if (error_msg == "") {
            LaunchedEffect(Unit) {
                navController.navigate(AuthScreenRoutes.AUTH.name) {
                    popUpTo(MainScreenRoutes.MAIN.name) {
                        inclusive = true
                    }
                }
                Toast.makeText(context, "Successfully Logged Out", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(context, error_msg, Toast.LENGTH_SHORT).show()
        }
    }
}