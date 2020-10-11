package ru.yogago.goyoga.data

import androidx.lifecycle.MutableLiveData

const val JUST_PAY: String = "just_pay"
const val REMOVE_ADS: String = "remove_ads"

object BillingState {
    val isAds: MutableLiveData<Boolean> = MutableLiveData()
    val isJustPay: MutableLiveData<Boolean> = MutableLiveData()

    fun setFlagByString(s: String, b: Boolean) {
        if (s == JUST_PAY) isJustPay.postValue(b)
        if (s == REMOVE_ADS) isAds.postValue(b)
    }

}