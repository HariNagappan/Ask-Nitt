package com.example.asknitt.data.model

import kotlinx.serialization.Serializable
import okhttp3.MultipartBody


data class UploadFileItem(val multipartBody: MultipartBody.Part,val filename:String)
