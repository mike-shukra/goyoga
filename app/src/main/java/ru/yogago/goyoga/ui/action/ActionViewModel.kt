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
    val mCurrentAsana: MutableLiveData<Int> = MutableLiveData()
    val isHolder: MutableLiveData<Boolean> = MutableLiveData()
    val go: MutableLiveData<Int> = MutableLiveData()
    val mData: MutableLiveData<Data> = MutableLiveData()
    private val dbDao = DataBase.db.getDBDao()
    private lateinit var asanasList: List<Asana>
    private var time: Int = 0

    fun setTime(t: Int) {
        time = t
        Log.d(LOG_TAG, "ActionViewModel - setTime - time: $time")
    }

    fun waitAsana() {
        Log.d(LOG_TAG, "ActionViewModel - waitAsana this: ${this.hashCode()}")
        launch {
            Log.d(LOG_TAG, "ActionViewModel - waitAsana launch this: ${this.hashCode()}")
            while (time > 0) {
                delay(100)
                time--
            }
            go.postValue((mCurrentAsana.value!! + 1))
            Log.d(LOG_TAG, "ActionViewModel - play go")
        }
    }

    fun loadData() = launch {

        val settings = dbDao.getSettings()
        val proportionately = settings!!.proportionately.toInt()

        asanasList = loadAsanasFromDB()
        asanasList.forEach {
            it.times = it.times * proportionately + settings.addTime
        }
        val aState: ActionState = dbDao.getActionState()
        val userData: UserData = loadUserDataFromDB()

        data = Data(asanas = asanasList, userData = userData, actionState = aState, settings = settings)
        mData.postValue(data)
    }

    private suspend fun loadAsanasFromDB(): List<Asana> {
        val asanas = dbDao.getAsanas()
        Log.d(LOG_TAG, "ActionViewModel - loadAsanasFromDB: $asanas")
        return asanas
    }

    private suspend fun loadUserDataFromDB(): UserData {
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
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "ActionViewModel - cancelBackgroundWork this: ${this.hashCode()}")
    }

}