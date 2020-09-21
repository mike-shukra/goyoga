package ru.yogago.goyoga.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.yogago.goyoga.MainActivity
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.RegistrationBody

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        registerViewModel.setModel()

        registerViewModel.isToken.observe(this, {
            if (it) {
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        setContentView(R.layout.activity_register)

        val registerPersonName = findViewById<EditText>(R.id.registerFragmentPersonName)
        val registerEmailAddress = findViewById<EditText>(R.id.registerFragmentEmailAddress)
        val registerPassword = findViewById<EditText>(R.id.registerFragmentPassword)
        val registerPasswordReplay = findViewById<EditText>(R.id.registerFragmentPasswordReplay)
        val registerSavePassButton = findViewById<Button>(R.id.registerFragmentSavePassButton)
        val registerLoading = findViewById<ProgressBar>(R.id.loadingReg)
        val registerError: TextView = findViewById(R.id.registerError)

        registerViewModel.registerError.observe(this@RegisterActivity, {
            registerError.visibility = View.VISIBLE
            registerError.text = it
            registerLoading.visibility = View.GONE
        })

        registerViewModel.loginFormState.observe(this@RegisterActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / phone / password is valid
            registerSavePassButton.isEnabled = loginState.isDataValid
            Log.d(LOG_TAG, "registerSavePassButton.isEnabled: " + loginState.isDataValid)

            if (loginState.usernameError != null) {
                registerEmailAddress.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                registerPassword.error = getString(loginState.passwordError)
            }
            if (loginState.passwordReplayError != null) {
                registerPasswordReplay.error = getString(loginState.passwordReplayError)
            }
        })

        registerEmailAddress.afterTextChanged {
            registerViewModel.loginDataChanged(
                username = registerEmailAddress.text.toString(),
                password = registerPassword.text.toString(),
                passwordReplay = registerPasswordReplay.text.toString())
        }

        registerPasswordReplay.afterTextChanged {
            registerViewModel.loginDataChanged(
                username = registerEmailAddress.text.toString(),
                password = registerPassword.text.toString(),
                passwordReplay = registerPasswordReplay.text.toString())
        }

        registerPassword.afterTextChanged {
            registerViewModel.loginDataChanged(
                username = registerEmailAddress.text.toString(),
                password = registerPassword.text.toString(),
                passwordReplay = registerPasswordReplay.text.toString())
        }

        registerSavePassButton.setOnClickListener {
            registerLoading.visibility = View.VISIBLE
            val registrationBody = RegistrationBody(
                email = registerEmailAddress.text.toString(),
                first_name = if (registerPersonName.text.isNotEmpty()) registerPersonName.text.toString() else getString(R.string.noValue),
                password = registerPassword.text.toString()
            )
            registerViewModel.register(registrationBody)
        }
    }
}