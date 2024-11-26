package ru.yogago.goyoga.service

import android.util.Log

class AndroidLogger : Logger {
    override fun d(tag: String, message: String) {
        println("" + tag + message)
//        Log.d(tag, message)
    }

    override fun e(tag: String, message: String) {
        TODO("Not yet implemented")
    }
}
