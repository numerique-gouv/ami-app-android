package fr.gouv.ami.api

import fr.gouv.ami.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

var baseUrl: String = BuildConfig.BASE_URL
    set(value) {
        field = value
        _retrofit = null
        _apiService = null
    }

private fun buildOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

    if (BuildConfig.DEBUG) {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, java.security.SecureRandom())
        }
        builder
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
    }

    return builder.build()
}

private val client = buildOkHttpClient()

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
    // Warning: this needs to be better handled the day we want to change baseURL on the fly.
    get() = _apiService ?: retrofit.create(ApiService::class.java).also { _apiService = it }
