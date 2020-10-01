package ru.yogago.goyoga.model

import android.util.Log
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.RegistrationBody
import ru.yogago.goyoga.data.Token
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.AppDatabase
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.ui.login.LoginViewModel
import ru.yogago.goyoga.ui.login.RegisterViewModel
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class LoginModel: CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val db: AppDatabase = DataBase.db
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var registerViewModel: RegisterViewModel
    val service = ApiFactory.API

    fun saveTokenDB(token: Token) {
        Log.d(LOG_TAG, "LoginModel - saveTokenDB token: $token")
        launch {
            val response = db.getDBDao().insertToken(token)
            Log.d(LOG_TAG, "LoginModel - saveTokenDB response: $response")
        }
    }

    fun logOut() {
        launch {
            val responseDeleteToken = db.getDBDao().deleteToken()
            Log.d(LOG_TAG, "LoginModel - LogOut - responseDeleteToken: $responseDeleteToken")
            val responseDeleteUser = db.getDBDao().deleteUserData()
            Log.d(LOG_TAG, "LoginModel - LogOut - responseDeleteUser: $responseDeleteUser")
            val responseDeleteAsanas = db.getDBDao().deleteAsanas()
            Log.d(LOG_TAG, "LoginModel - LogOut - responseDeleteAsanas: $responseDeleteAsanas")
            val responseActionState = db.getDBDao().deleteActionState()
            Log.d(LOG_TAG, "LoginModel - LogOut - responseActionState: $responseActionState")

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
        launch {
            val request = service.deleteUserAsync()
            try {
                val response = request.await()
                val message = response.body()!!
                if (message.error == null) {
                    Log.d(LOG_TAG, "LoginModel - deleteUser message: $message")
                }
                else {
                    loginViewModel.loginError.postValue(message.error)
                }
            } catch (e: Exception) {
                Log.d(LOG_TAG, "LoginModel - deleteUser Exception: $e")
                loginViewModel.loginError.postValue(e.toString())
            }

            val responseDeleteToken = db.getDBDao().deleteToken()
            Log.d(LOG_TAG, "LoginModel - deleteUser - responseDeleteToken: $responseDeleteToken")
            val responseDeleteUser = db.getDBDao().deleteUserData()
            Log.d(LOG_TAG, "LoginModel - deleteUser - responseDeleteUser: $responseDeleteUser")
            val responseDeleteAsanas = db.getDBDao().deleteAsanas()
            Log.d(LOG_TAG, "LoginModel - deleteUser - responseDeleteAsanas: $responseDeleteAsanas")
            val responseActionState = db.getDBDao().deleteActionState()
            Log.d(LOG_TAG, "LoginModel - deleteUser - responseActionState: $responseActionState")
        }
    }

    fun registerRemote(registrationBody: RegistrationBody) {
        launch {
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

    fun cancelBackgroundWork() {
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "ActionViewModel - cancelBackgroundWork")
    }


}