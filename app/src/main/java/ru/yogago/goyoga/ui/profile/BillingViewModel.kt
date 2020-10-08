package ru.yogago.goyoga.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.BillingItem
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.model.MyBilling
import kotlin.coroutines.CoroutineContext

class BillingViewModel : ViewModel(), CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    val billings: MutableLiveData<ArrayList<BillingItem>> = MutableLiveData()
    private lateinit var myBilling: MyBilling
    private lateinit var skus: List<SkuDetails>

    fun loadBillings() {
        val billingItems = ArrayList<BillingItem>()
        val onSuccess: (List<SkuDetails>) -> Unit = { skus ->
            this.skus = skus
            Log.d(AppConstants.LOG_TAG_BILLING, "BillingViewModel - loadBillings - onSuccess - List<SkuDetails>: $skus")
            skus.forEach {
                billingItems.add(BillingItem(
                    type = it.type,
                    price = it.price,
                    price_currency_code = it.priceCurrencyCode,
                    subscriptionPeriod = it.subscriptionPeriod,
                    title = it.title,
                    description = it.description
                ))
            }
            billings.postValue(billingItems)
        }
        val onError: (code: Int, message: String) -> Unit = { code: Int, message: String ->
            Log.d(AppConstants.LOG_TAG_BILLING, "BillingViewModel - loadBillings - onError - code: $code - message: $message")
        }
        myBilling.querySubscriptionSkuDetails(onSuccess, onError)

    }

    fun setMyBilling(mB: MyBilling){
        myBilling = mB
    }

    fun subscribe(id: Int) {
        val responseCode = myBilling.subscribe(skus[id])
        if (responseCode == BillingClient.BillingResponseCode.OK) BillingState.isAds = false
    }

    fun destroyBilling(){
        myBilling.destroy()
    }

}