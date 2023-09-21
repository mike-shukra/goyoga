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

class MainModel: CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val dbDao = DataBase.db.getDBDao()
    private lateinit var selectViewModel: SelectViewModel
    private lateinit var profileViewModel: ProfileViewModel

    fun loadData() {
        launch {
            val settings = dbDao.getSettings()
            val asanas: List<Asana> = dbDao.getAsanas()
            val userData: UserData = dbDao.getUserData()
            userData.allTime = (userData.allTime * settings?.proportionately!!).toInt() + (settings.addTime * userData.allCount)
            selectViewModel.userData.postValue(userData)
            selectViewModel.asanas.postValue(asanas.filter { asana ->
                asana.side != "first"
            })
        }
    }

    fun deleteUserData(){
        launch {
            val responseDeleteUserData = dbDao.deleteUserData()
            Log.d(LOG_TAG, "MainModel - deleteTokenAndUserData responseDeleteUserData: $responseDeleteUserData")
        }
    }

    fun create(level: Long, knee: Boolean, loins: Boolean, neck: Boolean, inverted: Boolean) {
        val parametersDTO = ParametersDTO(
            now = 1,
            allTime = 0,
            allCount = 0,
            level = Level.values()[level.toInt()].toString(),
            dangerKnee = knee,
            dangerLoins = loins,
            dangerNeck = neck,
            inverted = inverted
        )
        val call = ApiFactory.API.createAsync(TokenProvider.firebaseToken!!, parametersDTO)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                if (response.isSuccessful) {
                    val data = response.body()!!
                    launch {
                        val del = dbDao.deleteAsanas()
                        val insA = dbDao.insertAsanas(data.asanas!!)
                        val insS = dbDao.insertSettings(data.settings!!)
                        val insD = dbDao.insertUserData(data.userData!!)
                        Log.d(LOG_TAG, "MainModel - updateDB del: $del, insA: $insA, insS: $insS, idsD: $insD")
                        profileViewModel.done.postValue(true)
                    }
                } else {
                    val errorMessage = response.message()
                    Log.d(LOG_TAG, "MainModel - create - message error: $errorMessage")
                    profileViewModel.error.postValue(errorMessage)
                }
            }
            override fun onFailure(call: Call<Data>, t: Throwable) {
                // Request failed due to a network error or other issues
                // Handle the failure
            }
        })

    }

    fun loadUserData() {
            val call = ApiFactory.API.getDataAsync(TokenProvider.firebaseToken!!)
            call.enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {
                        val data = response.body()!!
                        launch {
                            val settings = dbDao.getSettings()
                            if (settings!!.timeOfFiltered < data.settings!!.timeOfFiltered) {
                                dbDao.deleteAsanas()
                                dbDao.insertAsanas(data.asanas!!)
                            }
                            dbDao.deleteUserData()
                            dbDao.deleteSettings()
                            dbDao.insertSettings(data.settings!!)
                            dbDao.insertUserData(data.userData!!)
                            profileViewModel.proportionately.postValue(data.settings!!.proportionately)
                            profileViewModel.addTime.postValue(data.settings!!.addTime)
                            profileViewModel.userData.postValue(data.userData!!)
                        }
                    } else {
                        val errorMessage = response.message()
                        Log.d(LOG_TAG, "MainModel - getRemoteData - message error: $errorMessage")
                        profileViewModel.error.postValue(errorMessage)
                    }
                }
                override fun onFailure(call: Call<Data>, t: Throwable) {
                    // Request failed due to a network error or other issues
                    // Handle the failure
                    profileViewModel.error.postValue(t.message)
                }
            })
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
        }
    }

    fun updateSettingsProportionately(value: Float) {
        launch {
            dbDao.updateSettingsProportionately(value)
        }
    }

}