package ru.yogago.goyoga.model

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.ui.profile.EditUserViewModel
import ru.yogago.goyoga.ui.profile.ProfileViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileModel {
    private val LOG_TAG: String = "myLog"
    private val dbDao = DataBase.db.getDBDao()
    private lateinit var editUserViewModel: EditUserViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private val service = ApiFactory.API

    fun updatePassword(password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val request = service.updatePasswordAsync(password)
            try {
                val response = request.await()
                if(response.isSuccessful) {
                    val token = response.body()!!
                    LoginModel().saveTokenDB(token)
                    Log.d(LOG_TAG, "ProfileModel - updatePassword response.body(): $token")
                } else {Log.d(LOG_TAG,"ProfileModel - updatePassword  error: " + response.errorBody().toString())}
            }
            catch (e: Exception) {Log.d(LOG_TAG, "ProfileModel - updatePassword - Exception: $e")}
        }
    }

    fun updateUserInfo(user: UserData){
        GlobalScope.launch(Dispatchers.IO) {
            val response = dbDao.insertUser(user)
            Log.d(LOG_TAG, "ProfileModel - updateUserInfo response: $response")
            updateUserRemote(user)
        }
    }

    fun loadUserToProfile() {
        GlobalScope.launch(Dispatchers.IO) {
            val data = loadRemoteUser()
            if (data.error == "no") {
                val saveUserToDB = saveUserToDB(data.userData)
                Log.d(AppConstants.LOG_TAG, "MainModel - loadUserToProfile - saveUserToDB: $saveUserToDB")

            } else {
                profileViewModel.error.postValue(data.error)
            }
            val user = loadUserFromDB()
            profileViewModel.user.postValue(user)

        }
    }

    private suspend fun saveUserToDB(user: UserData): Long {
        return suspendCoroutine {
            val response = dbDao.insertUser(user)
            Log.d(AppConstants.LOG_TAG, "MainModel - saveUserToDB response: $response")
            it.resume(response)
        }
    }

    private suspend fun loadUserFromDB(): UserData {
        return suspendCoroutine {
            val user = dbDao.loadUser()
            Log.d(AppConstants.LOG_TAG, "MainModel - loadUserFromDB user: $user")
            it.resume(user)
        }
    }

    fun loadUserToEditUser() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = dbDao.loadUser()
            Log.d(LOG_TAG, "ProfileModel - loadUserToEditUser user $response")
            editUserViewModel.setUserToVM(response)
        }
    }

    private fun updateUserRemote(user: UserData){
        GlobalScope.launch(Dispatchers.IO) {
            val petRequest = service.updateUserAsync(user)
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
        return suspendCoroutine {
            GlobalScope.launch(Dispatchers.IO) {
                val petRequest = service.getDataAsync()
                try {
                    val response = petRequest.await()
                    if(response.isSuccessful) {
                        val data = response.body()!!
                        Log.d(LOG_TAG, "ProfileModel - loadRemoteUser  data: $data")
                        it.resume(data)
                    } else {Log.d(LOG_TAG,"ProfileModel - loadRemoteUser  error: " + response.errorBody().toString())}
                }
                catch (e: Exception) {Log.d(LOG_TAG, "ProfileModel - loadRemoteUser - Exception: $e")}
            }
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

}