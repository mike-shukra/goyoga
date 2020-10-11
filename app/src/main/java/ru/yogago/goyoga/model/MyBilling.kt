package ru.yogago.goyoga.model

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG_BILLING
import ru.yogago.goyoga.data.BillingState
import ru.yogago.goyoga.data.JUST_PAY
import ru.yogago.goyoga.data.REMOVE_ADS
import kotlin.coroutines.CoroutineContext

class MyBilling(private val activity: Activity): CoroutineScope, PurchasesUpdatedListener {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private var isBillingServiceConnected = false

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult: BillingResult, purchases ->
        // To be implemented in a later section.
        Log.d(LOG_TAG_BILLING, "MyBilling - purchasesUpdatedListener - billingResult.debugMessage: $billingResult")
        Log.d(LOG_TAG_BILLING, "MyBilling - purchasesUpdatedListener - purchases: $purchases")
        purchases?.forEach {
            acknowledgedPurchase(it)
        }
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
                    Log.d(LOG_TAG_BILLING, "MyBilling - startConnect - BillingClientStateListener - onBillingSetupFinished - billingResult.responseCode: ${billingResult.responseCode}")
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
                    Log.d(LOG_TAG_BILLING, "MyBilling - startConnect - BillingClientStateListener - onBillingServiceDisconnected")
                }
            })
        }
    }

    private fun isSubscriptionPurchaseSupported(): Boolean {
        val response = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (response.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.d(LOG_TAG_BILLING, "isSubscriptionPurchaseSupported(), response.responseCode: ${response.responseCode}")
            Log.d(LOG_TAG_BILLING, "isSubscriptionPurchaseSupported(), not supported, error response: $response")
        }
        return response.responseCode == BillingClient.BillingResponseCode.OK
    }

    private fun startQueryPurchases(
        onSuccess: (List<Purchase>) -> Unit,
        onError: (message: String) -> Unit
    ) {
        if (isSubscriptionPurchaseSupported()) {
            val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
            if (purchasesResult.responseCode == BillingClient.BillingResponseCode.OK) {
                onSuccess(purchasesResult.purchasesList!!)
                Log.d(LOG_TAG_BILLING, "MyBilling - startQueryPurchases - purchasesResult.purchasesList: ${purchasesResult.purchasesList}")
            } else {
                Log.d(LOG_TAG_BILLING, "Error trying to query purchases: $purchasesResult")
                onError(purchasesResult.toString())
            }
        } else {
            onSuccess(emptyList())
        }
    }


    private fun subscriptionSkuDetails(skus: List<String>, onSuccess: (List<SkuDetails>) -> Unit, onError: (code: Int, message: String) -> Unit) {
        val params = SkuDetailsParams.newBuilder().setSkusList(skus).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                onSuccess(skuDetailsList)
            } else {
                onError(billingResult.responseCode, billingResult.debugMessage)
            }
        }
    }

    fun subscriptionSkuDetails(onSuccess: (List<SkuDetails>) -> Unit, onError: (code: Int, message: String) -> Unit) {
        startConnect {
            subscriptionSkuDetails(listOf(JUST_PAY, REMOVE_ADS), onSuccess, onError)
        }
    }

    fun subscribe(sku: SkuDetails): Int{
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(sku)
            .build()
        val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode
        Log.d(LOG_TAG_BILLING, "MyBilling - subscribe - responseCode: $responseCode")
        return responseCode
    }

    fun queryPurchases(
        onSuccess: (List<Purchase>) -> Unit,
        onError: (message: String) -> Unit
    ) {
        Log.d(LOG_TAG_BILLING, "MyBilling - queryPurchases")
        startConnect {
            startQueryPurchases(onSuccess, onError)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        Log.d(LOG_TAG_BILLING, "MyBilling - onPurchasesUpdated - billingResult: $billingResult")
        Log.d(LOG_TAG_BILLING, "MyBilling - onPurchasesUpdated - purchases: $purchases")

        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d(LOG_TAG_BILLING, "MyBilling - onPurchasesUpdated - Handle an error caused by a user cancelling the purchase flow.")
        } else {
            // Handle any other error codes.
            Log.d(LOG_TAG_BILLING, "MyBilling - onPurchasesUpdated - Handle any other error codes.")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        Log.d(LOG_TAG_BILLING, "MyBilling - handlePurchase")
        // Purchase retrieved from BillingClient#queryPurchases or your PurchasesUpdatedListener.
        val myPurchase : Purchase = purchase

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(myPurchase.purchaseToken)
                .build()
        launch {
            billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    acknowledgedPurchase(myPurchase)
                }
                Log.d(LOG_TAG_BILLING, "MyBilling - handlePurchase - outToken: $outToken")
                Log.d(LOG_TAG_BILLING, "MyBilling - handlePurchase - billingResult: $billingResult")
            }
        }

    }

    fun acknowledgedPurchase(purchase: Purchase) {
        launch {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                    val ackPurchaseResult = withContext(Dispatchers.IO) {
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                    }
                    Log.d(LOG_TAG_BILLING,"MyBilling - acknowledgedPurchase - ackPurchaseResult: $ackPurchaseResult")
                    BillingState.setFlagByString(purchase.sku, false)
                }
            }
        }
    }

    fun destroy() {
        Log.d(LOG_TAG_BILLING,"MyBilling - destroy()")
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }


}