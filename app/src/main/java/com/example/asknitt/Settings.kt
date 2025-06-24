package com.example.asknitt

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(mainViewModel: MainViewModel,modifier: Modifier=Modifier){
    val context= LocalContext.current
    var should_auto_login by remember{ mutableStateOf(mainViewModel.should_auto_login) }
    Box(modifier=Modifier
        .fillMaxSize()
        .background(color=Color.Black)){
        Column(modifier=Modifier.fillMaxSize().align(Alignment.Center).padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier=Modifier.fillMaxWidth()) {
                Text(
                    text="AUTO LOGIN",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.headings)),
                    color= colorResource(R.color.electric_green),
                )
                Spacer(modifier=Modifier.weight(1f))
                Switch(
                    checked = should_auto_login,
                    onCheckedChange = {
                        should_auto_login=!should_auto_login
                        mainViewModel.SaveAutoLogin(auto_login=should_auto_login, context=context)
                    },
                    colors= SwitchDefaults.colors(
                        checkedThumbColor = colorResource(R.color.electric_gold),
                        uncheckedThumbColor = colorResource(R.color.electric_gold).copy(alpha = 0.5f),
                        checkedTrackColor = colorResource(R.color.electric_blue),
                        uncheckedTrackColor = colorResource(R.color.electric_blue).copy(alpha = 0.5f),
                    )

                )
            }
        }
    }
}
