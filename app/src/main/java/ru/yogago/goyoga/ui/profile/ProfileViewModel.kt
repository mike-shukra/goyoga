package ru.yogago.goyoga.ui.profile

import android.app.Application
import androidx.lifecycle.*
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
}