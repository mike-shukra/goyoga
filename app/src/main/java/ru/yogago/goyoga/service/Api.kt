package ru.yogago.goyoga.service

import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.goyoga.data.*


interface Api {

    @GET("api/dataN")
    fun getDataAsync(@Header("X-Authorization-Firebase") header: String): Call<Data>

    @POST("api/createN")
    fun createAsync(
        @Header("X-Authorization-Firebase") header: String,
        @Body parametersDTO: ParametersDTO
    ): Call<Data>

}