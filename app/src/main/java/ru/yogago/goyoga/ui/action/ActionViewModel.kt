package ru.yogago.goyoga.ui.action

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.ActionState
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.service.DataBase
import kotlin.coroutines.CoroutineContext

class ActionViewModel : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    var isPlay: Boolean = false
    private lateinit var actionState: ActionState
    val userData: MutableLiveData<UserData> = MutableLiveData()
    val asana: MutableLiveData<Asana> = MutableLiveData()
    val asans: MutableLiveData<List<Asana>> = MutableLiveData()
    val isFinish: MutableLiveData<Boolean> = MutableLiveData()
    private val dbDao = DataBase.db.getDBDao()
    private lateinit var asanas: List<Asana>
    var id: Long = 0L

    fun loadData() = launch {
        val settings = dbDao.getSettings()
        val proportionately = settings?.proportionately!!.toInt()
        val addTime = settings.addTime

        if (id != 0L) dbDao.insertActionState(ActionState(currentId = id.toInt()))
        actionState = loadActionStateFromDB()
        Log.d(LOG_TAG, "ActionViewModel - loadData actionState: $actionState")
        asanas = loadAsanasFromDB()
        asanas.forEach {
            it.times = it.times * proportionately + addTime
        }
        asans.postValue(asanas)
        Log.d(LOG_TAG, "ActionViewModel - loadData asanas hashCode: ${asanas.hashCode()}")
        Log.d(LOG_TAG, "ActionViewModel - loadData asanas size: ${asanas.size}")
        val data: UserData? = loadDataFromDB()
        if (data != null) userData.postValue(loadDataFromDB())
        if (asanas.isNotEmpty()) {
            asana.postValue(asanas[actionState.currentId-1])
            playAsanas(actionState.currentId)
        }
    }

    private suspend fun playAsanas(current: Int) {
        var i = current-1
        while (i < asanas.size) {
            var time = asanas[i].times*10
            pauseIfIsPause()
            asana.postValue(asanas[i])
            while(time > 0) {
                pauseIfIsPause()
                delay(100)
                time--
            }
            i++
            actionState.currentId = i+1
            saveActionState()
            Log.d(LOG_TAG, "ActionViewModel - playAsanas actionState.currentId: ${actionState.currentId}")
        }
        isFinish.postValue(true)
        isPlay = false
        actionState.currentId = 1
        saveActionState()
        playAsanas(1)
    }

    private suspend fun pauseIfIsPause(){
        while (true)
            if (!isPlay) delay(100)
            else break
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

    private fun loadActionStateFromDB(): ActionState {
        var actionState: ActionState? = dbDao.getActionState()
        Log.d(LOG_TAG, "ActionViewModel - loadActionStateFromDB actionState: $actionState")
        if (actionState == null) actionState = ActionState()
        return actionState
    }

    private fun saveActionState() {
        val result = dbDao.insertActionState(actionState)
        Log.d(LOG_TAG, "ActionViewModel - saveActionStateToDB result: $result")
    }

    fun saveActionState(id: Int, isPlay: Boolean) {
        launch {
            val result = dbDao.insertActionState(ActionState(isPlay = isPlay, currentId = id))
            Log.d(LOG_TAG, "ActionViewModel - saveActionStateToDB result: $result")
        }
    }

    fun cancelBackgroundWork() {
        isPlay = false
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "ActionViewModel - cancelBackgroundWork this: ${this.hashCode()}")
    }

}