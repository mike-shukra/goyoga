package ru.yogago.goyoga

import android.app.Application

import android.util.Log
import androidx.preference.PreferenceManager
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import ru.yogago.goyoga.data.AppConstants.Companion.API_key
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.service.DataBase
import java.util.*

class MyCustomApplication : Application()  {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "MyCustomApplication - onCreate")
        // Creating an extended library configuration.
        val config = YandexMetricaConfig.newConfigBuilder(API_key).build()
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this)

        DataBase.createDataBase(this)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val change = when (sharedPreferences.getString("language", "bak")) {
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