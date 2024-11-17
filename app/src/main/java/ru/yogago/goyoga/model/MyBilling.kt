package ru.yogago.goyoga.model

import android.app.Activity
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyBilling @Inject constructor(
    private val billingClient: BillingClient
) : CoroutineScope, PurchasesUpdatedListener {

    private var job: Job = Job()
    override val coroutineContext = Dispatchers.Main + job

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Настройка завершена, можно делать запросы на покупки
                    launch {
                        queryPurchasesAsync()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Если соединение с сервисом Google Play разорвано, можно попытаться переподключиться
            }
        })
    }

    private suspend fun queryPurchasesAsync() {
        // Запрос уже совершенных покупок
        val result = billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP)
        handlePurchases(result.purchasesList)
    }

    private fun handlePurchases(purchases: List<Purchase>?) {
        // Обработка покупок
        purchases?.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                // Пожизненная покупка или подписка
                acknowledgePurchase(purchase)
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        // Подтверждение покупки (для предотвращения повторных запросов)
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Покупка подтверждена
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            handlePurchases(purchases)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Пользователь отменил покупку
        } else {
            // Обработка других ошибок
        }
    }

    fun destroy() {
        billingClient.endConnection()
    }

    fun initiatePurchase(activity: Activity, sku: String) {
        val skuList = listOf(sku)
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {
                val skuDetails = skuDetailsList[0]
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build()
                val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode
                if (responseCode != BillingClient.BillingResponseCode.OK) {
                    // Обработка ошибки при запуске покупки
                }
            }
        }
    }
}