package ru.yogago.goyoga.ui.action

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.service.DataBase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ActionViewModel : ViewModel() {

    val asana: MutableLiveData<Asana> = MutableLiveData()
    val userData: MutableLiveData<UserData> = MutableLiveData()
    val progressAll: MutableLiveData<Int> = MutableLiveData()
    private val dbDao = DataBase.db.getDBDao()
    var isPlay: Boolean = false
    lateinit var asanas: List<Asana>

    fun loadData() {
        GlobalScope.launch(Dispatchers.IO) {
            asanas = loadAsanasFromDB()
            val data = loadDataFromDB()
            userData.postValue(data)
            asana.postValue(asanas[0])
        }
    }

    fun playAsanas(count: Int){
        GlobalScope.launch(Dispatchers.IO) {
            asanas.forEach{
                asana.postValue(it)
                Thread.sleep(it.times.toLong()*1000)
                progressAll.postValue((100 / count * it.id).toInt())
            }
        }
    }

    private suspend fun loadAsanasFromDB(): List<Asana> {
        return suspendCoroutine {
            val asanas = dbDao.getAsanas()
            Log.d(LOG_TAG, "MainModel - loadAsanasFromDB: $asanas")
            it.resume(asanas)
        }
    }

    private suspend fun loadDataFromDB(): UserData {
        return suspendCoroutine {
            val userData = dbDao.getUserData()
            Log.d(LOG_TAG, "MainModel - loadDataFromDB: $userData")
            it.resume(userData)
        }
    }


}