package ru.yogago.goyoga.data

data class BillingItem (
    val sku: String,
    val title: String,
    val type: String,
    val price: String,
    val price_currency_code: String,
    val subscriptionPeriod: String,
    val description: String,
    val activated: Boolean = false
)