package ru.yogago.goyoga.service

import ru.yogago.goyoga.data.AppConstants

object ApiFactory {

    var API : Api = RetrofitFactory().retrofit(AppConstants.BASE_URL)
        .create(Api::class.java)

    fun createApi() {
       API = RetrofitFactory().retrofit(AppConstants.BASE_URL)
            .create(Api::class.java)
    }
}
