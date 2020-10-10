package ru.yogago.goyoga.data

import androidx.lifecycle.MutableLiveData

object BillingState {
    val isAds: MutableLiveData<Boolean> = MutableLiveData()
    val isJustPay: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isAds.postValue(false)
        isJustPay.postValue(false)
    }

    fun setFlagByString(s: String, b: Boolean) {
        if (s == "just_pay") isJustPay.postValue(b)
        if (s == "remove_ads") isAds.postValue(b)
    }

    fun getFlagByString(s: String): Boolean {
        if (s == "just_pay") return isJustPay.value!!
        if (s == "remove_ads") return isAds.value!!
        return false
    }
}