package ru.yogago.goyoga.service

import android.util.Log
import kotlinx.coroutines.*
import ru.yogago.goyoga.data.Token
import kotlin.coroutines.CoroutineContext

object TokenProvider: CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private const val LOG_TAG: String = "myLog"
    val service = ApiFactory.API
    var token: Token? = null
    lateinit var error: String

    suspend fun getToken(login: String, password: String) : Token{
        return withContext(coroutineContext) {
            val tokenResponse = service.authAsync(login, password, 0.toString(), 0.toString(), 0.toString(), 0.toString(), 0.toString())
            try {
                val response = tokenResponse.await()
                if (response.isSuccessful) {
                    token = response.body()!!
                    Log.d(LOG_TAG, "TokenProvider - getToken token: $token")
                    return@withContext token!!
                } else {
                    error = "Error: " + response.errorBody() + "\n\nResponse: " + response.raw().message
                    Log.d(LOG_TAG,"TokenProvider - getToken error: " + response.errorBody() + "\n\ngetToken - Ответ сети: " + response.raw().message)
                    token = Token(error = error)
                    return@withContext token!!
                }
            } catch (e: Exception) {
                error = e.toString()
                Log.d(LOG_TAG, "TokenProvider - getToken Exception: $e")
                token = Token(error = error)
                return@withContext token!!
            }
        }
    }
}