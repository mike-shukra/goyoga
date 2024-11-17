package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.goyoga.data.*


interface Api {

    @GET("api/data")
    suspend fun getDataAsync(): Data

    @POST("api/create")
    suspend fun createAsync(
        @Body parametersDTO: ParametersDTO
    ): Data

    @POST("api/public/firebase-signup")
    suspend fun signUp(@Header("firebaseToken") header: String): BooleanDTO

    @POST("api/public/is-exists")
    suspend fun isUserExist(@Header("firebaseToken") header: String): BooleanDTO

    @POST("api/update-parameters")
    suspend fun updateParameters(
        @Body parametersDTO: ParametersDTO
    ): Data

}