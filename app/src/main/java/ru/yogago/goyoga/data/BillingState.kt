package ru.yogago.goyoga.data

import androidx.lifecycle.MutableLiveData

const val JUST_PAY: String = "just_pay"
const val REMOVE_ADS: String = "remove_ads"
const val REMOVE_ADS_Y: String = "remove_ads_y"

object BillingState {
    private var isAdsMonth = true
    private var isAdsYear = true
    val isAds: MutableLiveData<Boolean> = MutableLiveData()
    val isJustPay: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isAds.postValue(false)
        isJustPay.postValue(false)
    }

    fun setFlagByString(s: String, b: Boolean) {
        if (s == JUST_PAY) isJustPay.postValue(b)
        if (s == REMOVE_ADS) isAdsMonth = b
        if (s == REMOVE_ADS_Y) isAdsYear = b
        isAds.postValue((isAdsMonth && isAdsYear))
    }

    fun getSubscribesList(): List<String> {
        return listOf(JUST_PAY, REMOVE_ADS, REMOVE_ADS_Y)
    }
}