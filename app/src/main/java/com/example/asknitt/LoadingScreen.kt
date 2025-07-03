package com.example.asknitt

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoadingScreenWithRetry(inside_launched_effect:(onResult:(Boolean, String)->Unit)->Unit, navController: NavController, should_verify_exp_sign:Boolean, to_show_on_success:@Composable ()->Unit, modifier:Modifier=Modifier){
    var issuccess by remember{ mutableStateOf(false) }
    var msg by remember{ mutableStateOf("") }
    var retry_number by remember{mutableStateOf(0)}
    val context= LocalContext.current
    LaunchedEffect(retry_number) {
        inside_launched_effect { success, error_msg ->
            issuccess=success
            msg=error_msg
        }
    }
    if(should_verify_exp_sign){
        if(msg== stringResource(R.string.expired_signature)){
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
    }
    Box(modifier=Modifier.fillMaxSize()){
        if(!issuccess && msg==""){
            CircularProgressIndicator(modifier=Modifier.align(Alignment.Center),color=colorResource(R.color.electric_green))
        }
        else if (!issuccess && msg!=""){
            Column(modifier=Modifier.align(Alignment.Center).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text="$msg",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.foldable)),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color=colorResource(R.color.electric_red)
                )
                Button(onClick = {
                    retry_number+=1
                    issuccess=false
                    msg=""
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
            to_show_on_success()
        }
    }
}

@Composable
fun LoadingScreenWithToast(inside_launched_effect: ((Boolean, String) -> Unit) -> Unit, navController: NavController, success_message:String="", should_show_success_toast: Boolean=true, onSuccess:()->Unit={}, onFailure:()->Unit={},modifier:Modifier=Modifier){
    var issuccess by remember { mutableStateOf(false) }
    var error_msg by remember { mutableStateOf("") }
    val context=LocalContext.current
    LaunchedEffect(Unit) {
        inside_launched_effect{success,msg->
            issuccess=success
            error_msg=msg
        }
    }
    if(error_msg== stringResource(R.string.expired_signature)){
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
        if(!issuccess && error_msg==""){
            CircularProgressIndicator(modifier=Modifier.align(Alignment.Center),color=colorResource(R.color.electric_green))
        }
        else if (!issuccess && error_msg!=""){
            Toast.makeText(LocalContext.current,error_msg, Toast.LENGTH_SHORT).show()
            LaunchedEffect(Unit) {
                onFailure()
            }
        }
        else{
            if(should_show_success_toast) {
                Toast.makeText(LocalContext.current, success_message, Toast.LENGTH_SHORT).show()
            }
            LaunchedEffect(Unit) {
                onSuccess()
            }
        }
    }
}