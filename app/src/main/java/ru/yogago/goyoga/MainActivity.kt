package ru.yogago.goyoga

import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.yogago.goyoga.service.DataBase
import java.util.*


class MainActivity : AppCompatActivity() {

//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(MyContextWrapper.wrap(base, "ru"))
//    }
//
//    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
//        val locale = Locale("ru")
//        overrideConfiguration.setLocale(locale)
//        super.applyOverrideConfiguration(overrideConfiguration)
//    }

    companion object {
        var dLocale: Locale = Locale("")
    }

    init {
        updateConfig(this)
    }

    private fun updateConfig(wrapper: ContextThemeWrapper) {
        if(dLocale == Locale("") )
            return

        Locale.setDefault(dLocale)
        val configuration = Configuration()
        configuration.setLocale(dLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()


//        val locale = Locale("ru")
//        Locale.setDefault(locale)

//        val config = baseContext.resources.configuration
//        baseContext.resources.configuration.setLocale(locale)
//        config.setLocale(locale)
//        val context = baseContext.createConfigurationContext(config)
//        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)


        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile, R.id.nav_select, R.id.nav_action
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (isLandSpace()) navView.visibility = View.GONE
    }

    private fun isLandSpace(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> false
            Configuration.ORIENTATION_LANDSCAPE -> true
            else -> false
        }
    }

}
