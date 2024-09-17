package ru.yogago.goyoga.model

import android.util.Log
import kotlinx.coroutines.*
import retrofit2.Call
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.service.TokenProvider
import ru.yogago.goyoga.ui.profile.ProfileViewModel
import ru.yogago.goyoga.ui.select.SelectViewModel
import kotlin.coroutines.CoroutineContext
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainModel: CoroutineScope {

    private val job = SupervisorJob()
    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Log.e(LOG_TAG,"MainModel coroutineExceptionHandler Error: $throwable in $context")
    }
    override val coroutineContext = Dispatchers.IO + job + coroutineExceptionHandler

    private val dbDao = DataBase.db.getDBDao()
    private lateinit var selectViewModel: SelectViewModel
    private lateinit var profileViewModel: ProfileViewModel

    fun clear() {
        job.cancel()
    }

    fun loadDataFromDB() {
        launch {
            val settings = dbDao.getSettings()
            val asanas: List<Asana> = dbDao.getAsanas()
            val userData: UserData = dbDao.getUserData()
            userData.allTime = (userData.allTime * settings?.proportionately!!).toInt() + (settings.addTime * userData.allCount)
            selectViewModel.userData.postValue(userData)
            selectViewModel.asanas.postValue(asanas)
//            selectViewModel.asanas.postValue(asanas.filter { asana ->
//                asana.side != "first"
//            })
        }
    }

    fun deleteUserData(){
        launch {
            val responseDeleteUserData = dbDao.deleteUserData()
            Log.d(LOG_TAG, "MainModel - deleteTokenAndUserData responseDeleteUserData: $responseDeleteUserData")
        }
    }

    fun create(level: Long, proportionally: Float, addTime: Int, knee: Boolean, loins: Boolean,
                       neck: Boolean, inverted: Boolean, sideBySideSort: Boolean) {
        val parametersDTO = ParametersDTO(
            now = 1,
            allTime = 0,
            allCount = 0,
            level = Level.values()[level.toInt()].toString(),
            proportionally = proportionally,
            addTime = addTime,
            dangerKnee = knee,
            dangerLoins = loins,
            dangerNeck = neck,
            inverted = inverted,
            sideBySideSort = sideBySideSort,
            System.currentTimeMillis()
        )
        launch {
            try {
                val deferred =
                    ApiFactory.API.createAsync(TokenProvider.firebaseToken!!, parametersDTO)
                val data: Data = deferred.await()

                val del = dbDao.deleteAsanas()
                val insA = dbDao.insertAsanas(data.asanas!!)
                val insS = dbDao.insertSettings(data.settings!!)
                val insAs = dbDao.insertActionState(data.actionState!!)
                val insD = dbDao.insertUserData(data.userData!!)
                Log.d(
                    LOG_TAG,
                    "MainModel - updateDB del: $del, insA: $insA, insS: $insS, idsD: $insD"
                )
                profileViewModel.done.postValue(true)

            } catch (e: IOException) {
                e.printStackTrace()
                val errorMessage = e.message
                Log.d(LOG_TAG, "MainModel - create - message error: $errorMessage")
                profileViewModel.error.postValue(errorMessage)
            }
        }
    }

    private suspend fun updateParameters(parametersDTO: ParametersDTO) {
        try {
            val deferred = ApiFactory.API.updateParameters(TokenProvider.firebaseToken!!, parametersDTO)
            val data: Data = deferred.await()
            Log.d(LOG_TAG, "MainModel - updateParameters data: $data")
            dbDao.deleteAsanas()
            dbDao.insertAsanas(data.asanas!!)

        } catch (e :Exception) {
            e.printStackTrace()
            Log.d(LOG_TAG, "MainModel - updateParameters - message error: " + e.message)
        }

    }

    fun createUserOnServerIfNotExist() {
        launch {
            try {
                Log.d(LOG_TAG, "MainModel - createUserOnServerIfNotExist TokenProvider.firebaseToken: ${TokenProvider.firebaseToken}")
                var deferred = ApiFactory.API.isUserExist(TokenProvider.firebaseToken!!)
                var booleanDTO = deferred.await()

                if (!booleanDTO.value) {
                    deferred = ApiFactory.API.signUp(TokenProvider.firebaseToken!!)
                    booleanDTO = deferred.await()
                }
                if (booleanDTO.value)
                    loadUserData()

            } catch (e: Exception) {
                profileViewModel.error.postValue(e.message)
            }
        }
    }

    private fun loadUserData() {
        launch {
            try {

                val deferred = ApiFactory.API.getDataAsync(TokenProvider.firebaseToken!!)
                val data = deferred.await()

                val settings = dbDao.getSettings()
                if (settings != null) {
                    if (settings.timeOfFiltered < data.settings!!.timeOfFiltered) {
                        dbDao.deleteAsanas()
                        dbDao.deleteUserData()
                        dbDao.deleteSettings()
                        dbDao.deleteActionState()
                        dbDao.insertAsanas(data.asanas!!)
                        dbDao.insertSettings(data.settings!!)
                        dbDao.insertUserData(data.userData!!)
                        dbDao.insertActionState(data.actionState!!)
                    }
                } else {
                    dbDao.insertAsanas(data.asanas!!)
                    dbDao.insertSettings(data.settings!!)
                    dbDao.insertUserData(data.userData!!)
                    dbDao.insertActionState(data.actionState!!)
                }
                profileViewModel.proportionately.postValue(data.settings!!.proportionately)
                profileViewModel.addTime.postValue(data.settings!!.addTime)
                profileViewModel.userData.postValue(data.userData!!)
            } catch (e: Exception) {
                profileViewModel.error.postValue(e.message)
            }
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
            val actionState = dbDao.getActionState()
            val userData = dbDao.getUserData()
            val settings = dbDao.getSettings()
            val parametersDTO = ParametersDTO(actionState, userData, settings)
            updateParameters(parametersDTO)
        }
    }

    fun updateSettingsProportionately(value: Float) {
        launch {
            dbDao.updateSettingsProportionately(value)
            val actionState = dbDao.getActionState()
            val userData = dbDao.getUserData()
            val settings = dbDao.getSettings()
            val parametersDTO = ParametersDTO(actionState, userData, settings)
            updateParameters(parametersDTO)
        }
    }

    fun updateSettingsHowToSort(value: Boolean) {
        launch {
            val ud = dbDao.updateHowToSort(value)
            val actionState = dbDao.getActionState()
            val userData = dbDao.getUserData()
            val settings = dbDao.getSettings()
            val parametersDTO = ParametersDTO(actionState, userData, settings)
            updateParameters(parametersDTO)
        }

    }

}