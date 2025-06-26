package com.example.asknitt

import android.R.attr.x
import android.app.ProgressDialog.show
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asknitt.ErrorDialogDismissOnly
import okhttp3.internal.userAgent
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import kotlin.contracts.contract
import kotlin.math.log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController,loginType: LoginType,mainViewModel: MainViewModel ,modifier: Modifier=Modifier){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password_visible by remember { mutableStateOf(false) }
    var show_incorrect_username by remember { mutableStateOf(false) }
    var show_incorrect_password by remember { mutableStateOf(false) }
    var show_check_internet_dialog by  remember { mutableStateOf(false) }
    var isloginselectable by remember { mutableStateOf(true) }
    var show_progress_indicator by remember { mutableStateOf(false) }
    val context=LocalContext.current

    Box(
        modifier=Modifier
            .fillMaxSize()
            .background(color =Color.Black)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))
        ) {
            Text(
                text="ASK NITT",
                fontSize =40.sp,
                color=colorResource(R.color.electric_green),
                fontFamily = FontFamily(Font(R.font.headings)),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier=Modifier
                    .padding(dimensionResource(R.dimen.med_padding))
            )
            Spacer(modifier=Modifier.height(30.dp))
            Text(
                text=if(loginType== LoginType.LOGIN) "LOGIN" else "SIGN UP",
                fontSize =32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.stripes)),
                color=colorResource(R.color.electric_green),
                textAlign = TextAlign.Center,
                modifier=Modifier
                    .padding(dimensionResource(R.dimen.med_padding))
            )
            OutlinedTextField(
                value = username,
                onValueChange = {username=it.trim()},
                placeholder = {
                    Text(
                        text="Enter Username Here",
                        color=colorResource(R.color.electric_green))},
                label = {Text(
                    text="Username",
                    color=colorResource(R.color.electric_green),
                )},
                singleLine = true,
                colors= OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.electric_green),
                    unfocusedTextColor = colorResource(R.color.electric_green),
                    focusedBorderColor = colorResource(R.color.electric_pink),
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    cursorColor = colorResource(R.color.electric_green)
                    ),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier=Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = {password=it},
                    placeholder = {
                        Text(
                            text="Enter Password Here",
                            color=colorResource(R.color.electric_green))},
                    label = {Text(
                        text="Password",
                        color=colorResource(R.color.electric_green),
                    )},
                    singleLine = true,
                    visualTransformation = if(password_visible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { Icon(
                        imageVector =if(password_visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = colorResource(R.color.electric_green),
                        modifier=Modifier.clickable{password_visible=!password_visible}
                    ) },
                    colors= OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.electric_green),
                        unfocusedTextColor = colorResource(R.color.electric_green),
                        focusedBorderColor = colorResource(R.color.electric_pink),
                        focusedContainerColor = Color.Black,
                        unfocusedContainerColor = Color.Black,
                        cursorColor = colorResource(R.color.electric_green)
                    ),
                )
                if(loginType== LoginType.LOGIN) {
                    TextButton(
                        onClick = {
                            navController.navigateUp()
                            navController.navigate(AuthScreenRoutes.SIGN_UP.name)
                        },
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 32.dp)
                    ) {
                        Text(
                            text = "Don't have an account? Sign Up",
                            color = colorResource(R.color.electric_gold)
                        )
                    }
                }
                Spacer(modifier=Modifier.height(32.dp))
                Button(
                    onClick = {
                        mainViewModel.SetUsername(username)
                        mainViewModel.SetPassword(password)
                        show_progress_indicator=true
                        isloginselectable=false

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.electric_green),
                        ),
                    enabled = isloginselectable
                )
                {
                    Text(
                        text=if(loginType== LoginType.LOGIN) "LOGIN" else "SIGN UP",
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.foldable)),
                        fontWeight = FontWeight.Bold,
                        color=colorResource(R.color.dark_gray)
                    )
                }
            }

        }
       if(show_check_internet_dialog){
           ErrorDialogDismissOnly(
               title="Error",
               body = "Unable to process Request, Please try again later" ,
               onDismiss = {show_check_internet_dialog=false})
       }
        if(show_progress_indicator){
            CircularProgressIndicator(modifier=Modifier.align(Alignment.Center),color=colorResource(R.color.electric_green))
            LoginSignupIntermediate(
                onLaunch = {
                    if(loginType== LoginType.LOGIN){
                        mainViewModel.LoginUser(
                            context = context,
                            onFinish = {success,error_msg->
                                if(success){
                                    navController.navigateUp()
                                    navController.navigate(MainScreenRoutes.MAIN.name)
                                }
                                else{
                                    show_progress_indicator=false
                                    Toast.makeText(context,error_msg,Toast.LENGTH_SHORT).show()
                                }
                                isloginselectable=true

                            })
                    }
                    else{
                        mainViewModel.SignUpUser(
                            context = context,
                            onFinish = {success,error_msg->
                                if(success){
                                    navController.navigate(MainScreenRoutes.MAIN.name){
                                        popUpTo(AuthScreenRoutes.AUTH.name){
                                            inclusive=true
                                        }
                                    }
                                }
                                else{
                                    show_progress_indicator=false
                                    Toast.makeText(context,error_msg,Toast.LENGTH_SHORT).show()
                                }
                                isloginselectable=true

                            })
                    }
                }
            )
        }
    }
}
@Composable
fun LoginSignupIntermediate(onLaunch:()->Unit){
    LaunchedEffect(Unit) {
        onLaunch()
    }
}
@Composable
fun ErrorDialogDismissOnly(title:String,body:String,dismissbuttontext:String="Dismiss",onDismiss:() ->Unit){
    AlertDialog(
        onDismissRequest = {onDismiss()},
        title = {
            Text(
                text=title,
                color = colorResource(R.color.electric_red))},
        text = {
            Text(
                text=body,
                color = Color.White)},
        confirmButton = {TextButton(onClick = {onDismiss()}) {
            Text(
                text=dismissbuttontext,
                color=Color.Green) }},
        containerColor = colorResource(R.color.dark_gray),

    )
}