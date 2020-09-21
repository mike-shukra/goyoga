package ru.yogago.goyoga.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.Data
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.service.TokenProvider
import ru.yogago.goyoga.ui.home.SelectViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainModel {
    private val dbDao = DataBase.db.getDBDao()
    private val service = ApiFactory.API
    private val db = DataBase.db
    private lateinit var selectViewModel: SelectViewModel
    val error: MutableLiveData<String> = MutableLiveData()
    val isTimeout: MutableLiveData<Boolean> = MutableLiveData()
    val isToken: MutableLiveData<Boolean> = MutableLiveData()

    fun loadData() {
        GlobalScope.launch(Dispatchers.IO) {
            val data = getRemoteData()
            if (data?.error == "no") {
                val saveAsanasToDB = saveAsanasToDB(data.asanas)
                Log.d(LOG_TAG, "MainModel - loadData - saveAsanasToDB: $saveAsanasToDB")

            } else {
                selectViewModel.error.postValue(data?.error)
            }
            val asanas = loadAsanasFromDB()
            selectViewModel.asanas.postValue(asanas)

        }
    }

    private suspend fun saveAsanasToDB(items: List<Asana>): List<Long> {
        return suspendCoroutine {
            val response = dbDao.insertAsanas(items)
            Log.d(LOG_TAG, "MainModel - saveAsanasToDB response: $response")
            it.resume(response)
        }
    }

    private suspend fun loadAsanasFromDB(): List<Asana> {
        return suspendCoroutine {
            val asanas = dbDao.getAll()
            Log.d(LOG_TAG, "MainModel - loadAsanasFromDB: $asanas")
            it.resume(asanas)
        }
    }

    private suspend fun getRemoteData(): Data? {
        return suspendCoroutine {
            GlobalScope.launch(Dispatchers.Main) {
                val request = service.getDataAsync()
                try {
                    val response = request.await()
                    if(response.isSuccessful) {
                        val data = response.body()!!
                        Log.d(LOG_TAG, "MainModel - getRemoteData - data: $data")
                        val asanas = data.asanas
                        Log.d(LOG_TAG, "MainModel - getRemoteData - asanas: $asanas")
                        val userData = data.userData
                        Log.d(LOG_TAG, "MainModel - getRemoteData - userData: $userData")
                        it.resume(data)
                    } else {
                        Log.d(LOG_TAG,"MainModel - getRemoteData error: " + response.errorBody().toString())
                        val error = response.errorBody().toString()
                        selectViewModel.error.postValue(error)
                        it.resume(null)
                    }
                }
                catch (e: Exception) {
                    Log.d(LOG_TAG, "MainModel - getRemoteData - Exception: $e")
                    val error = e.toString()
                    selectViewModel.error.postValue(error)
                    it.resume(null)
                }
            }
        }
    }

    fun isTokenDB() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = db.getDBDao().getToken()
            if (response != null) {
                Log.d(LOG_TAG, "MainModel - isTokenDB: $response")
                TokenProvider.token = response
                TokenProvider.cookieString = "id_user=${TokenProvider.token.userId}; code_user=${TokenProvider.token.token}"
                isToken.postValue(true)
            }
            else {
                isToken.postValue(false)
                Log.d(LOG_TAG, "MainModel - isTokenDB: no token")
            }
        }
    }

    fun setViewModel(m: SelectViewModel) : MainModel {
        this.selectViewModel = m
        return this
    }

}