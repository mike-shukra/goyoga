package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.goyoga.data.*


interface Api {

    @GET("api/data")
    fun getDataAsync(@Header("X-Authorization-Firebase") header: String): Deferred<Data>

    @POST("api/create")
    fun createAsync(
        @Header("X-Authorization-Firebase") header: String,
        @Body parametersDTO: ParametersDTO
    ): Deferred<Data>

    @POST("api/public/firebase-signup")
    fun signUp(
        @Header("firebaseToken") header: String
    ): Deferred<BooleanDTO>

    @POST("api/public/is-exists")
    fun isUserExist(
        @Header("firebaseToken") header: String
    ): Deferred<BooleanDTO>

    @POST("api/update-parameters")
    fun updateParameters(
        @Header("X-Authorization-Firebase") header: String,
        @Body parametersDTO: ParametersDTO
    ): Deferred<Data>

}