package ru.yogago.goyoga.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.model.MainModel

class SelectViewModel : ViewModel() {

    private val model = MainModel()
    val text: MutableLiveData<String> = MutableLiveData()
    val asanas: MutableLiveData<List<Asana>> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun setModel(): SelectViewModel {
        model.setViewModel(this)
        return this
    }

    fun loadAsanas() {
        model.loadData()
    }

}