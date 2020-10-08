package ru.yogago.goyoga.model

import android.util.Log
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.APP_TOKEN
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
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
            val data = getRemoteData()
            if (data.error == "no") {
                val updateDataDB = updateDataDB(data.asanas!!, data.userData!!)
                Log.d(LOG_TAG, "MainModel - loadData - updateDataDB: $updateDataDB")
            } else {
                selectViewModel.error.postValue(data.error)
            }
            val asanas: List<Asana>? = loadAsanasFromDB()
            val userData: UserData? = loadDataFromDB()
            if (asanas != null) selectViewModel.asanas.postValue(asanas.filter {
                it.side != "second"
            })
            if (userData != null) selectViewModel.userData.postValue(userData)

        }
    }

    private fun updateDataDB(asanas: List<Asana>, userData: UserData): Boolean {
        val saveAsanasToDB = saveAsanasToDB(asanas)
        Log.d(LOG_TAG, "MainModel - updateDataDB - saveAsanasToDB: $saveAsanasToDB")
        val saveUserToDB = saveUserToDB(userData)
        Log.d(LOG_TAG, "MainModel - updateDataDB - saveUserToDB: $saveUserToDB")
        return true
    }

    private fun saveAsanasToDB(items: List<Asana>): List<Long> {
        val responseDelete = dbDao.deleteAsanas()
        Log.d(LOG_TAG, "MainModel - saveAsanasToDB responseDelete: $responseDelete")
        val response = dbDao.insertAsanas(items)
        Log.d(LOG_TAG, "MainModel - saveAsanasToDB response: $response")
        return response
    }

    private fun saveUserToDB(user: UserData): Long {
        val response = dbDao.insertUserData(user)
        Log.d(LOG_TAG, "MainModel - saveUserToDB response: $response")
        return response
    }

    private fun loadAsanasFromDB(): List<Asana> {
        val asanas = dbDao.getAsanas()
        Log.d(LOG_TAG, "MainModel - loadAsanasFromDB: $asanas")
        return asanas
    }

    private fun loadDataFromDB(): UserData {
        val userData = dbDao.getUserData()
        Log.d(LOG_TAG, "MainModel - loadDataFromDB: $userData")
        return userData
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
            val message = getCreatedRemoteData(level, knee, loins, neck)
            if (message.message == "true") {
                val responseDeleteActionState = dbDao.deleteActionState()
                profileViewModel.done.postValue(true)
                Log.d(LOG_TAG, "MainModel - create responseDeleteActionState: $responseDeleteActionState")
                Log.d(LOG_TAG, "MainModel - create - message: $message")
            } else {
                Log.d(LOG_TAG, "MainModel - create - message: ${message.error}")
                profileViewModel.error.postValue(message.error)
            }
        }
    }

    private suspend fun getCreatedRemoteData(level: String, knee: String, loins: String, neck: String): Message {
        val request = service.createAsync(
            level = level,
            knee = knee,
            loins = loins,
            neck =neck
        )
        return try {
            val response = request.await()
            if(response.isSuccessful) {
                val message = response.body()!!
                Log.d(LOG_TAG, "MainModel - getCreatedRemoteData - data: $message")
                message
            } else {
                Log.d(LOG_TAG,"MainModel - getCreatedRemoteData error: " + response.errorBody().toString())
                Message(error = response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "MainModel - getCreatedRemoteData - Exception: $e")
            Message(error = e.toString())
        }
    }

    private fun saveTokenDB(token: Token) {
        val response = dbDao.insertToken(token)
        Log.d(LOG_TAG, "LoginModel - saveTokenDB response: $response")
    }

    fun loadUserData() {
        launch {
            if(!isTokenDB()) {
                val uniqueID: String = UUID.randomUUID().toString()
                registerAnonymousUser(uniqueID)
                val token = TokenProvider.getToken(uniqueID, APP_TOKEN)
                saveTokenDB(token)
            }

            val data = loadRemoteUser()
            if (data.error == "no") {
                val saveUserToDB = saveUserToDB(data.userData!!)
                Log.d(LOG_TAG, "MainModel - loadUserToProfile - saveUserToDB: $saveUserToDB")
            } else {
                profileViewModel.error.postValue(data.error)
            }
            var user: UserData? = loadUserFromDB()
            Log.d(LOG_TAG, "MainModel - loadUserToProfile - loadUserFromDB user: $user")
            if (user == null) user = UserData(0)
            profileViewModel.user.postValue(user)
        }
    }


    private fun loadUserFromDB(): UserData {
        val user = dbDao.loadUserData()
        Log.d(LOG_TAG, "MainModel - loadUserFromDB user: $user")
        return user
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

//    fun cancelBackgroundWork() {
//        coroutineContext.cancelChildren()
//        Log.d(LOG_TAG, "MainModel - cancelBackgroundWork")
//    }


}