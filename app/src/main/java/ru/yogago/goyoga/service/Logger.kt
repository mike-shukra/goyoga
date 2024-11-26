package ru.yogago.goyoga.service

interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String)
}