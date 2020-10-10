package ru.yogago.goyoga.data

object BillingState {
    var isAds = false
    var isJustPay = false

    fun setFlagByString(s: String, b: Boolean){
        if (s == "just_pay") isJustPay = b
        if (s == "remove_ads") isAds = b
    }
}