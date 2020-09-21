package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.goyoga.data.*


interface Api{

    @GET("Action/data")
    fun getDataAsync(): Deferred<Response<Data>>

    @POST("register")
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

    @PATCH("user/password")
    fun updatePasswordAsync(@Body password: String): Deferred<Response<Token>>

    @PATCH("user/data")
    fun updateUserAsync(@Body user: UserData): Deferred<Response<UserData>>

}