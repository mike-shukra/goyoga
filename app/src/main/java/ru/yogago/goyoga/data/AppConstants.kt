package ru.yogago.goyoga.data

class AppConstants {

    companion object {
        private const val DEVELOP_URL = "http://10.0.2.2:8080/"
        const val BASE_URL = DEVELOP_URL
        const val SMALL_PHOTO_URL = BASE_URL + "api/image/get/"
        const val PHOTO_URL = BASE_URL + "api/image/get/"
        const val LOG_TAG: String = "myLog"
        const val LOG_TAG_BILLING: String = "billingLog"
        const val APP_TOKEN_B = "dwfl56JghKKdGnjde3lsfsKllk05sgSsdfsd3898jkasd8LHGf5"
        const val API_key = "6e39ab29-3de3-4c18-88fc-ef061f56f4d6"
        const val YANDEX_RTB_ID_PROFILE = "R-M-648245-3"
        const val YANDEX_RTB_ID_ACTION = "R-M-648245-5"
        const val YANDEX_RTB_ID_SELECT_320X100 = "R-M-648245-4"
        const val YANDEX_RTB_ID_SELECT_VERTICAL = "R-M-648245-6"
        const val PLAY_STORE_SUBSCRIPTION_URL =
            "https://play.google.com/store/account/subscriptions"
//        const val PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL =
//            "https://play.google.com/store/account/subscriptions?sku=%s&package=%s"
    }

}