package fr.gouv.ami.api

import fr.gouv.ami.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

var baseUrl: String = BuildConfig.BASE_URL
    set(value) {
        field = value
        _retrofit = null
        _apiService = null
    }

private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()

private var _retrofit: Retrofit? = null
private val retrofit: Retrofit
    get() = _retrofit ?: Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .also { _retrofit = it }

private var _apiService: ApiService? = null
val apiService: ApiService
    get() = _apiService ?: retrofit.create(ApiService::class.java).also { _apiService = it }