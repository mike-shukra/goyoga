package ru.yogago.goyoga.model

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.BillingItem
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.ui.profile.BillingViewModel
import kotlin.coroutines.CoroutineContext

class MyBilling(val activity: Activity): CoroutineScope, PurchasesUpdatedListener {

    companion object {
        var myBilling: MyBilling? = null
        fun newInstance(activity: Activity?): MyBilling{
            if (myBilling == null) myBilling = MyBilling(activity!!)
            return myBilling!!
        }
    }

    private lateinit var skuDetails: List<SkuDetails>
    private lateinit var viewModel: BillingViewModel
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private var isBillingServiceConnected = false

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult: BillingResult, purchases ->
            // To be implemented in a later section.
            Log.d(LOG_TAG, "MyBilling - purchasesUpdatedListener - billingResult.debugMessage: ${billingResult.debugMessage}")
            Log.d(LOG_TAG, "MyBilling - purchasesUpdatedListener - purchases: $purchases")
        }

    private var billingClient = BillingClient.newBuilder(activity.applicationContext)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private fun startConnect(task: () -> Unit) {
        if (isBillingServiceConnected) {
            task()
        } else {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    Log.d(LOG_TAG, "MyBilling - startConnect - BillingClientStateListener - onBillingSetupFinished - billingResult.responseCode: ${billingResult.responseCode}")
                    if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                        isBillingServiceConnected = true
                        task()
                    }
                }
                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    isBillingServiceConnected = false
                    Log.d(LOG_TAG, "MyBilling - startConnect - BillingClientStateListener - onBillingServiceDisconnected")
                }
            })
        }
    }

    private fun isSubscriptionPurchaseSupported(): Boolean {
        val response = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (response.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.d(LOG_TAG, "isSubscriptionPurchaseSupported(), response.responseCode: ${response.responseCode}")
            Log.d(LOG_TAG, "isSubscriptionPurchaseSupported(), not supported, error response: $response")
        }
        return response.responseCode == BillingClient.BillingResponseCode.OK
    }

    private fun startQueryPurchases() {
        if (isSubscriptionPurchaseSupported()) {
            val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
            if (purchasesResult.responseCode == BillingClient.BillingResponseCode.OK) {
                onEntitledPurchases(purchasesResult.purchasesList!!)
            } else {
                Log.d(LOG_TAG, "Error trying to query purchases: $purchasesResult")
            }
        } else {
            onEntitledPurchases(emptyList())
        }
    }

    private fun onEntitledPurchases(purchasesList: List<Purchase>) {
        Log.d(LOG_TAG, "MyBilling - onEntitledPurchases - purchasesList: $purchasesList")
        if (purchasesList.isNotEmpty()) BillingState.isAds = true
        Log.d(LOG_TAG, "BillingState.isAds: ${BillingState.isAds}")

    }

    private fun querySkuDetails() {
        launch {
            val skuList = ArrayList<String>()
            skuList.add("remove_ads")
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
            val skuDetailsResult: SkuDetailsResult = withContext(coroutineContext) {
                billingClient.querySkuDetails(params.build())
            }
            // Process the result.
            Log.d(LOG_TAG, "MyBilling - querySkuDetails - skuDetailsResult.skuDetailsList: ${skuDetailsResult.skuDetailsList}")
            val billings = ArrayList<BillingItem>()
            skuDetails = skuDetailsResult.skuDetailsList!!
            skuDetails.forEach {
                billings.add(BillingItem(
                    type = it.type,
                    price = it.price,
                    price_currency_code = it.priceCurrencyCode,
                    subscriptionPeriod = it.subscriptionPeriod,
                    title = it.title,
                    description = it.description
                ))
            }
            viewModel.billings.postValue(billings)
        }
    }

    fun subscribe(id: Int){
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails[id])
            .build()
        val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode
        Log.d(LOG_TAG, "MyBilling - subscribe - responseCode: $responseCode")
    }

    fun loadBillings(){
        Log.d(LOG_TAG, "MyBilling - launchBilling")
        startConnect {
            querySkuDetails()
        }
    }

    fun queryPurchases() {
        startConnect {
            startQueryPurchases()
        }
    }

    fun setViewModel(vm: BillingViewModel) {
        viewModel = vm
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Purchase retrieved from BillingClient#queryPurchases or your PurchasesUpdatedListener.
        val purchase : Purchase = purchase

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
                BillingState.isAds = false
            }
        }
    }

}