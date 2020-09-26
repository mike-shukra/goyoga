package ru.yogago.goyoga.data

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: Long,
    val token: Token
)