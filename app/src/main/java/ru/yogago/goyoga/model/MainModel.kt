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
import ru.yogago.goyoga.service.AndroidLogger
import ru.yogago.goyoga.service.Logger
import ru.yogago.goyoga.service.Repository
import java.io.IOException

class MainModel: CoroutineScope {

    private val logger: Logger = AndroidLogger()
    private val job = SupervisorJob()
    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Log.e(LOG_TAG,"MainModel coroutineExceptionHandler Error: $throwable in $context")
    }
    override val coroutineContext = Dispatchers.IO + job + coroutineExceptionHandler

    private val dao = DataBase.db.getDBDao()
    private lateinit var selectViewModel: SelectViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private val repository = Repository(dao, ApiFactory.API)

    fun clear() {
        job.cancel()
    }

    fun loadDataFromDB() {
        launch {
            val settings = repository.getSettings()
            val asanas: List<Asana> = repository.getAsanas()
            val userData: UserData = repository.getUserData()
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
            val responseDeleteUserData = repository.deleteUserData()
            logger.d(LOG_TAG, "MainModel - deleteTokenAndUserData responseDeleteUserData: $responseDeleteUserData")
        }
    }

    fun create(parametersDTO: ParametersDTO) {
        launch {
            try {
                val isSuccess : Boolean = repository.createNewSequence(TokenProvider.firebaseToken!!, parametersDTO)
                profileViewModel.done.postValue(isSuccess)
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
            repository.updateParameters(TokenProvider.firebaseToken!!, parametersDTO)
        } catch (e :Exception) {
            e.printStackTrace()
            logger.d(LOG_TAG, "MainModel - updateParameters - message error: " + e.message)
        }

    }

    fun createUserOnServerIfNotExist() {
        launch {
            try {
                Log.d(LOG_TAG, "MainModel - createUserOnServerIfNotExist TokenProvider.firebaseToken: ${TokenProvider.firebaseToken}")
                var booleanDTO = repository.isUserExist(TokenProvider.firebaseToken!!)

                if (!booleanDTO.value)
                    booleanDTO = repository.signUp(TokenProvider.firebaseToken!!)

                if (booleanDTO.value)
                    loadUserData()
                else
                    profileViewModel.error.postValue("Can't create user on server")
            } catch (e: Exception) {
                profileViewModel.error.postValue(e.message)
            }
        }
    }

    private fun loadUserData() {
        launch {
            try {
                val data = repository.getDataAsync(TokenProvider.firebaseToken!!)
                val settings = repository.getSettings()
                if (settings != null) {
                    if (settings.timeOfFiltered < data.settings!!.timeOfFiltered) {
                        repository.deleteAllData()
                    }
                }
                repository.insertData(data)

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
            repository.updateSettingsAddTime(value)
            val actionState = repository.getActionState()
            val userData = repository.getUserData()
            val settings = repository.getSettings()
            val parametersDTO = ParametersDTO(actionState, userData, settings)
            updateParameters(parametersDTO)
        }
    }

    fun updateSettingsProportionately(value: Float) {
        launch {
            repository.updateSettingsProportionately(value)
            val actionState = repository.getActionState()
            val userData = repository.getUserData()
            val settings = repository.getSettings()
            val parametersDTO = ParametersDTO(actionState, userData, settings)
            updateParameters(parametersDTO)
        }
    }

    fun updateSettingsHowToSort(value: Boolean) {
        launch {
            val ud = repository.updateHowToSort(value)
            val actionState = repository.getActionState()
            val userData = repository.getUserData()
            val settings = repository.getSettings()
            val parametersDTO = ParametersDTO(actionState, userData, settings)
            updateParameters(parametersDTO)
        }

    }

}