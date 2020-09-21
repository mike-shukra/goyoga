package ru.yogago.goyoga.service

import android.content.Context
import android.util.Log
import androidx.room.Room
import ru.yogago.goyoga.data.AppConstants.LOG_TAG

object DataBase {
    lateinit var db: AppDatabase

    fun createDataBase(context: Context) : DataBase {
        this.db = Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration()
            .build()
        Log.d(LOG_TAG, "DataBase - createDataBase: " + this.db)
        return this
    }
}