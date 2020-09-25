package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.goyoga.data.*


interface Api{

    @GET("Action/data")
    fun getDataAsync(): Deferred<Response<Data>>

    @POST("Api/register")
    fun registerUserAsync(@Body registrationBody: RegistrationBody): Deferred<Response<RegistrationResponse>>

    @FormUrlEncoded
    @POST("Login/app")
    fun authAsync(
        @Field("login") login: String,
        @Field("passwd") passwd: String
    ): Deferred<Response<Token>>

    @FormUrlEncoded
    @POST("Select/runData")
    fun createAsync(
        @Field("level") level: String,
        @Field("knee") knee: String,
        @Field("loins") loins: String,
        @Field("neck") neck: String
    ): Deferred<Response<Data>>

    @DELETE("sign_out")
    fun logOutAsync(): Deferred<Response<Message>>

    @DELETE("user")
    fun deleteUserAsync(): Deferred<Response<Message>>

    @FormUrlEncoded
    @POST("EditUser/password")
    fun updatePasswordAsync(@Field("password") password: String): Deferred<Response<Token>>

    @FormUrlEncoded
    @POST("EditUser/name")
    fun updateUserNameAsync(@Field("login") name: String): Deferred<Response<Message>>

}