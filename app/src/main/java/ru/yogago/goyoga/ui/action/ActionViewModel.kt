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
    var actionState = ActionState()
    val userData: MutableLiveData<UserData> = MutableLiveData()
    val asana: MutableLiveData<Asana> = MutableLiveData()
    val isFinish: MutableLiveData<Boolean> = MutableLiveData()
    private val dbDao = DataBase.db.getDBDao()
    private lateinit var asanas: List<Asana>

    fun loadData() = launch {
        actionState = loadActionStateFromDB()
        userData.postValue(loadDataFromDB())
        asanas = loadAsanasFromDB()
        playAsanas(actionState.currentId)
        asana.postValue(asanas[actionState.currentId-1])
    }

    private fun playAsanas(current: Int) = launch {
        var i = current-1
        while (i < asanas.size) {
            // var time = asanas[i].times*10
            var time = asanas[i].times
            pauseIfIsPause()
            asana.postValue(asanas[i])
            while(time > 0) {
                pauseIfIsPause()
                delay(100)
                time--
            }
            i++
            actionState.currentId = i+1
            Log.d(LOG_TAG, "ActionViewModel - playAsanas actionState.currentId: ${actionState.currentId}")
        }
        isFinish.postValue(true)
        actionState.isFinish = true
        actionState.isPay = false
        actionState.currentId = 1
        actionState.animatorAllCurrentPlayTime = 0
        actionState.animatorItemCurrentPlayTime = 0
        saveActionState()
        Log.d(LOG_TAG, "ActionViewModel - playAsanas isFinish: ${actionState.isFinish}")
    }

    private suspend fun pauseIfIsPause(){
        while (true){
            if (!actionState.isPay) {
                delay(100)
            }
            else break
        }
    }

    private suspend fun loadAsanasFromDB(): List<Asana> {
        return withContext(coroutineContext) {
            val asanas = dbDao.getAsanas()
            Log.d(LOG_TAG, "ActionViewModel - loadAsanasFromDB: $asanas")
            return@withContext asanas
        }
    }

    private suspend fun loadDataFromDB(): UserData {
        return withContext(coroutineContext) {
            val userData = dbDao.getUserData()
            Log.d(LOG_TAG, "ActionViewModel - loadDataFromDB userData: $userData")
            return@withContext userData
        }
    }

    private suspend fun loadActionStateFromDB(): ActionState {
        return withContext(coroutineContext) {
            var actionState: ActionState? = dbDao.getActionState()
            Log.d(LOG_TAG, "ActionViewModel - loadActionStateFromDB actionState: $actionState")
            if (actionState == null) actionState = ActionState()
            return@withContext actionState
        }
    }

    fun saveActionState() = launch {
        Log.d(LOG_TAG, "ActionViewModel - saveActionStateToDB actionState: $actionState")
        val result = dbDao.insertActionState(actionState)
        Log.d(LOG_TAG, "ActionViewModel - saveActionStateToDB result: $result")
    }

    fun cancelBackgroundWork() {
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "ActionViewModel - cancelBackgroundWork")
    }

}