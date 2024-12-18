package ru.yogago.goyoga.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.SkuDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG_BILLING
import ru.yogago.goyoga.data.BillingItem
import ru.yogago.goyoga.model.MyBilling
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class BillingViewModel @Inject constructor(
    private var myBilling: MyBilling
) : ViewModel(), CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    val billings: MutableLiveData<ArrayList<BillingItem>> = MutableLiveData()

    private lateinit var skus: List<SkuDetails>

    fun loadBillings() {
        val billingItems = ArrayList<BillingItem>()
        val onSuccess: (List<SkuDetails>) -> Unit = { skus ->
            this.skus = skus
            Log.d(LOG_TAG_BILLING, "BillingViewModel - loadBillings - onSuccess - List<SkuDetails>: $skus")
            skus.forEach {
                billingItems.add(BillingItem(
                    sku = it.sku,
                    type = it.type,
                    price = it.price,
                    price_currency_code = it.priceCurrencyCode,
                    subscriptionPeriod = it.subscriptionPeriod,
                    title = it.title,
                    description = it.description
                ))
                Log.d(LOG_TAG_BILLING, "BillingViewModel - loadBillings - onSuccess - skus.title: ${it.sku}")

            }
            billings.postValue(billingItems)
        }
        val onError: (code: Int, message: String) -> Unit = { code: Int, message: String ->
            Log.d(LOG_TAG_BILLING, "BillingViewModel - loadBillings - onError - code: $code - message: $message")
        }
//        myBilling.subscriptionSkuDetails(onSuccess, onError)

    }

    fun setMyBilling(mB: MyBilling){
        myBilling = mB
    }

    fun subscribe(title: String) {
        skus.forEach {
            if (it.sku == title) {
//                val responseCode = myBilling.subscribe(it)
//                Log.d(LOG_TAG_BILLING, "BillingViewModel - subscribe it.sku: ${it.sku} responseCode: $responseCode")
            }
        }
    }

    fun destroyBilling(){
        myBilling.destroy()
    }

}