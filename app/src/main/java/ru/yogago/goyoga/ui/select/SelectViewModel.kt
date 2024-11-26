package ru.yogago.goyoga.ui.select

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.model.MainModel
import javax.inject.Inject

@HiltViewModel
class SelectViewModel @Inject constructor(
    private val model: MainModel
) : ViewModel() {

    val text: MutableLiveData<String> = MutableLiveData()
    val asanas: MutableLiveData<List<Asana>> = MutableLiveData()
    val userData: MutableLiveData<UserData> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun setModel(): SelectViewModel {
        model.setSelectViewModel(this)
        return this
    }

    fun loadAsanas() {
        model.loadDataFromDB()
    }

}