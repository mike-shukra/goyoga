package ru.yogago.goyoga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.model.MainModel
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private var isAuth: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        DataBase.createDataBase(this)
        isAuth = savedInstanceState?.getBoolean("isAuth") ?: false
        if (!isAuth){
            val model = MainModel()
            model.error.observe(this, { error ->
                Log.d(LOG_TAG, "MainActivity - error: $error")
                setContentView(R.layout.activity_error)
                val repeatButton = findViewById<Button>(R.id.repeat)
                val loginError = findViewById<TextView>(R.id.loginError)
                loginError.text = error
                model.isTimeout.observe(this, {
                    val loginMassage: TextView = findViewById<EditText>(R.id.loginMassage)
                    if (it) loginMassage.text = getString(R.string.timeOutMessage)
                })
                repeatButton.setOnClickListener {
                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })

            model.isToken.observe(this, {
                if (!it) {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                isAuth = true
            })
            model.isTokenDB()

        }


        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_profile, R.id.nav_select, R.id.nav_action))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean("isAuth", isAuth)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isAuth = savedInstanceState.getBoolean("isAuth")
    }
}
