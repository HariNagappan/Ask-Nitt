package com.example.asknitt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SearchScreen(navController: NavController, mainViewModel: MainViewModel,modifier:Modifier=Modifier){
    var search_text by remember{ mutableStateOf("") }
    Box(modifier=Modifier.fillMaxSize()){
        Column(
            modifier= Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))
        ) {
            SearchTextField(
                cur_text = search_text,
                singleLine = true,
                onValueChanged = { new_text ->
                    search_text = new_text
                },
                placeholder_text = "Search Questions Here",
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth()
                    .background(
                        colorResource(R.color.dark_gray),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = colorResource(R.color.electric_pink),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(8.dp)
                )
        }
    }
}