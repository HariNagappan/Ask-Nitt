package com.example.asknitt

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddAnswer(question_id:Int,answer_text:String,mainViewModel: MainViewModel,navController: NavController,onValueChanged:(String)->Unit,onClose:()->Unit){
    var should_show_loading_screen by remember { mutableStateOf(false) }

    Box{
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier=Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.from_top_padding))
        ){
            CustomOutlineTextField(
                cur_text = answer_text,
                enabled = true,
                singleLine=false,
                onValueChanged = {new_text->
                    onValueChanged(new_text)
                },
                modifier=Modifier.fillMaxWidth())
            Row(modifier=Modifier.fillMaxWidth()){
                Spacer(modifier=Modifier.weight(1f))
                Button(
                    onClick = {
                        onClose()
                    },
                    colors= ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_gray))
                ) {
                    Text(
                        text="Cancel",
                        fontSize = 16.sp,
                        color=colorResource(R.color.electric_red),
                        fontFamily = FontFamily(Font(R.font.foldable))
                    )
                }
                Button(
                    onClick = {
                        should_show_loading_screen=true
                    },
                    colors= ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_gray))
                ) {
                    Text(
                        text="Submit",
                        fontSize = 16.sp,
                        color=colorResource(R.color.electric_green),
                        fontFamily = FontFamily(Font(R.font.foldable))
                    )
                }
            }
        }
    }
    if(should_show_loading_screen){
        LoadingScreenWithToast(
            inside_launched_effect = {onResult->
                mainViewModel.PostAnswer(
                    question_id=question_id,
                    answer=answer_text,
                    onFinish = {success,msg->
                        onResult(success,msg)
                    }
                )
            },
            navController=navController,
            success_message = "Successfully Posted Answer",
            onSuccess = {
                onValueChanged("")
                should_show_loading_screen=false
                onClose()
            },
            onFailure = {
                should_show_loading_screen=false
            }
        )
    }
}