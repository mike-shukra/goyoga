package ru.yogago.goyoga.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.Purchase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.yogago.goyoga.data.*
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG_BILLING
import ru.yogago.goyoga.model.MainModel
import ru.yogago.goyoga.model.MyBilling
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val model: MainModel
) : ViewModel() {

    val navigationFlow: SharedFlow<Unit> = model.navigationFlow
    val userData: MutableLiveData<UserData> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
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
        model.createUserOnServerIfNotExist()
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
//                    myBilling.acknowledgedPurchase(it)
                    Log.d(LOG_TAG_BILLING, "ProfileViewModel - purchase.sku: ${it.skus}")
                    Log.d(LOG_TAG_BILLING, "ProfileViewModel - purchase.purchaseState: ${it.purchaseState}")
                    Log.d(LOG_TAG_BILLING, "ProfileViewModel - purchase.isAcknowledged: ${it.isAcknowledged}")

                    if ((it.purchaseState == Purchase.PurchaseState.PURCHASED) && (it.isAcknowledged)) {
                        BillingState.setFlagByString("it.sku", false)
                    }
                    else {
                        BillingState.setFlagByString("it.sku", true)
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
//        myBilling.queryPurchases(onSuccess, onError)
    }

    private fun checkContain(purchases: List<Purchase>) {
        val noContains = arrayListOf<String>()
        val subscribesList = BillingState.getSubscribesList()
        val purchaseStrings = arrayListOf<String>()
        purchases.forEach {
            purchaseStrings.add("it.sku")
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

    fun create(level: Long, seekBarProportionally: Float, seekBarAddTime: Int, knee: Boolean, loins: Boolean, neck: Boolean, inverted: Boolean, sideBySideSort: Boolean) {
        model.create(ParametersDTO(
            now = 1,
            allTime = 0,
            allCount = 0,
            level = Level.values()[level.toInt()].toString(),
            proportionally = seekBarProportionally,
            addTime = seekBarAddTime,
            dangerKnee = knee,
            dangerLoins = loins,
            dangerNeck = neck,
            inverted = inverted,
            sideBySideSort = sideBySideSort,
            System.currentTimeMillis())
        )
    }

    fun destroyBilling(){
        //TODO
//        myBilling.destroy()
    }

//    fun updateSettingsAddTime(value: Int) {
//        model.updateSettingsAddTime(value)
//    }
//
//    fun updateSettingsProportionately(value: Float) {
//        model.updateSettingsProportionately(value)
//    }
//    fun updateSettingsHowToSort(value: Boolean) {
//        model.updateSettingsHowToSort(value)
//    }
}