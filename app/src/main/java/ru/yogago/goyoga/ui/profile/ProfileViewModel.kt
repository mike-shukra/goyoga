package ru.yogago.goyoga.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.Purchase
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG_BILLING
import ru.yogago.goyoga.model.MainModel
import ru.yogago.goyoga.model.MyBilling

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val model = MainModel()
    val userData: MutableLiveData<UserData> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val done: MutableLiveData<Boolean> = MutableLiveData()
    val proportionately: MutableLiveData<Float> = MutableLiveData()
    val addTime: MutableLiveData<Int> = MutableLiveData()
    private lateinit var myBilling: MyBilling

    fun setModel(): ViewModel {
        model.setProfileViewModel(this)
        return this
    }

    fun setMyBilling(mB: MyBilling) {
        myBilling = mB
    }

    fun loadUserData() {
        model.loadUserData()
    }

//    viewModelScope.launch {
//        liveData.postValue("Новое значение")
//    }

    fun deleteUserData() {
        model.deleteUserData()
    }

    fun handleBilling() {
        val onSuccess: (List<Purchase>) -> Unit = { purchases ->
            Log.d(LOG_TAG_BILLING, "ProfileViewModel - MyBilling - handleBilling - purchasesList: $purchases")
            if (purchases.isEmpty()) {
                BillingState.getSubscribesList().forEach {
                    BillingState.setFlagByString(it, true)
                }
            }
            else {
                purchases.forEach {
                    myBilling.acknowledgedPurchase(it)
                    Log.d(LOG_TAG_BILLING, "ProfileViewModel - purchase.sku: ${it.sku}")
                    Log.d(LOG_TAG_BILLING, "ProfileViewModel - purchase.purchaseState: ${it.purchaseState}")
                    Log.d(LOG_TAG_BILLING, "ProfileViewModel - purchase.isAcknowledged: ${it.isAcknowledged}")

                    if ((it.purchaseState == Purchase.PurchaseState.PURCHASED) && (it.isAcknowledged)) {
                        BillingState.setFlagByString(it.sku, false)
                    }
                    else {
                        BillingState.setFlagByString(it.sku, true)
                    }
                }
                checkContain(purchases)
            }
            Log.d(LOG_TAG_BILLING, "ProfileViewModel - BillingState.isAds: ${BillingState.isAds.value}")
            Log.d(LOG_TAG_BILLING, "ProfileViewModel - BillingState.isJustPay: ${BillingState.isJustPay.value}")
        }
        val onError: (message: String) -> Unit = {message: String ->
            Log.d(LOG_TAG_BILLING, "ProfileViewModel - handleBilling - onError - message: $message")
        }
        myBilling.queryPurchases(onSuccess, onError)
    }

    private fun checkContain(purchases: List<Purchase>) {
        val noContains = arrayListOf<String>()
        val subscribesList = BillingState.getSubscribesList()
        val purchaseStrings = arrayListOf<String>()
        purchases.forEach {
            purchaseStrings.add(it.sku)
        }
        subscribesList.forEach {
            if (!purchaseStrings.contains(it)) {
                noContains.add(it)
            }
        }
        noContains.forEach {
            BillingState.setFlagByString(it, true)
            Log.d(LOG_TAG_BILLING, "ProfileViewModel - checkContain noContain: $it")
        }
    }

    fun create(level: Long, knee: Boolean, loins: Boolean, neck: Boolean, inverted: Boolean) {
        model.create(
            level = level,
            knee = knee,
            loins = loins,
            neck = neck,
            inverted = inverted
        )
    }

    fun destroyBilling(){
        myBilling.destroy()
    }

    fun updateSettingsAddTime(value: Int) {
        model.updateSettingsAddTime(value)
    }

    fun updateSettingsProportionately(value: Float) {
        model.updateSettingsProportionately(value)
    }
}