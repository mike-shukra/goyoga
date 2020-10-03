package ru.yogago.goyoga.model

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import kotlin.coroutines.CoroutineContext

class MyBilling(val context: Context, private val activity: Activity): CoroutineScope {

    private var skuDetails: SkuDetails? = null
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    fun startConnect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    querySkuDetails()
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
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
            skuDetails = skuDetailsResult.skuDetailsList?.get(0)
            Log.d(LOG_TAG, "MyBilling - querySkuDetails - skuDetailsResult.skuDetailsList: ${skuDetailsResult.skuDetailsList}")
        }
    }

    fun launchBilling(){
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails!!)
            .build()
        val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode
    }

}