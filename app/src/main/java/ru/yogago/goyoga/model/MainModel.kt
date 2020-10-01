package ru.yogago.goyoga.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.Data
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.service.TokenProvider
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
    val error: MutableLiveData<String> = MutableLiveData()
    val isToken: MutableLiveData<Boolean> = MutableLiveData()

    fun loadData() {
        launch {
            val data = getRemoteData()
            if (data.error == "no") {
                val updateDataDB = updateDataDB(data.asanas!!, data.userData!!)
                Log.d(LOG_TAG, "MainModel - loadData - updateDataDB: $updateDataDB")
            } else {
                selectViewModel.error.postValue(data.error)
            }
            val asanas = loadAsanasFromDB()
            val userData = loadDataFromDB()
            selectViewModel.asanas.postValue(asanas.filter {
                it.side != "second"
            })
            selectViewModel.userData.postValue(userData)

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

    fun isTokenDB() {
        launch {
            try {
                val response = dbDao.getToken()!!
                Log.d(LOG_TAG, "MainModel - isTokenDB: $response")
                TokenProvider.token = response
                TokenProvider.cookieString = "id_user=${TokenProvider.token.userId}; code_user=${TokenProvider.token.token}"
                isToken.postValue(true)
            }
            catch(e: Exception) {
                isToken.postValue(false)
                Log.d(LOG_TAG, "MainModel - isTokenDB no token: $e")
                val uniqueID: String = UUID.randomUUID().toString()
                Log.d(LOG_TAG, "MainModel - uniqueID: $uniqueID")

            }
        }
    }

    fun setViewModel(m: SelectViewModel) : MainModel {
        this.selectViewModel = m
        return this
    }

    fun cancelBackgroundWork() {
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "ActionViewModel - cancelBackgroundWork")
    }

}