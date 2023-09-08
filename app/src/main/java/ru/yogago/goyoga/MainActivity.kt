package ru.yogago.goyoga

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*


class MainActivity : AppCompatActivity() {

//    private lateinit var oneTapClient: SignInClient
//    private lateinit var signInRequest: BeginSignInRequest

    companion object {
        var dLocale: Locale = Locale("")
    }

    init {
        if(dLocale != Locale("") ) {
            Locale.setDefault(dLocale)
            val configuration = Configuration()
            configuration.setLocale(dLocale)
            this.applyOverrideConfiguration(configuration)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

//        oneTapClient = Identity.getSignInClient(this)
//        signInRequest = BeginSignInRequest.builder()
//            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
//                .setSupported(true)
//                .build())
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build())
//            // Automatically sign in when exactly one credential is retrieved.
//            .setAutoSelectEnabled(true)
//            .build()

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
