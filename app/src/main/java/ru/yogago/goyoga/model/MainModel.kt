package ru.yogago.goyoga.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG
import ru.yogago.goyoga.ui.profile.ProfileViewModel
import ru.yogago.goyoga.ui.select.SelectViewModel
import ru.yogago.goyoga.service.AndroidLogger
import ru.yogago.goyoga.service.Logger
import ru.yogago.goyoga.service.Repository
import java.io.IOException
import javax.inject.Inject

class MainModel @Inject constructor(
    private val repository: Repository
) : CoroutineScope  {

    private val _navigationFlow = MutableSharedFlow<Unit>(replay = 0)
    val navigationFlow: SharedFlow<Unit> = _navigationFlow
    private val logger: Logger = AndroidLogger()
    private val job = SupervisorJob()
    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Log.e(LOG_TAG,"MainModel coroutineExceptionHandler Error: $throwable in $context")
    }
    override val coroutineContext = Dispatchers.IO + job + coroutineExceptionHandler

    private lateinit var selectViewModel: SelectViewModel
    private lateinit var profileViewModel: ProfileViewModel

    fun clear() {
        job.cancel()
    }

    fun loadDataFromDB() {
        launch {
            val settings = repository.getSettings()
            val asanas: List<Asana> = repository.getAsanas()
            val userData: UserData = repository.getUserData()
            userData.allTime = (userData.allTime * settings.proportionately).toInt() + (settings.addTime * userData.allCount)
            selectViewModel.userData.postValue(userData)
            selectViewModel.asanas.postValue(asanas)

            logger.d(LOG_TAG, "MainModel - loadDataFromDB asanas: $asanas , userData: $userData")
        }
    }

    fun deleteUserData(){
        launch {
            val responseDeleteUserData = repository.deleteUserData()
            logger.d(LOG_TAG, "MainModel - deleteTokenAndUserData responseDeleteUserData: $responseDeleteUserData")
        }
    }

    fun create(parametersDTO: ParametersDTO) {
        Log.d(LOG_TAG, "MainModel - create - parametersDTO: $parametersDTO")
        launch {
            try {
                val isSuccess : Boolean = repository.createNewSequence(parametersDTO)
                if (isSuccess) _navigationFlow.emit(Unit)
            } catch (e: IOException) {
                e.printStackTrace()
                val errorMessage = e.message
                Log.d(LOG_TAG, "MainModel - create - message error: $errorMessage")
                profileViewModel.error.postValue(errorMessage)
            }
        }
    }

//    private suspend fun updateParameters(parametersDTO: ParametersDTO) {
//        try {
//            repository.updateParameters(parametersDTO)
//        } catch (e :Exception) {
//            e.printStackTrace()
//            logger.d(LOG_TAG, "MainModel - updateParameters - message error: " + e.message)
//        }
//
//    }

    fun createUserOnServerIfNotExist() {
        launch {
            try {
                if (!repository.isUserExist().value) {
                    repository.signUp()
                    loadUserData()
                }
                else {
                    loadDataFromDataBase()
                }
            } catch (e: Exception) {
                profileViewModel.error.postValue(e.message)
            }
        }
    }

    private fun loadDataFromDataBase() {
        launch {
            val settings = repository.getSettings()
            Log.d(LOG_TAG, "MainModel - loadDataFromDataBase settings: $settings")
            if  (settings == null) {
                loadUserData()
            } else {
                val userData = repository.getUserData()
                profileViewModel.proportionately.postValue(settings.proportionately)
                profileViewModel.addTime.postValue(settings.addTime)
                profileViewModel.userData.postValue(userData)
            }
        }
    }

    private fun loadUserData() {
        launch {
            try {
                val data = repository.getDataAsync()
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

    fun setSelectViewModel(vm: SelectViewModel) : MainModel {
        this.selectViewModel = vm
        return this
    }

    fun setProfileViewModel(vm: ProfileViewModel) : MainModel {
        this.profileViewModel = vm
        return this
    }

//    fun cancelBackgroundWork() {
//        coroutineContext.cancelChildren()
//        Log.d(LOG_TAG, "MainModel - cancelBackgroundWork")
//    }

//    fun updateSettingsAddTime(value: Int) {
//        launch {
//            repository.updateSettingsAddTime(value)
//            val actionState = repository.getActionState()
//            val userData = repository.getUserData()
//            val settings = repository.getSettings()
//            val parametersDTO = ParametersDTO(actionState, userData, settings)
//            updateParameters(parametersDTO)
//        }
//    }

//    fun updateSettingsProportionately(value: Float) {
//        launch {
//            repository.updateSettingsProportionately(value)
//            val actionState = repository.getActionState()
//            val userData = repository.getUserData()
//            val settings = repository.getSettings()
//            val parametersDTO = ParametersDTO(actionState, userData, settings)
//            updateParameters(parametersDTO)
//        }
//    }

//    fun updateSettingsHowToSort(value: Boolean) {
//        launch {
//            val ud = repository.updateHowToSort(value)
//            val actionState = repository.getActionState()
//            val userData = repository.getUserData()
//            val settings = repository.getSettings()
//            val parametersDTO = ParametersDTO(actionState, userData, settings)
//            updateParameters(parametersDTO)
//        }
//
//    }

}