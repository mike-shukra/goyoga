package ru.yogago.goyoga.ui.action

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.ActionState
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.Data
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.service.DataBase
import kotlin.coroutines.CoroutineContext

class ActionViewModel : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private lateinit var data: Data
    val go: MutableLiveData<Boolean> = MutableLiveData()
    val mData: MutableLiveData<Data> = MutableLiveData()
    private val dbDao = DataBase.db.getDBDao()
    private lateinit var asanasList: List<Asana>
    private var time: Int = 0
    private var isPause: Boolean = false

    fun setTime(t: Int) {
        time = t
        Log.d(LOG_TAG, "setTime - time: $time, isPause: $isPause")
    }

    fun setIsPause(flag: Boolean) {
        launch {
            delay(150)
            Log.d(LOG_TAG, "setIsPause - time: $time, isPause: $isPause")
            isPause = flag
        }
    }

    fun waitAsana() {
        launch {
            play()
        }
    }

    private suspend fun play(){
        while (true) {
            while (time > 0) {
                delay(100)
                Log.d(LOG_TAG, "play - time: $time")
                time--
            }
            go.postValue(true)
            isPause = true
            pauseIfIsPause()
        }
    }

    private suspend fun pauseIfIsPause(){
        while (true){
            Log.d(LOG_TAG, "pauseIfIsPause - true isPause: $isPause time: $time")
            if (isPause) {
                    delay(100)
                    Log.d(LOG_TAG, "pauseIfIsPause - isPause: $isPause time: $time")
            }
            else {
                Log.d(LOG_TAG, "pauseIfIsPause - return isPause: $isPause time: $time")
                return
            }
        }
    }

    fun loadData() = launch {

        val settings = dbDao.getSettings()
        val proportionately = settings?.proportionately!!.toInt()
        val addTime = settings.addTime

        asanasList = loadAsanasFromDB()
        asanasList.forEach {
            it.times = it.times * proportionately + addTime
        }
        val userData: UserData? = loadDataFromDB()
        val aState: ActionState? = dbDao.getActionState()
        Log.d(LOG_TAG, "ActionViewModel - loadActionStateFromDB aState: $aState")
        data = Data(asanas = asanasList, userData = userData, actionState = aState, settings = settings)
        mData.postValue(data)
    }

    private fun loadAsanasFromDB(): List<Asana> {
        val asanas = dbDao.getAsanas()
        Log.d(LOG_TAG, "ActionViewModel - loadAsanasFromDB: $asanas")
        return asanas
    }

    private fun loadDataFromDB(): UserData {
        val userData = dbDao.getUserData()
        Log.d(LOG_TAG, "ActionViewModel - loadDataFromDB userData: $userData")
        return userData
    }

    fun saveActionState(actionState: ActionState) {
        launch {
            val result = dbDao.insertActionState(actionState)
            Log.d(LOG_TAG, "ActionViewModel - saveActionStateToDB actionState: $actionState result: $result")
        }
    }

    fun cancelBackgroundWork() {
        isPause = true
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "ActionViewModel - cancelBackgroundWork this: ${this.hashCode()}")
    }

}