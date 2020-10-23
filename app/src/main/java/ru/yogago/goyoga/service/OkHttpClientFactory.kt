package ru.yogago.goyoga.service

import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.yogago.goyoga.BuildConfig
import java.net.CookieManager
import java.util.concurrent.TimeUnit

class OkHttpClientFactory {

    private val authInterceptor = Interceptor {chain->
        val newUrl = chain
            .request()
            .url
            .newBuilder()
            .build()

        val newRequest = chain
            .request()
            .newBuilder()
            .url(newUrl)
            .addHeader("Cookie", TokenProvider.cookieString)
            .build()

        chain.proceed(newRequest)
    }

    private val loggingInterceptor =  HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val cookieJar = JavaNetCookieJar(CookieManager())

    private val client =
        if (BuildConfig.DEBUG) {
            OkHttpClient()
                .newBuilder()
                .connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(7, TimeUnit.SECONDS)
                .writeTimeout(7, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .cookieJar(cookieJar)
                .build()
        } else {
            OkHttpClient()
                .newBuilder()
                .connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(7, TimeUnit.SECONDS)
                .writeTimeout(7, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
        }


    fun getClient(): OkHttpClient = client

}