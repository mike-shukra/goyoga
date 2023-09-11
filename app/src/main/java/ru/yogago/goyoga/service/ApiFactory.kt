package ru.yogago.goyoga.service

import ru.yogago.goyoga.data.AppConstants


object ApiFactory {
    val API : Api = RetrofitFactory.retrofit(AppConstants.BASE_URL)
    .create(Api::class.java)
    val API_2 : Api = RetrofitFactory.retrofit("http://10.0.2.2:8080/")
    .create(Api::class.java)
}
