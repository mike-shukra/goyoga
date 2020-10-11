package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.goyoga.data.*


interface Api {

    @GET("Api/data")
    fun getDataAsync(): Deferred<Response<Data>>

    @FormUrlEncoded
    @POST("Api/registerApp")
    fun registerAnonymousUserAsync(
        @Field("login") login: String,
        @Field("appToken") appToken: String,
    ): Deferred<Response<Message>>

    @FormUrlEncoded
    @POST("Api/login")
    fun authAsync(
        @Field("login") login: String,
        @Field("passwd") password: String,
        @Field("level") level: String,
        @Field("knee") knee: String,
        @Field("loins") loins: String,
        @Field("neck") neck: String
    ): Deferred<Response<Token>>

    @FormUrlEncoded
    @POST("Api/create")
    fun createAsync(
        @Field("level") level: String,
        @Field("knee") knee: String,
        @Field("loins") loins: String,
        @Field("neck") neck: String
    ): Deferred<Response<Message>>

}