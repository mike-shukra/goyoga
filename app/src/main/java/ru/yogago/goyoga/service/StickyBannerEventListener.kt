package ru.yogago.goyoga.service

import android.util.Log
import com.yandex.mobile.ads.AdEventListener
import com.yandex.mobile.ads.AdRequestError
import ru.yogago.goyoga.data.AppConstants

class StickyBannerEventListener : AdEventListener.SimpleAdEventListener() {
    override fun onAdLoaded() {
        Log.d(AppConstants.LOG_TAG, "Ad loaded")
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        Log.d(AppConstants.LOG_TAG,"Failed to load with reason: " + error.description)
    }
}