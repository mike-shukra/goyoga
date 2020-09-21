package ru.yogago.goyoga.service.login

import android.util.Log
import ru.yogago.goyoga.data.AppConstants.LOG_TAG
import ru.yogago.goyoga.data.LoggedInUser
import ru.yogago.goyoga.service.TokenProvider

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val token = TokenProvider.getToken(username, password)
            if (token.error == null) {
                return Result.Success(
                    LoggedInUser(
                        token.userId!!,
                        token.first_name!!,
                        token
                    )
                )
            }
            Log.d(LOG_TAG,"LoginDataSource - getToken error: " + TokenProvider.token.error)
            return Result.Error(
                TokenProvider.token.error.toString()
            )
        } catch (e: Throwable) {
            return Result.Error(
                e.toString()
            )
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}