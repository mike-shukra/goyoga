package ru.yogago.goyoga.model

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.RegistrationBody
import ru.yogago.goyoga.data.Token
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.AppDatabase
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.service.TokenProvider
import ru.yogago.goyoga.ui.login.LoginViewModel
import ru.yogago.goyoga.ui.login.RegisterViewModel
import java.lang.Exception

class LoginModel {
    private val db: AppDatabase = DataBase.db
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var registerViewModel: RegisterViewModel
    val service = ApiFactory.API

    fun saveTokenDB(token: Token) {
        Log.d(LOG_TAG, "LoginModel - saveTokenDB token: $token")
        GlobalScope.launch(Dispatchers.IO) {
            val response = db.getDBDao().insertToken(token)
            Log.d(LOG_TAG, "LoginModel - saveTokenDB response: $response")
        }
    }

    fun logOut() {
        GlobalScope.launch(Dispatchers.IO) {
            val responseDeleteToken = db.getDBDao().deleteToken()
            Log.d(LOG_TAG, "LoginModel - LogOut - responseDeleteToken: $responseDeleteToken")
            val responseDeleteUser = db.getDBDao().deleteUserData()
            Log.d(LOG_TAG, "LoginModel - LogOut - responseDeleteUser: $responseDeleteUser")
            val responseDeleteAsanas = db.getDBDao().deleteAsanas()
            Log.d(LOG_TAG, "LoginModel - LogOut - responseDeleteAsanas: $responseDeleteAsanas")
            val responseActionState = db.getDBDao().deleteActionState()
            Log.d(LOG_TAG, "LoginModel - LogOut - responseActionState: $responseActionState")
        }
        GlobalScope.launch(Dispatchers.IO) {
            val request = service.logOutAsync()
            try {
                val response = request.await()
                Log.d(LOG_TAG, "LoginModel - logOut response: " + response.body())
            }
            catch (e: Exception){
                Log.d(LOG_TAG, "LoginModel - logOut Exception: $e")
            }
        }
    }

    fun deleteUser() {
        GlobalScope.launch(Dispatchers.IO) {
            val request = service.deleteUserAsync()
            val response = request.await()
            Log.d(LOG_TAG, "LoginModel - deleteUser: " + response.body())
            val responseDeleteToken = db.getDBDao().deleteToken()
            val responseDeleteUser = db.getDBDao().deleteUserData()
            Log.d(LOG_TAG, "LoginModel - deleteUser - responseDeleteToken: $responseDeleteToken")
            Log.d(LOG_TAG, "LoginModel - deleteUser - responseDeleteUser: $responseDeleteUser")
        }
    }

    fun registerRemote(registrationBody: RegistrationBody) {
        GlobalScope.launch(Dispatchers.IO) {
            val request = service.registerUserAsync(registrationBody.login, registrationBody.email, registrationBody.password)
            try {
                val response = request.await()
                val message = response.body()!!
                if (message.error == null) {
                    Log.d(LOG_TAG, "LoginModel - registerRemote message: $message")
                    registerViewModel.isRegister.postValue(message.result)
                }
                else {
                    registerViewModel.registerError.postValue(message.error)
                }
            }
            catch (e: Exception){
                Log.d(LOG_TAG, "LoginModel - registerRemote Exception: $e")
                registerViewModel.registerError.postValue(e.toString())
            }
        }
    }

    fun setViewModel(m: LoginViewModel) : LoginModel {
        this.loginViewModel = m
        Log.d(LOG_TAG, "this.viewModel: " + this.loginViewModel)
        return this
    }

    fun setViewModel(m: RegisterViewModel) : LoginModel {
        this.registerViewModel = m
        Log.d(LOG_TAG, "this.viewModel: " + this.registerViewModel)
        return this
    }
}