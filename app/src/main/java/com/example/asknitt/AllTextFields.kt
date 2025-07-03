package com.example.asknitt

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun SearchTextField(cur_text: String,placeholder_text:String,singleLine: Boolean,onValueChanged: (String) -> Unit,modifier: Modifier){
    BasicTextField(
        value = cur_text,
        onValueChange = { onValueChanged(it) },
        textStyle = TextStyle(
            color=colorResource(R.color.electric_green),
            fontSize = 16.sp
        ),
        singleLine = singleLine,
        decorationBox = {innerTextField->
            if(cur_text.isEmpty()){
                Text(
                    text=placeholder_text,
                    color=colorResource(R.color.electric_green),
                    fontSize = 14.sp
                )
            }
            innerTextField()
        },
        modifier = modifier,
    )
}
@Composable
fun CustomOutlineTextField(cur_text:String, containerColor: Color=colorResource(R.color.dark_gray), singleLine:Boolean, enabled:Boolean, onValueChanged:(String)->Unit, modifier:Modifier=Modifier){
    OutlinedTextField(
        value = cur_text,
        onValueChange = { onValueChanged(it) },
        enabled=enabled,
        placeholder = {
            Text(
                text = "",
                color = colorResource(R.color.electric_green)
            )
        },
        label = {
            Text(
                text = "",
                color = colorResource(R.color.electric_green),
            )
        },
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorResource(R.color.electric_green),
            unfocusedTextColor = colorResource(R.color.electric_green),
            focusedBorderColor = colorResource(R.color.electric_pink),
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            cursorColor = colorResource(R.color.electric_green)
        ),
        modifier=modifier
    )
}
