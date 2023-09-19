package ru.yogago.goyoga.model

import android.util.Log
import kotlinx.coroutines.*
import retrofit2.Call
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.Companion.APP_TOKEN_B
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.service.TokenProvider
import ru.yogago.goyoga.ui.profile.ProfileViewModel
import ru.yogago.goyoga.ui.select.SelectViewModel
import java.util.*
import kotlin.coroutines.CoroutineContext
import retrofit2.Callback
import retrofit2.Response

class MainModel: CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val dbDao = DataBase.db.getDBDao()
    private lateinit var selectViewModel: SelectViewModel
    private lateinit var profileViewModel: ProfileViewModel

    fun loadData() {
        launch {
            delay(300)
            val data = getRemoteData()
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
            val settings = dbDao.getSettings()
            val asanas: List<Asana>? = dbDao.getAsanas()
            val userData: UserData? = dbDao.getUserData()
            userData?.let {
                it.allTime = (it.allTime * settings?.proportionately!!).toInt() + (settings.addTime * it.allCount)
                selectViewModel.userData.postValue(userData)
                selectViewModel.asanas.postValue(asanas?.filter {asana ->
                    asana.side != "first"
                })
            }
        }
    }

    private suspend fun getRemoteData(): Data {
        try {
            val request = ApiFactory.API_2.getDataAsync(header = TokenProvider.firebaseToken!!)
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

    private suspend fun tryTokenDB(): Boolean {
        val responseTokenDB: Token? = dbDao.getToken()
        Log.d(LOG_TAG, "MainModel - isTokenDB: $responseTokenDB")
        return if (responseTokenDB != null) {
            TokenProvider.token = responseTokenDB
            true
        } else {
            Log.d(LOG_TAG, "MainModel - tryTokenDB: no token")
            val uniqueID: String = UUID.randomUUID().toString()
            val token = TokenProvider.getToken(uniqueID, APP_TOKEN_B)
            Log.d(LOG_TAG, "MainModel - loadUserData - tryTokenDB token: $token")
            token.error?.let {
                profileViewModel.error.postValue(token.error)
            }
            val response = dbDao.insertToken(token)
            Log.d(LOG_TAG, "MainModel - tryTokenDB - saveTokenDB response: $response")
            false
        }
    }

    fun deleteTokenAndUserData(){
        launch {
            val responseDeleteTokenDB = dbDao.deleteToken()
            Log.d(LOG_TAG, "MainModel - deleteTokenAndUserData responseDeleteTokenDB: $responseDeleteTokenDB")
            val responseDeleteUserData = dbDao.deleteUserData()
            Log.d(LOG_TAG, "MainModel - deleteTokenAndUserData responseDeleteUserData: $responseDeleteUserData")
            tryTokenDB()
        }
    }

    fun create(level: Long, knee: Boolean, loins: Boolean, neck: Boolean, inverted: Boolean) {
        val parametersDTO = ParametersDTO(
            now = 1,
            allTime = 0,
            allCount = 0,
            level = Level.values()[level.toInt()].toString(),
            dangerKnee = knee,
            dangerLoins = loins,
            dangerNeck = neck,
            inverted = inverted
        )
        val call = ApiFactory.API_2.createAsyncTwo(TokenProvider.firebaseToken!!, parametersDTO)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d(LOG_TAG, "MainModel - create - message: ${responseBody.toString()}")
                    profileViewModel.done.postValue(true)
                } else {
                    val errorMessage = response.message()
                    Log.d(LOG_TAG, "MainModel - create - message error: $errorMessage")
                    profileViewModel.error.postValue(errorMessage)
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                // Request failed due to a network error or other issues
                // Handle the failure
            }
        })

    }

    private suspend fun createData(level: Level, knee: Boolean, loins: Boolean, neck: Boolean, inverted: Boolean){
        return withContext(TokenProvider.coroutineContext) {
            dbDao.insertActionState(ActionState())
            val parametersDTO = ParametersDTO(
                now = 1,
                allTime = 0,
                allCount = 0,
                level = level.toString(),
                dangerKnee = knee,
                dangerLoins = loins,
                dangerNeck = neck,
                inverted = inverted
            )
            val requestMessageCreate = ApiFactory.API.createAsync(TokenProvider.firebaseToken!!, parametersDTO)
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
            if(!tryTokenDB()) {
                dbDao.insertSettings(Settings())
                createData(
                    level = Level.NOT_SPECIFIED,
                    knee = false,
                    loins = false,
                    neck = false,
                    inverted = false
                )
            }
            val settings = dbDao.getSettings()
            profileViewModel.proportionately.postValue(settings?.proportionately)
            profileViewModel.addTime.postValue(settings?.addTime)

            val data = getRemoteData()
            if (data.error == "no") {
                val responseDelete = dbDao.deleteAsanas()
                Log.d(LOG_TAG, "MainModel - loadUserData responseDelete: $responseDelete")
                val responseInsertAsanas = dbDao.insertAsanas(data.asanas!!)
                Log.d(LOG_TAG, "MainModel - loadUserData responseInsertAsanas: $responseInsertAsanas")
                val responseInsertUserData = dbDao.insertUserData(data.userData!!)
                Log.d(LOG_TAG, "MainModel - loadUserData responseInsertUserData: $responseInsertUserData")
            } else {
                Log.d(LOG_TAG, "MainModel - loadUserData data.error: ${data.error}")
                profileViewModel.error.postValue(data.error)
            }
            var user: UserData? = dbDao.loadUserData()
            Log.d(LOG_TAG, "MainModel - loadUserData - loadUserFromDB user: $user")
            if (user == null) user = UserData(0)
            profileViewModel.userData.postValue(user)
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

    fun updateSettingsAddTime(value: Int) {
        launch {
            dbDao.updateSettingsAddTime(value)
        }
    }

    fun updateSettingsProportionately(value: Float) {
        launch {
            dbDao.updateSettingsProportionately(value)
        }
    }

}