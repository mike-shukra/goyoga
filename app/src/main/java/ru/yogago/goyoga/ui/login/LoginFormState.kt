package ru.yogago.goyoga.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val loginError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val passwordReplayError: Int? = null,
    val phoneError: Int? = null,
    val isDataValid: Boolean = false
)