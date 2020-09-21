package ru.yogago.goyoga.data

data class Message (
    val message: String? = null,
    val image_url: String? = null,
    val error: String? = null,
    val errorKey: Int? = null
)