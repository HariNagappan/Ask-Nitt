package com.example.asknitt.data.remote

import com.example.asknitt.data.BASE_URL
import com.example.asknitt.data.JWT_TOKEN
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val authinterceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("Authorization", JWT_TOKEN)
        .build()
    chain.proceed(request)
}
val client= OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .addInterceptor(authinterceptor)
    .build()

val retrofit= Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val api=retrofit.create(ApiService::class.java)