package com.example.asknitt.ui.presentation.doubts

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asknitt.R
import com.example.asknitt.data.model.UploadFileItem
import com.example.asknitt.ui.components.CustomOutlineTextField
import com.example.asknitt.ui.components.LoadingScreenWithToast
import com.example.asknitt.util.FileUploadCard
import com.example.asknitt.util.GetFileNameFromUri
import com.example.asknitt.util.UriToMultipart
import com.example.asknitt.viewmodels.AnswerViewModel
import com.example.asknitt.viewmodels.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddAnswer(
    question_id: Int, 
    answer_text: String, 
    answerViewModel: AnswerViewModel, 
    mainViewModel: MainViewModel,
    navController: NavController, 
    onValueChanged: (String) -> Unit, 
    onClose: () -> Unit
) {
    var should_show_loading_screen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            answerViewModel.answerFiles.add(
                UploadFileItem(
                    multipartBody = UriToMultipart("files", context, uri),
                    filename = GetFileNameFromUri(context, uri)
                )
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth().padding(top = dimensionResource(R.dimen.from_top_padding))
    ) {
        CustomOutlineTextField(
            cur_text = answer_text,
            enabled = true,
            singleLine = false,
            onValueChanged = onValueChanged,
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Upload Files", fontSize = 16.sp, color = colorResource(R.color.electric_blue), fontFamily = FontFamily(Font(R.font.foldable)))
            IconButton(onClick = { filePicker.launch("*/*") }) {
                Icon(Icons.Default.Upload, null, tint = colorResource(R.color.electric_green))
            }
        }

        answerViewModel.answerFiles.forEach { file ->
            FileUploadCard(file, onDeleteClick = { answerViewModel.answerFiles.remove(file) })
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onClose) {
                Text("Cancel", color = colorResource(R.color.electric_red))
            }
            Button(
                onClick = { should_show_loading_screen = true },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_gray))
            ) {
                Text("Submit", color = colorResource(R.color.electric_green))
            }
        }
    }

    if (should_show_loading_screen) {
        LoadingScreenWithToast(
            inside_launched_effect = { onResult ->
                answerViewModel.postAnswer(question_id, answer_text, mainViewModel.username, onResult)
            },
            navController = navController,
            success_message = "Answer Posted!",
            onSuccess = {
                onValueChanged("")
                should_show_loading_screen = false
                onClose()
            },
            onFailure = { should_show_loading_screen = false }
        )
    }
}
