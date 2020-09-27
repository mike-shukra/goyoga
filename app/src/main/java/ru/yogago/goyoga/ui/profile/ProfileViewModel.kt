package ru.yogago.goyoga.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.cancelChildren
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.model.ProfileModel

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val model = ProfileModel()
    val user: MutableLiveData<UserData> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun setModel(): ViewModel {
        model.setProfileViewModel(this)
        return this
    }

    fun loadUserData() {
        model.loadUserToProfile()
    }

    fun create(level: String, knee: Boolean, loins: Boolean, neck: Boolean) {
        model.create(
            level = level,
            knee = if (knee) 1.toString() else 0.toString(),
            loins = if (loins) 1.toString() else 0.toString(),
            neck = if (neck) 1.toString() else 0.toString()
        )
    }

    fun cancelBackgroundWork() {
        model.cancelBackgroundWork()
        Log.d(AppConstants.LOG_TAG, "ActionViewModel - cancelBackgroundWork")
    }

}