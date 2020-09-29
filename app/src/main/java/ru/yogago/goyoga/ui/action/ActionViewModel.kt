package ru.yogago.goyoga.ui.action

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.ActionState
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.service.DataBase
import kotlin.coroutines.CoroutineContext

class ActionViewModel : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    var isPlay: Boolean = false
    var actionState = ActionState()
    val userData: MutableLiveData<UserData> = MutableLiveData()
    val asana: MutableLiveData<Asana> = MutableLiveData()
    val isFinish: MutableLiveData<Boolean> = MutableLiveData()
    private val dbDao = DataBase.db.getDBDao()
    private lateinit var asanas: List<Asana>

    fun loadData() = launch {
        actionState = loadActionStateFromDB()
        Log.d(LOG_TAG, "ActionViewModel - loadData actionState: $actionState")
        userData.postValue(loadDataFromDB())
        asanas = loadAsanasFromDB()
        Log.d(LOG_TAG, "ActionViewModel - loadData asanas hashCode: ${asanas.hashCode()}")
        Log.d(LOG_TAG, "ActionViewModel - loadData asanas size: ${asanas.size}")
        asana.postValue(asanas[actionState.currentId-1])
        playAsanas(actionState.currentId)
    }

    private suspend fun playAsanas(current: Int) {
        var i = current-1
        while (i < asanas.size) {
            var time = asanas[i].times
            pauseIfIsPause()
            asana.postValue(asanas[i])
            while(time > 0) {
                pauseIfIsPause()
                delay(100)
                time--
            }
            i++
            saveActionState()
            actionState.currentId = i+1
            Log.d(LOG_TAG, "ActionViewModel - playAsanas actionState.currentId: ${actionState.currentId}")
        }
        isFinish.postValue(true)
        isPlay = false
        actionState.currentId = 1
        actionState.animatorAllCurrentPlayTime = 0
        actionState.animatorItemCurrentPlayTime = 0
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

    fun cancelBackgroundWork() {
        isPlay = false
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "ActionViewModel - cancelBackgroundWork this: ${this.hashCode()}")
    }

}