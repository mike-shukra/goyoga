package ru.yogago.goyoga.ui.action

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.yogago.goyoga.data.Asana
import ru.yogago.goyoga.service.DataBase
import kotlin.coroutines.CoroutineContext

class PageViewModel : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    val asana: MutableLiveData<Asana> = MutableLiveData()
    val isPlay: MutableLiveData<Boolean> = MutableLiveData()
    private val dbDao = DataBase.db.getDBDao()

    fun loadData(id: Int) = launch {
        val settings = dbDao.getSettings()
        val proportionately = settings?.proportionately!!.toInt()
        val addTime = settings.addTime
        val item = dbDao.getAsana(id)
        item.times = item.times * proportionately + addTime
        asana.postValue(item)

        val actionState = dbDao.getActionState()
        isPlay.postValue(actionState.isPlay)

    }

}