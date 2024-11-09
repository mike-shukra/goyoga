package ru.yogago.goyoga

import android.app.Application
import android.content.Context
import android.os.Build

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

    private fun setLocale(context: Context, locale: Locale): Context {
        Log.d(LOG_TAG, "MyCustomApplication - setLocale: $locale")

        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)  // Важно вызвать super.attachBaseContext() сначала!

        val sharedPreferences = base.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("language", Locale.getDefault().language) ?: Locale.getDefault().language
        val locale = Locale(language)

        // Теперь вызываем метод setLocale, когда base контекст уже корректно установлен
        val context = setLocale(base, locale)
        MainActivity.dLocale = locale
        LoginActivity.dLocale = locale
        SignUpActivity.dLocale = locale
    }

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

    }

}