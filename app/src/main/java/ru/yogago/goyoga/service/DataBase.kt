package ru.yogago.goyoga.service

import android.app.Application
import android.util.Log
import androidx.room.Room
import ru.yogago.goyoga.data.AppConstants.Companion.LOG_TAG

object DataBase {
    lateinit var db: AppDatabase

    fun createDataBase(application: Application) : DataBase {
        this.db = Room.databaseBuilder(application, AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration()
            .build()
        Log.d(LOG_TAG, "DataBase - createDataBase: " + this.db)
        return this
    }
    fun createDataBaseToMainTread(application: Application) : DataBase {
        this.db = Room.databaseBuilder(application, AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        Log.d(LOG_TAG, "DataBase - createDataBaseToMainTread: " + this.db)
        return this
    }
}