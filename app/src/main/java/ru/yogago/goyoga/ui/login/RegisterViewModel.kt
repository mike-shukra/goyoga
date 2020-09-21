package ru.yogago.goyoga.ui.login

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.RegistrationBody
import ru.yogago.goyoga.model.LoginModel

class RegisterViewModel(application: Application) : AndroidViewModel(application){

    private val model: LoginModel = LoginModel()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    val isToken: MutableLiveData<Boolean> = MutableLiveData()
    val registerError: MutableLiveData<String> = MutableLiveData()

    fun setModel(): RegisterViewModel {
        model.setViewModel(this)
        return this
    }

    fun register(registrationBody: RegistrationBody) {
        model.registerRemote(registrationBody)
    }

    fun loginDataChanged(username: String, password: String, passwordReplay: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value =
                LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value =
                LoginFormState(passwordError = R.string.invalid_password)
        } else if (!isPasswordReplayValid(password, passwordReplay)) {
            _loginForm.value =
                LoginFormState(passwordReplayError = R.string.invalid_password_replay)
        } else {
            _loginForm.value =
                LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordReplayValid(password: String, passwordReplay: String): Boolean {
        val compare = password.compareTo(passwordReplay)
        return compare == 0
    }
}