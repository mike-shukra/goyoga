package ru.yogago.goyoga.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.yogago.goyoga.MainActivity
import ru.yogago.goyoga.R
import ru.yogago.goyoga.service.DataBase


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        DataBase.createDataBase(this)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(this.application) ).get(LoginViewModel::class.java)
        loginViewModel.setModel()

        setContentView(R.layout.activity_login)

        val loginError: TextView = findViewById<EditText>(R.id.loginError)
        val loginMessage: TextView = findViewById<EditText>(R.id.loginMessage)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val reg = findViewById<Button>(R.id.reg)
        val loading = findViewById<ProgressBar>(R.id.loading)

        val message = intent.getStringExtra("message")

        if (message != null) {
            loginMessage.visibility = View.VISIBLE
            loginMessage.text = message
        }

        reg.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginViewModel.loginError.observe(this, {
            loginError.visibility = View.VISIBLE
            loginError.text = it
            loading.visibility = View.GONE
        })

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.loginError != null) {
                username.error = getString(loginState.loginError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {

            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                loginViewModel.saveToken()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
//            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(username.text.toString(), password.text.toString())
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(username.text.toString(), password.text.toString())
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(username.text.toString(), password.text.toString())
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }

    }

    private fun updateUiWithUser(model: String) {
        val welcome = getString(R.string.welcome)
        Toast.makeText(
            applicationContext,
            "$welcome $model",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(errorString: String?) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginViewModel.cancelBackgroundWork()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}