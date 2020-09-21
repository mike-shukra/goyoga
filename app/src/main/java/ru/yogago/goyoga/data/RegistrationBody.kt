package ru.yogago.goyoga.data

data class RegistrationBody (
    val password: String,
    val email: String,
    val first_name: String = "default"
)