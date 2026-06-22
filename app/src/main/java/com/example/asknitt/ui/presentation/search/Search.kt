package com.example.asknitt.ui.presentation.search

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FilterListOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import com.example.asknitt.R
import com.example.asknitt.data.MAX_TAG_LENGTH
import com.example.asknitt.data.model.FilterItem
import com.example.asknitt.data.model.QuestionStatus
import com.example.asknitt.ui.components.LoadingScreenWithRetry
import com.example.asknitt.ui.components.SearchTextField
import com.example.asknitt.ui.presentation.doubts.CustomTagsShowerRemovable
import com.example.asknitt.ui.presentation.doubts.CustomTagsSuggestionShower
import com.example.asknitt.ui.presentation.doubts.DoubtCard
import com.example.asknitt.viewmodels.DoubtsViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchScreen(navController: NavController, doubtsViewModel: DoubtsViewModel, modifier: Modifier = Modifier) {
    var showSearchingLoading by remember { mutableStateOf(false) }
    var shouldShowFilterBox by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(
                    top = dimensionResource(R.dimen.from_top_padding),
                    bottom = dimensionResource(R.dimen.large_padding),
                    start = dimensionResource(R.dimen.large_padding),
                    end = dimensionResource(R.dimen.large_padding)
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    shouldShowFilterBox = !shouldShowFilterBox
                }) {
                    Icon(
                        imageVector = if (shouldShowFilterBox) Icons.Outlined.FilterListOff else Icons.Outlined.FilterList,
                        contentDescription = "Filters",
                        tint = colorResource(R.color.electric_green)
                    )
                }
                SearchTextField(
                    cur_text = doubtsViewModel.searchQuestionText,
                    singleLine = true,
                    onValueChanged = { newText ->
                        doubtsViewModel.searchQuestionText = newText
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
                        .padding(horizontal = 12.dp)
                )
                IconButton(
                    onClick = {
                        showSearchingLoading = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = colorResource(R.color.electric_green),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            AnimatedVisibility(shouldShowFilterBox) {
                FilterBox(doubtsViewModel = doubtsViewModel)
            }
            
            if (doubtsViewModel.filteredDoubts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No Questions Found",
                        color = colorResource(R.color.white),
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.foldable)),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    doubtsViewModel.filteredDoubts.forEach { doubt ->
                        DoubtCard(
                            navController = navController,
                            doubt = doubt
                        )
                    }
                }
            }
        }
    }
    
    if (showSearchingLoading) {
        LoadingScreenWithRetry(
            inside_launched_effect = { onResult ->
                doubtsViewModel.searchDoubts(
                    onFinish = { success, msg ->
                        onResult(success, msg)
                    }
                )
            },
            navController = navController,
            should_verify_exp_sign = false,
            to_show_on_success = {
                showSearchingLoading = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilterBox(doubtsViewModel: DoubtsViewModel, modifier: Modifier = Modifier) {
    val filters: List<FilterItem> = listOf(
        FilterItem(idx = 0, name = "Tags"),
        FilterItem(idx = 1, name = "Timestamp"),
        FilterItem(idx = 2, name = "Status")
    )
    var selectedOption by remember { mutableStateOf(filters[0].name) }
    
    Box(
        modifier = Modifier
            .background(color = colorResource(R.color.dark_gray), shape = RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.med_padding))
                .fillMaxWidth()
        ) {
            Row {
                filters.forEach { filter ->
                    val isSelected = filter.name == selectedOption
                    TextButton(onClick = {
                        selectedOption = filter.name
                    }) {
                        Text(
                            text = filter.name,
                            fontSize = 16.sp,
                            color = if (isSelected) colorResource(R.color.electric_green) else colorResource(R.color.white)
                        )
                    }
                }
            }
            when (selectedOption) {
                "Tags" -> TagsSelectionTab(doubtsViewModel = doubtsViewModel)
                "Timestamp" -> TimeStampSelectionTab(doubtsViewModel = doubtsViewModel)
                "Status" -> StatusSelectionTab(doubtsViewModel = doubtsViewModel)
            }
        }
    }
}

@Composable
fun TagsSelectionTab(doubtsViewModel: DoubtsViewModel, modifier: Modifier = Modifier) {
    var tagSearchText by remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            SearchTextField(
                cur_text = tagSearchText,
                singleLine = true,
                onValueChanged = { new_text ->
                    if (new_text.length <= MAX_TAG_LENGTH) {
                        tagSearchText = new_text
                    }
                },
                placeholder_text = "Search for Tags",
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
                    .padding(horizontal = 12.dp)
            )
            AnimatedVisibility(doubtsViewModel.searchQuestionTags.isNotEmpty()) {
                IconButton(
                    onClick = {
                        doubtsViewModel.searchQuestionTags.clear()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorResource(R.color.electric_red)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete All Tags",
                        tint = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomTagsSuggestionShower(
            cur_text = tagSearchText,
            add_to_lst = doubtsViewModel.searchQuestionTags,
            doubtsViewModel = doubtsViewModel,
            exclude = doubtsViewModel.searchQuestionTags
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTagsShowerRemovable(from_lst = doubtsViewModel.searchQuestionTags)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeStampSelectionTab(doubtsViewModel: DoubtsViewModel, modifier: Modifier = Modifier) {
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Enable Date Filter", color = Color.White, modifier = Modifier.padding(end = 8.dp))
            Switch(
                checked = doubtsViewModel.shouldDateFilter,
                onCheckedChange = {
                    doubtsViewModel.shouldDateFilter = it
                })
        }
        AnimatedVisibility(doubtsViewModel.shouldDateFilter) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "From: ${doubtsViewModel.fromDate}",
                        color = colorResource(R.color.white),
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { showFromDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick Start Date",
                            tint = colorResource(R.color.electric_green)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "To: ${doubtsViewModel.toDate}",
                        color = colorResource(R.color.white),
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { showToDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick End Date",
                            tint = colorResource(R.color.electric_green)
                        )
                    }
                }
            }
        }
    }

    if (showFromDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                doubtsViewModel.fromDate = LocalDate.of(year, month + 1, day)
                showFromDatePicker = false
            },
            doubtsViewModel.fromDate.year,
            doubtsViewModel.fromDate.monthValue - 1,
            doubtsViewModel.fromDate.dayOfMonth
        ).apply {
            datePicker.maxDate = calendar.timeInMillis
            setOnCancelListener { showFromDatePicker = false }
            show()
        }
    }
    if (showToDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                doubtsViewModel.toDate = LocalDate.of(year, month + 1, day)
                showToDatePicker = false
            },
            doubtsViewModel.toDate.year,
            doubtsViewModel.toDate.monthValue - 1,
            doubtsViewModel.toDate.dayOfMonth
        ).apply {
            datePicker.maxDate = calendar.timeInMillis
            datePicker.minDate = doubtsViewModel.fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            setOnCancelListener { showToDatePicker = false }
            show()
        }
    }
}

@Composable
fun StatusSelectionTab(doubtsViewModel: DoubtsViewModel, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Question Status", color = Color.White)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            QuestionStatus.entries.forEach { status ->
                val isSelected = (status == doubtsViewModel.statusDoubtFilter)
                Text(
                    text = if (status == QuestionStatus.PENDING) "UNSOLVED" else status.name,
                    fontSize = 16.sp,
                    color = if (isSelected) colorResource(R.color.electric_green) else colorResource(R.color.white),
                    modifier = Modifier
                        .clickable {
                            doubtsViewModel.statusDoubtFilter = status
                        }
                        .border(
                            width = 1.dp,
                            color = if (isSelected) colorResource(R.color.electric_pink) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}
