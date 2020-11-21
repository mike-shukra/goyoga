package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.goyoga.data.*


interface Api {

    @FormUrlEncoded
    @POST("Api/dataN")
    fun getDataAsync(
        @Field("id_user") idUser: String,
        @Field("code_user") codeUser: String
    ): Deferred<Response<Data>>

    @FormUrlEncoded
    @POST("Api/loginN")
    fun authAsync(
        @Field("login") login: String,
        @Field("appToken") appToken: String,
        @Field("level") level: String,
        @Field("knee") knee: String,
        @Field("loins") loins: String,
        @Field("neck") neck: String,
        @Field("inverted") inverted: String
    ): Deferred<Response<Token>>

    @FormUrlEncoded
    @POST("Api/createN")
    fun createAsync(
        @Field("id_user") idUser: String,
        @Field("code_user") codeUser: String,
        @Field("level") level: String,
        @Field("knee") knee: String,
        @Field("loins") loins: String,
        @Field("neck") neck: String,
        @Field("inverted") inverted: String
    ): Deferred<Response<Message>>

}