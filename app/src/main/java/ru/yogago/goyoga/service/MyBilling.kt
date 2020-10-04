package ru.yogago.goyoga.service

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import kotlin.coroutines.CoroutineContext

class MyBilling(val activity: Activity): CoroutineScope {

    private lateinit var skuDetails: List<SkuDetails>
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
        }

    private var billingClient = BillingClient.newBuilder(activity.applicationContext)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private fun startConnect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d(LOG_TAG, "MyBilling - startConnect - BillingClientStateListener - onBillingSetupFinished")
                Log.d(LOG_TAG, "MyBilling - startConnect - BillingClientStateListener - billingResult.responseCode: ${billingResult.responseCode}")
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    launch {
                        querySkuDetails()
                        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails[0])
                            .build()
                        val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode
                    }

                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d(LOG_TAG, "MyBilling - startConnect - BillingClientStateListener - onBillingServiceDisconnected")
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private suspend fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add("remove_ads")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        val skuDetailsResult: SkuDetailsResult = withContext(coroutineContext) {
            billingClient.querySkuDetails(params.build())
        }
        // Process the result.
        Log.d(LOG_TAG, "MyBilling - querySkuDetails - skuDetailsResult.skuDetailsList: ${skuDetailsResult.skuDetailsList}")
        skuDetails = skuDetailsResult.skuDetailsList!!
    }

    fun launchBilling(){
        Log.d(LOG_TAG, "MyBilling - launchBilling")

        startConnect()

    }

}