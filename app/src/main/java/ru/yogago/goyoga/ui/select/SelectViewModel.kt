package ru.yogago.goyoga.ui.select

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.model.MainModel

class SelectViewModel : ViewModel() {

    private val model = MainModel()
    val text: MutableLiveData<String> = MutableLiveData()
    val asanas: MutableLiveData<List<Asana>> = MutableLiveData()
    val userData: MutableLiveData<UserData> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun setModel(): SelectViewModel {
        model.setViewModel(this)
        return this
    }

    fun loadAsanas() {
        model.loadData()
    }

    fun cancelBackgroundWork() {
        model.cancelBackgroundWork()
        Log.d(AppConstants.LOG_TAG, "SelectViewModel - cancelBackgroundWork")
    }
}