package ru.yogago.goyoga.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.yogago.goyoga.data.Token
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object TokenProvider {

    private const val LOG_TAG: String = "myLog"
    lateinit var token: Token
    lateinit var error: String
    var cookieString: String = "false"

    suspend fun getToken(login: String, password: String) : Token{
        return suspendCoroutine {
            val service = ApiFactory.API
            GlobalScope.launch(Dispatchers.Main) {
                val tokenResponse = service.authAsync(login, password)
                try {
                    val response = tokenResponse.await()
                    if (response.isSuccessful) {
                        token = response.body()!!
                        it.resume(token)
                    } else {
                        error = "Error: " + response.errorBody() + "\n\nResponse: " + response.raw().message
                        Log.d(LOG_TAG,"TokenProvider - getToken error: " + response.errorBody() + "\n\ngetToken - Ответ сети: " + response.raw().message)
                        token = Token(error = error)
                        it.resume(token)
                    }
                } catch (e: Exception) {
                    error = e.toString()
                    Log.d(LOG_TAG, "TokenProvider - getToken Exception: $e")
                    token = Token(error = error)
                    it.resume(token)
                }
            }
        }
    }
}