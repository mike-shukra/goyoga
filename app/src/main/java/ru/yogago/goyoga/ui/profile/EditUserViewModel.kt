package ru.yogago.goyoga.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.yogago.goyoga.R
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.model.ProfileModel
import ru.yogago.goyoga.ui.login.LoginFormState

class EditUserViewModel(application: Application) : AndroidViewModel(application) {

    private val model = ProfileModel()
    val passwordFormState: MutableLiveData<LoginFormState> = MutableLiveData()
    val nameFormState: MutableLiveData<LoginFormState> = MutableLiveData()
    val user: MutableLiveData<UserData> = MutableLiveData()
    val isUpdate: MutableLiveData<Boolean> = MutableLiveData()

    fun setUserToVM(user: UserData?) {
        this.user.postValue(user)
    }

    fun updateUserName(userData: UserData) {
        model.updateUserName(userData)
    }

    fun setModel(): EditUserViewModel {
        model.setEditUserViewModel(this)
        return this
    }

    fun loadUser() {
        model.loadUserToEditUser()
    }

    private fun isNameValid(password: String): Boolean {
        return password.length > 3
    }

    fun nameDataChanged(name: String) {
        if (!isNameValid(name)) {
            nameFormState.value =
                LoginFormState(usernameError = R.string.invalid_name)
        } else {
            nameFormState.value =
                LoginFormState(isDataValid = true)
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordReplayValid(password: String, passwordReplay: String): Boolean {
        val compare = password.compareTo(passwordReplay)
        return compare == 0
    }

    fun passwordDataChanged(password: String, passwordReplay: String) {
        if (!isPasswordValid(password)) {
            passwordFormState.value =
                LoginFormState(passwordError = R.string.invalid_password)
        } else if (!isPasswordReplayValid(password, passwordReplay)) {
            passwordFormState.value =
                LoginFormState(passwordReplayError = R.string.invalid_password_replay)
        } else {
            passwordFormState.value =
                LoginFormState(isDataValid = true)
        }
    }

    fun updatePassword(password: String) {
        model.updatePassword(password)
    }
}