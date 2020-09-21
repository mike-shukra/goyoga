package ru.yogago.goyoga.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.model.LoginModel
import ru.yogago.goyoga.service.TokenProvider
import ru.yogago.goyoga.service.login.LoginRepository
import ru.yogago.goyoga.service.login.Result

class LoginViewModel(private val loginRepository: LoginRepository, application: Application) : AndroidViewModel(application) {

    private val model : LoginModel = LoginModel()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    val loginError: MutableLiveData<String> = MutableLiveData()

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        GlobalScope.launch(Dispatchers.IO) {
            val result = loginRepository.login(username, password)
            if (result is Result.Success) {
                _loginResult.postValue(LoginResult(
                    success = LoggedInUserView(displayName = result.data.first_name)
                ))
                TokenProvider.token = result.data.token
            } else {
                Log.d(LOG_TAG, "LoginViewModel Exception: " + TokenProvider.token.error)
                _loginResult.postValue(LoginResult(error = TokenProvider.token.error.toString()))
                loginError.postValue(TokenProvider.token.error.toString())
            }
        }
    }

    fun logOut(){
        model.logOut()
    }

    fun deleteUser(){
        model.deleteUser()
    }

    fun saveToken() {
        model.saveTokenDB(TokenProvider.token)
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value =
                LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value =
                LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value =
                LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return username.length > 1
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun setModel(): LoginViewModel {
        model.setViewModel(this)
        return this
    }

}