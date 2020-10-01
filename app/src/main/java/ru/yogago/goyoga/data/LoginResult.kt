package ru.yogago.goyoga.data

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: String? = null,
    val error: String? = null
)