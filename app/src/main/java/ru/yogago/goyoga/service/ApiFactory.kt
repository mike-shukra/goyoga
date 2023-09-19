package ru.yogago.goyoga.service

import ru.yogago.goyoga.data.AppConstants


object ApiFactory {
    val API : Api = RetrofitFactory.retrofit(AppConstants.BASE_URL)
    .create(Api::class.java)
}
