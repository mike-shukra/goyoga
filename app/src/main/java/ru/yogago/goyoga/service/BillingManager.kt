package ru.yogago.goyoga.service


import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.FeatureType
import ru.yogago.goyoga.service.BillingManager.Companion.formatPeriod

/**
 * When using this class:
 * - Call `queryPurchases()` in your Activity's onResume() method
 * - Call `query*SubscriptionSkuDetails()` when you want to show your in-app products
 * - Call `startPurchaseFlow()` when one of your in-app products is clicked on
 * - Call `destroy()` in your Activity's onDestroy() method
 *
 * Good example: https://github.com/googlesamples/android-play-billing/blob/master/TrivialDrive_v2/shared-module/src/main/java/com/example/billingmodule/billing/BillingManager.java
 *
 * Note: More security can be added by using 'developer payloads', but that is not used here.
 */
class BillingManager(
    private val activity: Activity,
    private val onEntitledPurchases: (List<Purchase>) -> Unit,
    private val onPurchase: (Purchase) -> Unit
) {

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                purchases?.let {
                    for (purchase in purchases) {
                        when (purchase.purchaseState) {
                            Purchase.PurchaseState.PURCHASED -> {
                                onPurchase(purchase)
                                if (!purchase.isAcknowledged) {
                                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.purchaseToken)
                                        .build()
                                    billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                                        log("acknowledgePurchase(), billingResult=$billingResult")
                                    }
                                }
                            }
                            Purchase.PurchaseState.PENDING -> {
                                // Here you can confirm to the user that they've started the pending
                                // purchase, and to complete it, they should follow instructions that
                                // are given to them. You can also choose to remind the user in the
                                // future to complete the purchase if you detect that it is still
                                // pending.
                            }
                        }
                    }
                }
                log("onPurchasesUpdated(), $purchases")
            }
            BillingResponseCode.USER_CANCELED -> log("onPurchasesUpdated() - user cancelled the purchase flow - skipping")
            else -> log("onPurchasesUpdated() got unknown resultCode: ${billingResult.responseCode}")
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .enablePendingPurchases()
        .setListener(purchasesUpdatedListener)
        .build()

    private var isBillingServiceConnected = false

    init {
        startServiceConnection {
            queryPurchases()
        }
    }

    fun queryPurchases() {
        if (isSubscriptionPurchaseSupported()) {
            val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
            if (purchasesResult.responseCode == BillingResponseCode.OK) {
                onEntitledPurchases(purchasesResult.purchasesList!!)
            } else {
                log("Error trying to query purchases: $purchasesResult")
            }
        } else {
            onEntitledPurchases(emptyList())
        }
    }

    fun queryBusinessSubscriptionSkuDetails(onSuccess: (List<SkuDetails>) -> Unit, onError: (code: Int, message: String) -> Unit) {
        startServiceConnection {
            querySubscriptionSkuDetails(listOf(Sku.BUSINESS_MONTHLY, Sku.BUSINESS_YEARLY), onSuccess, onError)
        }
    }

    fun queryIndividualSubscriptionSkuDetails(onSuccess: (List<SkuDetails>) -> Unit, onError: (code: Int, message: String) -> Unit) {
        startServiceConnection {
            querySubscriptionSkuDetails(listOf(Sku.INDIVIDUAL_YEARLY), onSuccess, onError)
        }
    }

    fun startPurchaseFlow(sku: SkuDetails) {
        startServiceConnection {
            val flowParams = BillingFlowParams.newBuilder().setSkuDetails(sku).build()
            val billingResult = billingClient.launchBillingFlow(activity, flowParams)
            log("startPurchaseFlow(...), billingResult=$billingResult")
        }
    }

    fun destroy() {
        log("destroy()")
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    private fun startServiceConnection(task: () -> Unit) {
        if (isBillingServiceConnected) {
            task()
        } else {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    log("onBillingSetupFinished(...), billingResult=$billingResult")
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        isBillingServiceConnected = true
                        task()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    log("onBillingServiceDisconnected()")
                    isBillingServiceConnected = false
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })
        }
    }

    private fun querySubscriptionSkuDetails(skus: List<String>, onSuccess: (List<SkuDetails>) -> Unit, onError: (code: Int, message: String) -> Unit) {
        val params = SkuDetailsParams.newBuilder().setSkusList(skus).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingResponseCode.OK && skuDetailsList != null) {
                onSuccess(skuDetailsList)
            } else {
                onError(billingResult.responseCode, billingResult.debugMessage)
            }
        }
    }

    private fun isSubscriptionPurchaseSupported(): Boolean {
        val response = billingClient.isFeatureSupported(FeatureType.SUBSCRIPTIONS)
        if (response.responseCode != BillingResponseCode.OK) {
            log("isSubscriptionPurchaseSupported(), not supported, error response: $response")
        }
        return response.responseCode == BillingResponseCode.OK
    }

    private fun log(message: String) {
        Log.d("BillingManager", message)
    }

    companion object {

        /**
         * P1W equates to one week,
         * P1M equates to one month,
         * P3M equates to three months,
         * P6M equates to six months,
         * P1Y equates to one year
         */
        fun formatPeriod(period: String, isIncludeSingularNumber: Boolean): String {
            if (period.count() < 3) return ""
            val isSingular = period[1] == '1'
            val unit = when (period[2]) {
                'W' -> if (isSingular) "week" else "weeks"
                'M' -> if (isSingular) "month" else "months"
                'Y' -> if (isSingular) "year" else "years"
                else -> ""
            }
            return if (isSingular && !isIncludeSingularNumber) unit else "${period[1]} $unit"
        }

    }

    /** The format of SKUs must start with number or lowercase letter and can contain only numbers (0-9),
     * lowercase letters (a-z), underscores (_) & periods (.).*/
    object Sku {
        const val BUSINESS_MONTHLY = "business_monthly2" // Made a mistake with "business_monthly", accidentally make it yearly, so we can't use that SKU anymore
        const val BUSINESS_YEARLY = "business_yearly"
        const val INDIVIDUAL_YEARLY = "individual_yearly"

        // Testing
//        const val TEST_PURCHASED = "android.test.purchased"
//        const val TEST_CANCELED = "android.test.canceled"
//        const val TEST_UNAVAILABLE = "android.test.item_unavailable"
    }

}

fun SkuDetails.displayPeriod() = formatPeriod(this.subscriptionPeriod, isIncludeSingularNumber = false)
fun SkuDetails.displayIntroductoryPeriod() = formatPeriod(this.introductoryPricePeriod, isIncludeSingularNumber = true)