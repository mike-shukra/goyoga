package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Call
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
    @POST("api/dataN")
    fun getDataAsyncTwo(
        @Header("X-Authorization-Firebase") header: String,
        @Field("code_user") codeUser: String
    ): Deferred<Response<Data>>

//    @GET("search")
//    fun getRestaurantsBySearch(
//        @Query("entity_id") entity_id: String?,
//        @Query("entity_type") entity_type: String?,
//        @Query("q") query: String?,
//        @Header("Accept") accept: String?,
//        @Header("user-key") userkey: String?
//    ): Call<String?>?

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