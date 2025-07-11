package com.example.asknitt

import android.R.attr.onClick
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.sp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.core.net.toUri

@Composable
fun FileUploadCard(fileItem: UploadFileItem,onDeleteClick:()->Unit,modifier: Modifier=Modifier){
    Card(
        modifier=Modifier.fillMaxWidth(),
        colors= CardDefaults.cardColors(containerColor = colorResource(R.color.dark_gray))
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier=Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.med_padding))){
            Text(
                text=fileItem.filename,
                color=colorResource(R.color.white),
                fontSize=16.sp,
            )
            Spacer(modifier=Modifier.weight(1f))
            IconButton(onClick = {
                onDeleteClick()
            },
                colors = IconButtonDefaults.iconButtonColors(containerColor = colorResource(R.color.electric_red))) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete File",
                    tint=colorResource(R.color.white)
                )
            }
        }
    }
}
@Composable
fun FileCard(filename:String, path: String, bgcolor: Color, modifier:Modifier=Modifier){
    val context= LocalContext.current
    Card(
        modifier=Modifier.fillMaxWidth(),
        colors= CardDefaults.cardColors(containerColor = bgcolor)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier=Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.med_padding))){
            Text(
                text=filename,
                color=colorResource(R.color.electric_blue),
                fontSize=16.sp,
                modifier= Modifier
                    .clickable{
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = path.toUri()
                        }
                        context.startActivity(intent)
                    }
            )
            Spacer(modifier=Modifier.weight(1f))
        }
    }
}
fun GetFileNameFromUri(context: Context, uri: Uri): String {
    var name: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
    }
    if (name == null) {
        name = uri.path?.substringAfterLast('/')
    }
    return name?:"Unknown File"
}
fun UriToMultipart(partName: String, context: Context, uri: Uri): MultipartBody.Part {
    val fileName = GetFileNameFromUri(context, uri)
    val inputStream = context.contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes() ?: byteArrayOf()
    val requestBody = bytes.toRequestBody("*/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, fileName, requestBody)
}