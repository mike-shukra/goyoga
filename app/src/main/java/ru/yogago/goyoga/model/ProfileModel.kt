package ru.yogago.goyoga.model

import android.util.Log
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.ui.profile.EditUserViewModel
import ru.yogago.goyoga.ui.profile.ProfileViewModel
import kotlin.coroutines.CoroutineContext

class ProfileModel: CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val dbDao = DataBase.db.getDBDao()
    private lateinit var editUserViewModel: EditUserViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private val service = ApiFactory.API

    fun create(level: String, knee: String, loins: String, neck: String) {
        launch {
            val data = getCreatedRemoteData(level, knee, loins, neck)
            if (data.error == "no") {
                val responseDeleteActionState = dbDao.deleteActionState()
                Log.d(LOG_TAG, "ProfileModel - create responseDeleteActionState: $responseDeleteActionState")
                Log.d(LOG_TAG, "ProfileModel - create - data: $data")
            } else {
                Log.d(LOG_TAG, "ProfileModel - create - error: ${data.error}")
                profileViewModel.error.postValue(data.error)
            }
        }
    }

    private suspend fun getCreatedRemoteData(level: String, knee: String, loins: String, neck: String): Data {
        val request = service.createAsync(
            level = level,
            knee = knee,
            loins = loins,
            neck =neck
        )
        try {
            val response = request.await()
            return if(response.isSuccessful) {
                val data = response.body()!!
                Log.d(LOG_TAG, "ProfileModel - getRemoteData - data: $data")
                val asanas = data.asanas
                Log.d(LOG_TAG, "ProfileModel - getRemoteData - asanas: $asanas")
                val userData = data.userData
                Log.d(LOG_TAG, "ProfileModel - getRemoteData - userData: $userData")
                data
            } else {
                Log.d(LOG_TAG,"ProfileModel - getRemoteData error: " + response.errorBody().toString())
                Data(error = response.errorBody().toString())
            }
        }
        catch (e: Exception) {
            Log.d(LOG_TAG, "ProfileModel - getRemoteData - Exception: $e")
            return Data(error = e.toString())
        }
    }



    fun updatePassword(password: String) {
        launch {
            val request = service.updatePasswordAsync(password)
            try {
                val response = request.await()
                if(response.isSuccessful) {
                    val token = response.body()!!
                    LoginModel().saveTokenDB(token)
                    Log.d(LOG_TAG, "ProfileModel - updatePassword response.body(): $token")
                    editUserViewModel.isUpdate.postValue(true)
                } else {Log.d(LOG_TAG,"ProfileModel - updatePassword  error: " + response.errorBody().toString())}
            }
            catch (e: Exception) {Log.d(LOG_TAG, "ProfileModel - updatePassword - Exception: $e")}
        }
    }

    fun updateUserName(userData: UserData){
        launch {
            val response = dbDao.insertUserName(userData.first_name!!, userData.id)
            Log.d(LOG_TAG, "ProfileModel - updateUserInfo response: $response")
            updateUserNameRemote(userData)
        }
    }

    fun loadUserToProfile() {
        launch {
            val data = loadRemoteUser()
            if (data.error == "no") {
                val saveUserToDB = saveUserToDB(data.userData!!)
                Log.d(LOG_TAG, "ProfileModel - loadUserToProfile - saveUserToDB: $saveUserToDB")
            } else {
                profileViewModel.error.postValue(data.error)
            }
            var user = loadUserFromDB()
            Log.d(LOG_TAG, "ProfileModel - loadUserToProfile - loadUserFromDB user: $user")
            if (user == null) user = UserData(0)
            profileViewModel.user.postValue(user)

        }
    }

    private fun saveUserToDB(user: UserData): Long {
        val response = dbDao.insertUserData(user)
        Log.d(LOG_TAG, "ProfileModel - saveUserToDB response: $response")
        return response
    }

    private fun loadUserFromDB(): UserData {
        val user = dbDao.loadUserData()
        Log.d(LOG_TAG, "ProfileModel - loadUserFromDB user: $user")
        return user
    }

    fun loadUserToEditUser() {
        launch {
            val response = dbDao.loadUserData()
            Log.d(LOG_TAG, "ProfileModel - loadUserToEditUser user $response")
            editUserViewModel.setUserToVM(response)
        }
    }

    private fun updateUserNameRemote(user: UserData){
        launch {
            val petRequest = service.updateUserNameAsync(user.first_name!!)
            try {
                val response = petRequest.await()
                if(response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(LOG_TAG, "ProfileModel - updateUserRemote  data: $data")
                    editUserViewModel.isUpdate.postValue(true)
                } else {Log.d(LOG_TAG,"ProfileModel - updateUserRemote  error: " + response.errorBody().toString())}
            }
            catch (e: Exception) {Log.d(LOG_TAG, "ProfileModel - updateUserRemote - Exception: $e")}
        }
    }

    private suspend fun loadRemoteUser(): Data {
        val petRequest = service.getDataAsync()
        return try {
            val response = petRequest.await()
            if(response.isSuccessful) {
                val data = response.body()!!
                Log.d(LOG_TAG, "ProfileModel - loadRemoteUser  data: $data")
                data
            } else {
                Log.d(LOG_TAG,"ProfileModel - loadRemoteUser  error: " + response.errorBody().toString())
                Data(error = response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "ProfileModel - loadRemoteUser - Exception: $e")
            Data(error = e.toString())
        }
    }

    fun setEditUserViewModel(m: EditUserViewModel) : ProfileModel {
        this.editUserViewModel = m
        return this
    }

    fun setProfileViewModel(m: ProfileViewModel) : ProfileModel {
        this.profileViewModel = m
        return this
    }

    fun cancelBackgroundWork() {
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "ActionViewModel - cancelBackgroundWork")
    }

}