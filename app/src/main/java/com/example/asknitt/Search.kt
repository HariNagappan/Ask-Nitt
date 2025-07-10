package com.example.asknitt

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FilterListOff
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchScreen(navController: NavController, mainViewModel: MainViewModel,modifier:Modifier=Modifier){
    var search_text by remember{ mutableStateOf(mainViewModel.search_question_text) }
    var show_searching_loading by remember { mutableStateOf(false) }
    var should_show_filter_box by remember { mutableStateOf(false) }
    Box(modifier=Modifier.fillMaxSize()){
        Column(
            modifier= Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(top=dimensionResource(R.dimen.from_top_padding),bottom=dimensionResource(R.dimen.large_padding),start=dimensionResource(R.dimen.large_padding),end=dimensionResource(R.dimen.large_padding)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier=Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    should_show_filter_box = !should_show_filter_box
                }) {
                    Icon(
                        imageVector = if(should_show_filter_box) Icons.Outlined.FilterListOff else Icons.Outlined.FilterList,
                        contentDescription = "Tags",
                        tint = colorResource(R.color.electric_green)
                    )
                }
                SearchTextField(
                    cur_text = search_text,
                    singleLine = true,
                    onValueChanged = { new_text ->
                        search_text = new_text
                        mainViewModel.search_question_text=new_text
                    },
                    placeholder_text = "Search Questions Here",
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
                IconButton(
                    onClick = {
                        show_searching_loading=true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = colorResource(R.color.electric_green),
                        modifier=Modifier
                            .size(32.dp)
                    )
                }
            }
            AnimatedVisibility(should_show_filter_box)  {
                FilterBox(mainViewModel=mainViewModel)
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier=Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())) {
                mainViewModel.filtered_doubts.forEach { doubt ->
                    DoubtCard(
                        navController = navController,
                        doubt=doubt
                    )
                }
            }
        }
    }
    if(show_searching_loading){
        LoadingScreenWithRetry(
            inside_launched_effect = {onResult->
                mainViewModel.SearchDoubts(
                    search_text=search_text,
                    onFinish = {success,msg->
                        onResult(success,msg)
                    })
            },
            navController=navController,
            should_verify_exp_sign = false,
            to_show_on_success = {
                show_searching_loading=false
            }
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilterBox(mainViewModel: MainViewModel,modifier:Modifier=Modifier){
    val filters:List<FilterItem> =listOf(
        FilterItem(idx=0,name="Tags"),
        FilterItem(idx=1,name="Timestamp"),
        FilterItem(idx=2,name="Status")
        )
    var selectedoption by remember { mutableStateOf(filters[0].name) }
    Box(modifier=Modifier
        .background(color=colorResource(R.color.dark_gray), shape = RoundedCornerShape(16.dp))
        .fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier=Modifier
                .padding(dimensionResource(R.dimen.med_padding))
                .fillMaxWidth()) {
            Row {
                filters.forEach { filter ->
                    val isselected = filter.name == selectedoption
                    TextButton(onClick = {
                        selectedoption = filter.name
                    }) {
                        Text(
                            text = filter.name,
                            fontSize = 16.sp,
                            color = if (isselected) colorResource(R.color.electric_green) else colorResource(
                                R.color.white
                            )
                        )
                    }
                }
            }
            when (selectedoption) {
                "Tags" -> {
                    TagsSelectionTab(mainViewModel = mainViewModel)
                }

                "Timestamp" -> {
                    TimeStampSelectionTab(mainViewModel=mainViewModel)
                }

                "Status" -> {
                    StatusSelectionTab(mainViewModel=mainViewModel)
                }
            }
        }
    }
}
@Composable
fun TagsSelectionTab(mainViewModel: MainViewModel,modifier: Modifier=Modifier){
    var tag_search_text by remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier=Modifier.fillMaxWidth()) {
            SearchTextField(
                cur_text = tag_search_text,
                singleLine = true,
                onValueChanged = { new_text ->
                    if (new_text.length <= MAX_TAG_LENGTH) {
                        tag_search_text = new_text
                    }
                },
                placeholder_text = "Search for Tags",
                modifier = Modifier
                    .height(36.dp)
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
            AnimatedVisibility(mainViewModel.search_question_tags.isNotEmpty()) {
                IconButton(
                    onClick = {
                        mainViewModel.search_question_tags.clear()
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = colorResource(R.color.electric_red))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete All Tags",
                        tint = colorResource(R.color.white)
                    )
                }
            }
        }
        CustomTagsSuggestionShower(
            cur_text = tag_search_text,
            add_to_lst = mainViewModel.search_question_tags,
            mainViewModel = mainViewModel,
            exclude = mainViewModel.search_question_tags
        )
        CustomTagsShowerRemovable(from_lst = mainViewModel.search_question_tags)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeStampSelectionTab(mainViewModel: MainViewModel,modifier:Modifier=Modifier){
    var show_from_date_picker by remember { mutableStateOf(false) }
    var show_to_date_picker by remember { mutableStateOf(false) }
    var from_date by remember { mutableStateOf(mainViewModel.from_date) }
    var to_date by remember { mutableStateOf(mainViewModel.to_date) }
    val calender by remember {mutableStateOf(Calendar.getInstance())}

    val context= LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(
                text="From: $from_date",
                color=colorResource(R.color.white),
                fontSize = 16.sp
            )
            IconButton(onClick = {
                show_from_date_picker=true
            }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = colorResource(R.color.electric_green)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(
                text="To: $to_date",
                color=colorResource(R.color.white),
                fontSize = 16.sp
            )
            IconButton(onClick = {
                show_to_date_picker=true
            }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = colorResource(R.color.electric_green)
                )
            }
        }
    }
    if(show_from_date_picker){
        val datePickerDialog=DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                from_date = LocalDate.of(year, month + 1, dayOfMonth)
                mainViewModel.from_date=from_date
                show_from_date_picker=false
            },
            from_date.year,
            from_date.monthValue - 1,
            from_date.dayOfMonth
        )
        datePickerDialog.setTitle("From Date")
        datePickerDialog.datePicker.maxDate=calender.timeInMillis
        datePickerDialog.setOnCancelListener {
            show_from_date_picker=false
        }
        datePickerDialog.show()
    }
    if(show_to_date_picker){
        val datePickerDialog= DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                to_date = LocalDate.of(year, month + 1, dayOfMonth)
                mainViewModel.to_date=to_date
                show_to_date_picker=false
            },
            to_date.year,
            to_date.monthValue - 1,
            to_date.dayOfMonth
        )
        datePickerDialog.setTitle("To Date")
        datePickerDialog.datePicker.maxDate=calender.timeInMillis
        datePickerDialog.datePicker.minDate= from_date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        datePickerDialog.setOnCancelListener {
            show_to_date_picker=false
        }
        datePickerDialog.show()
    }
}

@Composable
fun StatusSelectionTab(mainViewModel: MainViewModel,modifier:Modifier=Modifier){
    var selected_status by remember { mutableStateOf(mainViewModel.status_doubt_filter) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier=Modifier.fillMaxWidth()
    ) {
        Text(
            text="Status"
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier=Modifier.fillMaxWidth()){
            QuestionStatus.entries.forEach {question_status->
                val isselected=(question_status==selected_status)
                Text(
                    text=if(question_status== QuestionStatus.PENDING) "UNSOLVED" else question_status.name,
                    fontSize=16.sp,
                    color=if(isselected) colorResource(R.color.electric_green) else colorResource(R.color.white),
                    modifier= Modifier
                        .clickable{
                            selected_status=question_status
                            mainViewModel.status_doubt_filter=question_status
                        }
                        .border(width=1.dp,color=if(isselected) colorResource(R.color.electric_pink) else Color.Transparent,shape=RoundedCornerShape(8.dp))
                        .padding(4.dp)
                )
                Spacer(modifier=Modifier.width(16.dp))
            }
        }
    }
}