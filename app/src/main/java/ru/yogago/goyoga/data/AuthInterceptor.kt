package ru.yogago.goyoga.data

import okhttp3.Interceptor
import okhttp3.Response
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = firebaseAuth.currentUser?.getIdToken(false)?.result?.token
        val requestBuilder = chain.request().newBuilder()

        // Проверка токена
        token?.let {
            requestBuilder.header("X-Authorization-Firebase", it)
        }

        return chain.proceed(requestBuilder.build())
    }
}
