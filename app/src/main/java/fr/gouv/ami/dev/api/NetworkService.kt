package fr.gouv.ami.dev.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

const val baseUrl = "https://ami-back-staging.osc-fr1.scalingo.io"

val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(client)
    .build()

val apiService = retrofit.create(ApiService::class.java)