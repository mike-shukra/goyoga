package ru.yogago.goyoga.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.Purchase
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.data.UserData
import ru.yogago.goyoga.model.MainModel
import ru.yogago.goyoga.model.MyBilling

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val model = MainModel()
    val user: MutableLiveData<UserData> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val done: MutableLiveData<Boolean> = MutableLiveData()
    val isAds: MutableLiveData<Boolean> = MutableLiveData()
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

    fun handleBilling() {
        val onSuccess: (List<Purchase>) -> Unit = { purchases ->
            Log.d(AppConstants.LOG_TAG_BILLING, "ProfileViewModel - MyBilling - handleBilling - purchasesList: $purchases")
            if (purchases.isEmpty()) {
                BillingState.isAds = true
                isAds.postValue(true)
            }
            else {
                purchases.forEach {
                    myBilling.acknowledgedPurchase(it)
                    val test = ((it.sku == "remove_ads") && (it.purchaseState == Purchase.PurchaseState.PURCHASED) && (it.isAcknowledged))
                    Log.d(AppConstants.LOG_TAG_BILLING, "ProfileViewModel - test: $test")
                    Log.d(AppConstants.LOG_TAG_BILLING, "ProfileViewModel - test: it.sku: ${it.sku}")
                    Log.d(AppConstants.LOG_TAG_BILLING, "ProfileViewModel - test: it.isAcknowledged: ${it.isAcknowledged}")

                    if ((it.sku == "remove_ads") && (it.purchaseState == Purchase.PurchaseState.PURCHASED) && (it.isAcknowledged)) {
                        BillingState.isAds = false
                        isAds.postValue(BillingState.isAds)
                    }
                    else {
                        BillingState.isAds = true
                        isAds.postValue(BillingState.isAds)
                    }
                }
            }
            Log.d(AppConstants.LOG_TAG_BILLING, "ProfileViewModel - BillingState.isAds: ${BillingState.isAds}")

        }
        val onError: (message: String) -> Unit = {message: String ->
            Log.d(AppConstants.LOG_TAG_BILLING, "ProfileViewModel - handleBilling - onError - message: $message")
        }
        myBilling.queryPurchases(onSuccess, onError)
    }

    fun create(level: String, knee: Boolean, loins: Boolean, neck: Boolean) {
        model.create(
            level = level,
            knee = if (knee) 1.toString() else 0.toString(),
            loins = if (loins) 1.toString() else 0.toString(),
            neck = if (neck) 1.toString() else 0.toString()
        )
    }

    fun destroyBilling(){
        myBilling.destroy()
    }
}