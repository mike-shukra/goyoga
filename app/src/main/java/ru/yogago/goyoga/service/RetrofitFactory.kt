package ru.yogago.goyoga.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.yogago.goyoga.BuildConfig
import java.net.CookieManager
import java.util.concurrent.TimeUnit

object RetrofitFactory{

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

    //Not logging the authkey if not debug
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

    fun retrofit(baseUrl : String) : Retrofit = Retrofit
        .Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

}