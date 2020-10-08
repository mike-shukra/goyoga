package ru.yogago.goyoga.data

data class BillingItem (
    val type: String,
    val price: String,
    val price_currency_code: String,
    val subscriptionPeriod: String,
    val title: String,
    val description: String
)