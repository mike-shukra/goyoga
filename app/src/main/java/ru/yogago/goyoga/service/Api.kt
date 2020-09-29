package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.goyoga.data.*


interface Api{

    @GET("Api/data")
    fun getDataAsync(): Deferred<Response<Data>>

    @FormUrlEncoded
    @POST("Api/register")
    fun registerUserAsync(
        @Field("login") login: String,
        @Field("mail") email: String,
        @Field("passwd") password: String
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
    ): Deferred<Response<Data>>

    @POST("Api/out")
    fun logOutAsync(): Deferred<Response<Message>>

    @POST("Api/deleteUser")
    fun deleteUserAsync(): Deferred<Response<Message>>

    @FormUrlEncoded
    @POST("Api/password")
    fun updatePasswordAsync(@Field("password") password: String): Deferred<Response<Token>>

    @FormUrlEncoded
    @POST("Api/name")
    fun updateUserNameAsync(@Field("login") name: String): Deferred<Response<Message>>

}