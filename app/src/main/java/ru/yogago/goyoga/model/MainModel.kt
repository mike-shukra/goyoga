package ru.yogago.goyoga.model

import android.util.Log
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.Companion.APP_TOKEN
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.service.TokenProvider
import ru.yogago.goyoga.ui.profile.ProfileViewModel
import ru.yogago.goyoga.ui.select.SelectViewModel
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainModel: CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val dbDao = DataBase.db.getDBDao()
    private val service = ApiFactory.API
    private lateinit var selectViewModel: SelectViewModel
    private lateinit var profileViewModel: ProfileViewModel

    fun loadData() {
        launch {
            delay(300)
            var data = getRemoteData()
            if (data.error != "no") {
                delay(1000)
                data = getRemoteData()
            }
            if (data.error == "no") {
                val responseDelete = dbDao.deleteAsanas()
                Log.d(LOG_TAG, "MainModel - loadData responseDelete: $responseDelete")
                val responseInsertAsanas = dbDao.insertAsanas(data.asanas!!)
                Log.d(LOG_TAG, "MainModel - loadData responseInsertAsanas: $responseInsertAsanas")
                val responseInsertUserData = dbDao.insertUserData(data.userData!!)
                Log.d(LOG_TAG, "MainModel - loadData responseInsertUserData: $responseInsertUserData")
            } else {
                Log.d(LOG_TAG, "MainModel - loadData error: ${data.error}")
                selectViewModel.error.postValue(data.error)
            }
            val asanas: List<Asana>? = dbDao.getAsanas()
            val userData: UserData? = dbDao.getUserData()
            if (asanas != null) selectViewModel.asanas.postValue(asanas.filter {
                it.side != "second"
            })
            if (userData != null) selectViewModel.userData.postValue(userData)

        }
    }

    private suspend fun getRemoteData(): Data {
        val request = service.getDataAsync()
        try {
            val response = request.await()
            return if(response.isSuccessful) {
                val data = response.body()!!
                Log.d(LOG_TAG, "MainModel - getRemoteData - data: $data")
                val asanas = data.asanas
                Log.d(LOG_TAG, "MainModel - getRemoteData - asanas: $asanas")
                val userData = data.userData
                Log.d(LOG_TAG, "MainModel - getRemoteData - userData: $userData")
                data
            } else {
                Log.d(LOG_TAG,"MainModel - getRemoteData error: " + response.errorBody().toString())
                Data(error = response.errorBody().toString())
            }
        }
        catch (e: Exception) {
            Log.d(LOG_TAG, "MainModel - getRemoteData - Exception: $e")
            return  Data(error = e.toString())
        }
    }

    private fun isTokenDB(): Boolean {
        val response: Token? = dbDao.getToken()
        Log.d(LOG_TAG, "MainModel - isTokenDB: $response")
        return if (response != null) {
            TokenProvider.token = response
            TokenProvider.cookieString = "id_user=${TokenProvider.token.userId}; code_user=${TokenProvider.token.token}"
            true
        } else {
            Log.d(LOG_TAG, "MainModel - isTokenDB: no token")
            false
        }
    }

    private suspend fun registerAnonymousUser(uniqueID: String) {
        val request = service.registerAnonymousUserAsync(uniqueID, APP_TOKEN)
        try {
            val response = request.await()
            val message = response.body()!!
            if (message.error == null) {
                Log.d(LOG_TAG, "MainModel - registerRemote message: $message")
            }
        }
        catch (e: java.lang.Exception){
            Log.d(LOG_TAG, "MainModel - registerRemote Exception: $e")
        }
    }

    fun create(level: String, knee: String, loins: String, neck: String) {
        launch {
            dbDao.insertActionState(ActionState(currentId = 1))
            val requestMessageCreate = service.createAsync(
                level = level,
                knee = knee,
                loins = loins,
                neck = neck
            )
            try {
                val responseMessage = requestMessageCreate.await()
                if(responseMessage.isSuccessful) {
                    val message = responseMessage.body()!!
                    if (message.message == "true") {
                        Log.d(LOG_TAG, "MainModel - create - message: $message")
                        profileViewModel.done.postValue(true)
                    } else {
                        Log.d(LOG_TAG, "MainModel - create - message error: ${message.error}")
                        profileViewModel.error.postValue(message.error)
                    }
                } else {
                    Log.d(LOG_TAG,"MainModel - responseMessage error: " + responseMessage.errorBody().toString())
                    Message(error = responseMessage.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.d(LOG_TAG, "MainModel - responseMessage - Exception: $e")
                Message(error = e.toString())
            }
        }
    }

    fun loadUserData() {
        launch {
            if(!isTokenDB()) {
                val uniqueID: String = UUID.randomUUID().toString()
                registerAnonymousUser(uniqueID)
                val token = TokenProvider.getToken(uniqueID, APP_TOKEN)
                val response = dbDao.insertToken(token)
                Log.d(LOG_TAG, "MainModel - loadUserData - saveTokenDB response: $response")
                create("0", "0", "0", "0")
            }

            val data = loadRemoteUser()
            if (data.error == "no") {
                val saveUserToDB = dbDao.insertUserData(data.userData!!)
                Log.d(LOG_TAG, "MainModel - loadUserData - saveUserToDB: $saveUserToDB")
            } else {
                Log.d(LOG_TAG, "MainModel - loadUserData - error: ${data.error}")
                profileViewModel.error.postValue(data.error)
            }
            var user: UserData? = dbDao.loadUserData()
            Log.d(LOG_TAG, "MainModel - loadUserData - loadUserFromDB user: $user")
            if (user == null) user = UserData(0)
            profileViewModel.user.postValue(user)
        }
    }

    private suspend fun loadRemoteUser(): Data {
        val petRequest = service.getDataAsync()
        return try {
            val response = petRequest.await()
            if(response.isSuccessful) {
                val data = response.body()!!
                Log.d(LOG_TAG, "MainModel - loadRemoteUser  data: $data")
                data
            } else {
                Log.d(LOG_TAG,"MainModel - loadRemoteUser  error: " + response.errorBody().toString())
                Data(error = response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "MainModel - loadRemoteUser - Exception: $e")
            Data(error = e.toString())
        }
    }

    fun setViewModel(m: SelectViewModel) : MainModel {
        this.selectViewModel = m
        return this
    }

    fun setProfileViewModel(m: ProfileViewModel) : MainModel {
        this.profileViewModel = m
        return this
    }

    fun cancelBackgroundWork() {
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "MainModel - cancelBackgroundWork")
    }


}