package ru.yogago.goyoga.data

data class BillingItem (
    var sku: String,
    var title: String,
    var type: String,
    var price: String,
    var price_currency_code: String,
    var subscriptionPeriod: String,
    var description: String,
    var activated: Boolean = false
)