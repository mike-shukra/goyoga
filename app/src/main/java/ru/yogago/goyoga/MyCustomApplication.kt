package ru.yogago.goyoga

import android.app.Application

import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
//import com.yandex.metrica.YandexMetrica
//import com.yandex.metrica.YandexMetricaConfig
import ru.yogago.goyoga.data.AppConstants.Companion.API_key
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.Settings
import ru.yogago.goyoga.service.DataBase
import java.util.*

class MyCustomApplication : Application()  {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "MyCustomApplication - onCreate")
//        // Creating an extended library configuration.
//        val config = YandexMetricaConfig.newConfigBuilder(API_key).build()
//        // Initializing the AppMetrica SDK.
//        YandexMetrica.activate(applicationContext, config)
//        // Automatic tracking of user activity.
//        YandexMetrica.enableActivityAutoTracking(this)

        DataBase.createDataBase(this)

        val dBMainThreadDao = DataBase.createDataBaseToMainTread(this).getDBDao()
        var settings = dBMainThreadDao.getSettings()
        if (settings == null) {
            settings = Settings()
            dBMainThreadDao.insertSettings(settings)
        }
        val lang = settings.language
        val change = when (lang) {
            "Russian" -> {
                "ru"
            }
            "English" -> {
                "en"
            }
            else -> {
                ""
            }
        }
        MainActivity.dLocale = Locale(change)

    }


}