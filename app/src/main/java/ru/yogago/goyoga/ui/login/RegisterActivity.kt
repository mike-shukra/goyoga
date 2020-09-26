package ru.yogago.goyoga.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.yogago.goyoga.MainActivity
import ru.yogago.goyoga.R
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

        registerViewModel.isRegister.observe(this, {
            if (it) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.putExtra("message", getString(R.string.registration_true))
                startActivity(intent)
                finish()
            }
        })

        setContentView(R.layout.activity_register)

        val registerLogin = findViewById<EditText>(R.id.registerFragmentLogin)
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

            registerSavePassButton.isEnabled = loginState.isDataValid

            if (loginState.loginError != null) {
                registerLogin.error = getString(loginState.loginError)
            }
            if (loginState.emailError != null) {
                registerEmailAddress.error = getString(loginState.emailError)
            }
            if (loginState.passwordError != null) {
                registerPassword.error = getString(loginState.passwordError)
            }
            if (loginState.passwordReplayError != null) {
                registerPasswordReplay.error = getString(loginState.passwordReplayError)
            }
        })

        registerLogin.afterTextChanged {
            registerViewModel.loginDataChanged(
                login = registerLogin.text.toString(),
                email = registerEmailAddress.text.toString(),
                password = registerPassword.text.toString(),
                passwordReplay = registerPasswordReplay.text.toString())
        }

        registerEmailAddress.afterTextChanged {
            registerViewModel.loginDataChanged(
                login = registerLogin.text.toString(),
                email = registerEmailAddress.text.toString(),
                password = registerPassword.text.toString(),
                passwordReplay = registerPasswordReplay.text.toString())
        }

        registerPasswordReplay.afterTextChanged {
            registerViewModel.loginDataChanged(
                login = registerLogin.text.toString(),
                email = registerEmailAddress.text.toString(),
                password = registerPassword.text.toString(),
                passwordReplay = registerPasswordReplay.text.toString())
        }

        registerPassword.afterTextChanged {
            registerViewModel.loginDataChanged(
                login = registerLogin.text.toString(),
                email = registerEmailAddress.text.toString(),
                password = registerPassword.text.toString(),
                passwordReplay = registerPasswordReplay.text.toString())
        }

        registerSavePassButton.setOnClickListener {
            registerLoading.visibility = View.VISIBLE
            val registrationBody = RegistrationBody(
                login = registerLogin.text.toString(),
                email = registerEmailAddress.text.toString(),
                password = registerPassword.text.toString()
            )
            registerViewModel.register(registrationBody)
        }
    }
}