package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.goyoga.data.*


interface Api {

    @GET("api/dataN")
    fun getDataAsync(@Header("X-Authorization-Firebase") header: String): Deferred<Response<Data>>

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

    @POST("api/createN")
    fun createAsync(
        @Header("X-Authorization-Firebase") header: String,
        @Body parametersDTO: ParametersDTO
    ): Deferred<Response<Message>>

    @POST("api/createN")
    fun createAsyncTwo(
        @Header("X-Authorization-Firebase") header: String,
        @Body parametersDTO: ParametersDTO
    ): Call<Data>

}