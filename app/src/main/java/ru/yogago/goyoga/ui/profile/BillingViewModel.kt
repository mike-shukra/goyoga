package ru.yogago.goyoga.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.yogago.goyoga.data.BillingItem
import ru.yogago.goyoga.model.MyBilling
import kotlin.coroutines.CoroutineContext

class BillingViewModel : ViewModel(), CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    val billings: MutableLiveData<ArrayList<BillingItem>> = MutableLiveData()
    private lateinit var myBilling: MyBilling

    fun loadBillings() {
        myBilling.loadBillingById()
    }

    fun setMyBilling(mB: MyBilling){
        myBilling = mB
        myBilling.setViewModel(this)
    }

    fun subscribe(id: Int) {
        myBilling.subscribe(id)
    }
}