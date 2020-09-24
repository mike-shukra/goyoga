package ru.yogago.goyoga.service

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.yogago.goyoga.data.*

@Database(entities = [Asana::class, UserData::class, Token::class, ActionState::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDBDao(): DBDao
}

