package ru.yogago.goyoga.model

import android.util.Log
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.AppConstants.APP_TOKEN
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.Data
import ru.yogago.goyoga.data.Token
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.service.ApiFactory
import ru.yogago.goyoga.service.DataBase
import ru.yogago.goyoga.service.TokenProvider
import ru.yogago.goyoga.ui.profile.EditUserViewModel
import ru.yogago.goyoga.ui.profile.ProfileViewModel
import ru.yogago.goyoga.ui.select.SelectViewModel
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainModel: CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val dbDao = DataBase.db.getDBDao()
    private val service = ApiFactory.API
    private lateinit var selectViewModel: SelectViewModel
    private lateinit var editUserViewModel: EditUserViewModel
    private lateinit var profileViewModel: ProfileViewModel

    fun loadData() {
        launch {
            val data = getRemoteData()
            if (data.error == "no") {
                val updateDataDB = updateDataDB(data.asanas!!, data.userData!!)
                Log.d(LOG_TAG, "MainModel - loadData - updateDataDB: $updateDataDB")
            } else {
                selectViewModel.error.postValue(data.error)
            }
            val asanas: List<Asana>? = loadAsanasFromDB()
            val userData: UserData? = loadDataFromDB()
            if (asanas != null) selectViewModel.asanas.postValue(asanas.filter {
                it.side != "second"
            })
            if (userData != null) selectViewModel.userData.postValue(userData)

        }
    }

    private fun updateDataDB(asanas: List<Asana>, userData: UserData): Boolean {
        val saveAsanasToDB = saveAsanasToDB(asanas)
        Log.d(LOG_TAG, "MainModel - updateDataDB - saveAsanasToDB: $saveAsanasToDB")
        val saveUserToDB = saveUserToDB(userData)
        Log.d(LOG_TAG, "MainModel - updateDataDB - saveUserToDB: $saveUserToDB")
        return true
    }

    private fun saveAsanasToDB(items: List<Asana>): List<Long> {
        val responseDelete = dbDao.deleteAsanas()
        Log.d(LOG_TAG, "MainModel - saveAsanasToDB responseDelete: $responseDelete")
        val response = dbDao.insertAsanas(items)
        Log.d(LOG_TAG, "MainModel - saveAsanasToDB response: $response")
        return response
    }

    private fun saveUserToDB(user: UserData): Long {
        val response = dbDao.insertUserData(user)
        Log.d(LOG_TAG, "MainModel - saveUserToDB response: $response")
        return response
    }

    private fun loadAsanasFromDB(): List<Asana> {
        val asanas = dbDao.getAsanas()
        Log.d(LOG_TAG, "MainModel - loadAsanasFromDB: $asanas")
        return asanas
    }

    private fun loadDataFromDB(): UserData {
        val userData = dbDao.getUserData()
        Log.d(LOG_TAG, "MainModel - loadDataFromDB: $userData")
        return userData
    }

    private suspend fun getRemoteData(): Data {
        val request = service.getDataAsync()
        try {
            val response = request.await()
            return if(response.isSuccessful) {
                val data = response.body()!!
                Log.d(LOG_TAG, "MainModel - getRemoteData - data: $data")
                val asanas = data.asanas
                Log.d(LOG_TAG, "MainModel - getRemoteData - asanas: $asanas")
                val userData = data.userData
                Log.d(LOG_TAG, "MainModel - getRemoteData - userData: $userData")
                data
            } else {
                Log.d(LOG_TAG,"MainModel - getRemoteData error: " + response.errorBody().toString())
                Data(error = response.errorBody().toString())
            }
        }
        catch (e: Exception) {
            Log.d(LOG_TAG, "MainModel - getRemoteData - Exception: $e")
            return  Data(error = e.toString())
        }
    }

    private fun isTokenDB(): Boolean {
        val response: Token? = dbDao.getToken()
        Log.d(LOG_TAG, "MainModel - isTokenDB: $response")
        return if (response != null) {
            TokenProvider.token = response
            TokenProvider.cookieString = "id_user=${TokenProvider.token.userId}; code_user=${TokenProvider.token.token}"
            true
        } else {
            Log.d(LOG_TAG, "MainModel - isTokenDB: no token")
            false
        }
    }

    private suspend fun registerAnonymousUser(uniqueID: String) {
        val request = service.registerAnonymousUserAsync(uniqueID, APP_TOKEN)
        try {
            val response = request.await()
            val message = response.body()!!
            if (message.error == null) {
                Log.d(LOG_TAG, "MainModel - registerRemote message: $message")
            }
        }
        catch (e: java.lang.Exception){
            Log.d(LOG_TAG, "MainModel - registerRemote Exception: $e")
        }
    }

    fun create(level: String, knee: String, loins: String, neck: String) {
        launch {
            val data = getCreatedRemoteData(level, knee, loins, neck)
            if (data.error == "no") {
                val responseDeleteActionState = dbDao.deleteActionState()
                Log.d(LOG_TAG, "MainModel - create responseDeleteActionState: $responseDeleteActionState")
                Log.d(LOG_TAG, "MainModel - create - data: $data")
            } else {
                Log.d(LOG_TAG, "MainModel - create - error: ${data.error}")
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
                Log.d(LOG_TAG, "MainModel - getRemoteData - data: $data")
                val asanas = data.asanas
                Log.d(LOG_TAG, "MainModel - getRemoteData - asanas: $asanas")
                val userData = data.userData
                Log.d(LOG_TAG, "MainModel - getRemoteData - userData: $userData")
                data
            } else {
                Log.d(LOG_TAG,"MainModel - getRemoteData error: " + response.errorBody().toString())
                Data(error = response.errorBody().toString())
            }
        }
        catch (e: Exception) {
            Log.d(LOG_TAG, "MainModel - getRemoteData - Exception: $e")
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
                    Log.d(LOG_TAG, "MainModel - updatePassword response.body(): $token")
                    editUserViewModel.isUpdate.postValue(true)
                } else {
                    Log.d(LOG_TAG,"MainModel - updatePassword  error: " + response.errorBody().toString())
                    editUserViewModel.error.postValue(response.errorBody().toString())
                }
            }
            catch (e: Exception) {
                Log.d(LOG_TAG, "MainModel - updatePassword - Exception: $e")
                editUserViewModel.error.postValue(e.toString())
            }
        }
    }

    fun updateUserName(userData: UserData){
        launch {
            if (updateUserNameRemote(userData)) {
                val response = dbDao.insertUserName(userData.first_name!!, userData.id)
                Log.d(LOG_TAG, "MainModel - updateUserInfo response: $response")
            }
        }
    }

    fun loadUserData() {
        launch {
            if(!isTokenDB()) {
                val uniqueID: String = UUID.randomUUID().toString()
                registerAnonymousUser(uniqueID)
                val token = TokenProvider.getToken(uniqueID, APP_TOKEN)
                LoginModel().saveTokenDB(token)
            }

            val data = loadRemoteUser()
            if (data.error == "no") {
                val saveUserToDB = saveUserToDB(data.userData!!)
                Log.d(LOG_TAG, "MainModel - loadUserToProfile - saveUserToDB: $saveUserToDB")
            } else {
                profileViewModel.error.postValue(data.error)
            }
            var user: UserData? = loadUserFromDB()
            Log.d(LOG_TAG, "MainModel - loadUserToProfile - loadUserFromDB user: $user")
            if (user == null) user = UserData(0)
            profileViewModel.user.postValue(user)

        }
    }

    private fun loadUserFromDB(): UserData {
        val user = dbDao.loadUserData()
        Log.d(LOG_TAG, "MainModel - loadUserFromDB user: $user")
        return user
    }

    fun loadUserToEditUser() {
        launch {
            val response: UserData? = dbDao.loadUserData()
            Log.d(LOG_TAG, "MainModel - loadUserToEditUser user $response")
            if (response != null) editUserViewModel.setUserToVM(response)
//            else editUserViewModel.error.postValue("")
        }
    }

    private suspend fun updateUserNameRemote(user: UserData): Boolean = withContext(coroutineContext){
            val petRequest = service.updateUserNameAsync(user.first_name!!)
            try {
                val response = petRequest.await()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(LOG_TAG, "MainModel - updateUserRemote  data: $data")
                    if (data.result!!) {
                        editUserViewModel.isUpdate.postValue(true)
                        return@withContext true
                    }
                    if (data.error != null) {
                        editUserViewModel.error.postValue(data.error)
                        return@withContext false
                    }
                    return@withContext false
                } else {
                    Log.d(LOG_TAG,"MainModel - updateUserRemote  error: " + response.errorBody().toString())
                    editUserViewModel.error.postValue(response.errorBody().toString())
                    return@withContext false
                }
            }
            catch (e: Exception) {
                Log.d(LOG_TAG, "MainModel - updateUserRemote - Exception: $e")
                editUserViewModel.error.postValue(e.toString())
                return@withContext false
            }
        }

    private suspend fun loadRemoteUser(): Data {
        val petRequest = service.getDataAsync()
        return try {
            val response = petRequest.await()
            if(response.isSuccessful) {
                val data = response.body()!!
                Log.d(LOG_TAG, "MainModel - loadRemoteUser  data: $data")
                data
            } else {
                Log.d(LOG_TAG,"MainModel - loadRemoteUser  error: " + response.errorBody().toString())
                Data(error = response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "MainModel - loadRemoteUser - Exception: $e")
            Data(error = e.toString())
        }
    }

    fun setViewModel(m: SelectViewModel) : MainModel {
        this.selectViewModel = m
        return this
    }

    fun setEditUserViewModel(m: EditUserViewModel) : MainModel {
        this.editUserViewModel = m
        return this
    }

    fun setProfileViewModel(m: ProfileViewModel) : MainModel {
        this.profileViewModel = m
        return this
    }

    fun cancelBackgroundWork() {
        coroutineContext.cancelChildren()
        Log.d(LOG_TAG, "MainModel - cancelBackgroundWork")
    }

}