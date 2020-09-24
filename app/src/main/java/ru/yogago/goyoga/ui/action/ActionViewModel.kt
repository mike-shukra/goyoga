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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ActionViewModel : ViewModel() {

    val actionState: ActionState = ActionState()
    val userData: MutableLiveData<UserData> = MutableLiveData()
    val asana: MutableLiveData<Asana> = MutableLiveData()
    var job: Job? = null
    private val dbDao = DataBase.db.getDBDao()
    lateinit var asanas: List<Asana>

    fun loadData() {
        Log.d(LOG_TAG, "ActionViewModel - loadData")

        GlobalScope.launch(Dispatchers.IO) {
            userData.postValue(loadDataFromDB())
            val actionStateDB = loadActionStateFromDB()
            actionState.isPay = actionStateDB.isPay
            actionState.isFinish = actionStateDB.isFinish
            actionState.currentId = actionStateDB.currentId
            actionState.animatorItemCurrentPlayTime = actionStateDB.animatorItemCurrentPlayTime
            actionState.animatorAllCurrentPlayTime = actionStateDB.animatorAllCurrentPlayTime
            asanas = loadAsanasFromDB()
            if (job == null) playAsanas(actionState.currentId)
//            asana.postValue(asanas[actionState.currentId-1])
        }
    }

    private fun playAsanas(current: Int){
        job = GlobalScope.launch(Dispatchers.IO) {
            var i = current-1
            while (i < asanas.size) {
                // var time = asanas[i].times*10
                var time = asanas[i].times
                pauseIfIsPause()
                asana.postValue(asanas[i])
                while(time > 0) {
                    pauseIfIsPause()
                    Thread.sleep(100)
                    time--
                }
                i++
                actionState.currentId = i+1
                Log.d(LOG_TAG, "ActionViewModel - playAsanas actionState.currentId: ${actionState.currentId}")
            }
            actionState.isFinish = true
            actionState.isPay = false
            actionState.currentId = 1
            actionState.animatorAllCurrentPlayTime = 0
            actionState.animatorItemCurrentPlayTime = 0
            saveActionState()
            Log.d(LOG_TAG, "ActionViewModel - playAsanas isFinish: ${actionState.isFinish}")
        }
    }

    private fun pauseIfIsPause(){
        while (true){
            if (!actionState.isPay) {
                Thread.sleep(100)
            }
            else break
        }
    }

    private suspend fun loadAsanasFromDB(): List<Asana> {
        return suspendCoroutine {
            val asanas = dbDao.getAsanas()
            Log.d(LOG_TAG, "ActionViewModel - loadAsanasFromDB: $asanas")
            it.resume(asanas)
        }
    }

    private suspend fun loadDataFromDB(): UserData {
        return suspendCoroutine {
            val userData = dbDao.getUserData()
            Log.d(LOG_TAG, "ActionViewModel - loadDataFromDB userData: $userData")
            it.resume(userData)
        }
    }

    private suspend fun loadActionStateFromDB(): ActionState {
        return suspendCoroutine {
            var actionState: ActionState? = dbDao.getActionState()
            Log.d(LOG_TAG, "ActionViewModel - loadActionStateFromDB actionState: $actionState")
            if (actionState == null) actionState = ActionState()
            it.resume(actionState)
        }
    }

    fun saveActionState() {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(LOG_TAG, "ActionViewModel - saveActionStateToDB actionState: $actionState")
            val result = dbDao.insertActionState(actionState)
            Log.d(LOG_TAG, "ActionViewModel - saveActionStateToDB result: $result")
        }
    }


}