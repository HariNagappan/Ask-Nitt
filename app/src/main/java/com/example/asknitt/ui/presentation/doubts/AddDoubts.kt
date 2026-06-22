package com.example.asknitt.ui.presentation.doubts

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.asknitt.R
import com.example.asknitt.data.MAX_QUESTION_LENGTH
import com.example.asknitt.data.MAX_TAG_LENGTH
import com.example.asknitt.data.MAX_TITLE_LENGTH
import com.example.asknitt.data.model.UploadFileItem
import com.example.asknitt.ui.components.CustomOutlineTextField
import com.example.asknitt.ui.components.LoadingScreenWithToast
import com.example.asknitt.ui.components.SearchTextField
import com.example.asknitt.ui.presentation.auth.ErrorDialogDismissOnly
import com.example.asknitt.util.FileUploadCard
import com.example.asknitt.util.GetFileNameFromUri
import com.example.asknitt.util.UriToMultipart
import com.example.asknitt.viewmodels.DoubtsViewModel
import com.example.asknitt.viewmodels.MainViewModel

@Composable
fun AddDoubtScreen(doubtsViewModel: DoubtsViewModel, navController: NavController, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    var titleText by remember { mutableStateOf("") }
    var questionText by remember { mutableStateOf("") }
    var tagSearchText by remember { mutableStateOf("") }
    val scollState = rememberScrollState()

    var shouldShowSearchField by remember { mutableStateOf(false) }
    var tagSearchFocused by remember { mutableStateOf(false) }
    var shouldShowIntermediateScreen by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            doubtsViewModel.doubtFiles.add(
                UploadFileItem(
                    multipartBody = UriToMultipart("files", context, uri),
                    filename = GetFileNameFromUri(context, uri)
                )
            )
        }
    }

    LaunchedEffect(questionText) {
        scollState.animateScrollTo(scollState.maxValue)
    }

    Box(modifier = Modifier.fillMaxSize().background(color = Color.Black)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.fillMaxSize().padding(dimensionResource(R.dimen.large_padding)).imePadding().verticalScroll(scollState)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { navController.navigateUp(); doubtsViewModel.clearCurrentQuestionTags() },
                    modifier = Modifier.size(40.dp).border(2.dp, colorResource(R.color.electric_green), CircleShape).clip(CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = colorResource(R.color.electric_green))
                }
                Spacer(Modifier.width(16.dp))
                Text("POST QUESTION", fontSize = 32.sp, color = colorResource(R.color.electric_gold), fontFamily = FontFamily(Font(R.font.headings)))
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("TITLE", color = colorResource(R.color.electric_red), fontFamily = FontFamily(Font(R.font.stripes)))
                CustomOutlineTextField(
                    cur_text = titleText,
                    onValueChanged = { if (it.length <= MAX_TITLE_LENGTH) titleText = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = true
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("QUESTION", color = colorResource(R.color.electric_red), fontFamily = FontFamily(Font(R.font.stripes)))
                CustomOutlineTextField(
                    cur_text = questionText,
                    onValueChanged = { if (it.length <= MAX_QUESTION_LENGTH) questionText = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    enabled = true
                )
            }

            // Tags Section
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("TAGS", color = colorResource(R.color.electric_red), fontFamily = FontFamily(Font(R.font.stripes)))
                AnimatedVisibility(shouldShowSearchField) {
                    SearchTextField(
                        cur_text = tagSearchText,
                        onValueChanged = { if (it.length <= MAX_TAG_LENGTH) tagSearchText = it },
                        modifier = Modifier.height(36.dp).focusRequester(FocusRequester())
                            .onFocusChanged { tagSearchFocused = it.isFocused },
                        placeholder_text = "Search Tags",
                        singleLine = true
                    )
                }
                if (!shouldShowSearchField) Spacer(Modifier.weight(1f))
                IconButton(onClick = { shouldShowSearchField = !shouldShowSearchField }) {
                    Icon(if (!shouldShowSearchField) Icons.Default.Search else Icons.Default.SearchOff, null, tint = colorResource(R.color.electric_green))
                }
            }

            AnimatedVisibility(tagSearchFocused) {
                CustomTagsSuggestionShower(tagSearchText, doubtsViewModel.curQuestionTags, doubtsViewModel, doubtsViewModel.curQuestionTags)
            }
            CustomTagsShowerRemovable(doubtsViewModel.curQuestionTags)

            // Files Section
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Upload Files", color = colorResource(R.color.electric_blue), fontFamily = FontFamily(Font(R.font.foldable)))
                IconButton(onClick = { filePicker.launch("*/*") }) {
                    Icon(Icons.Default.Upload, null, tint = colorResource(R.color.electric_green))
                }
            }
            doubtsViewModel.doubtFiles.forEach { file ->
                FileUploadCard(file, onDeleteClick = { doubtsViewModel.doubtFiles.remove(file) })
            }

            Button(
                onClick = { shouldShowIntermediateScreen = true },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.electric_green))
            ) {
                Text("SUBMIT QUESTION", fontWeight = FontWeight.Bold, color = colorResource(R.color.dark_gray))
            }
        }

        if (shouldShowIntermediateScreen) {
            LoadingScreenWithToast(
                inside_launched_effect = { onResult ->
                    doubtsViewModel.postUserDoubt(mainViewModel.username, titleText, questionText, onResult)
                },
                navController = navController,
                success_message = "Question Posted!",
                onSuccess = {
                    shouldShowIntermediateScreen = false
                    navController.navigateUp()
                },
                onFailure = { shouldShowIntermediateScreen = false }
            )
        }
    }
}
