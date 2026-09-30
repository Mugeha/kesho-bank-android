package com.keshobank.mobile.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    const val DEFAULT_BASE_URL = "http://10.0.2.2:4000/"

    // Vuln #11: a hardcoded partner-integration key, recoverable straight out
    // of the APK via jadx or `strings`, and directly usable against the
    // backend's partner endpoints outside the app.
    private const val PARTNER_API_KEY = "kesho_live_sk_7f3a9c1e2b6d4f58a0c9"

    fun create(baseUrl: String = DEFAULT_BASE_URL): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val apiKeyInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Kesho-Partner-Key", PARTNER_API_KEY)
                .build()
            chain.proceed(request)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
