package com.example.asknitt

import android.R.attr.top
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(mainViewModel: MainViewModel,modifier: Modifier=Modifier){
    Box(modifier=Modifier
        .fillMaxSize()
        .background(color=Color.Black))
    {
        Column(modifier=Modifier.fillMaxSize().align(Alignment.Center).padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding))) {
            Text(
                text="WELCOME, ${mainViewModel.username}",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.headings)),
                modifier=Modifier.align(Alignment.Start),
                color= colorResource(R.color.electric_gold),
            )
            Spacer(modifier=Modifier.height(32.dp))
            Text(
                text="Trending Doubts:",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.headings)),
                modifier=Modifier.align(Alignment.Start),
                color= colorResource(R.color.electric_gold),
            )
            Spacer(modifier=Modifier.height(32.dp))
            LazyColumn(modifier=Modifier.fillMaxWidth()) {
                items(10) {

                }
            }
        }
    }
}
@Composable
fun QuestionListItem(gameViewModel: MainViewModel){

}