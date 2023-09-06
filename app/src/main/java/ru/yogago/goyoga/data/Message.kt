package ru.yogago.goyoga.data

data class Message (
    var message: String? = null,
    var result: Boolean? = null,
    var image_url: String? = null,
    var error: String? = null,
    var errorKey: Int? = null
)