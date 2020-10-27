package ru.yogago.goyoga.service

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.yogago.goyoga.data.*

@Database(entities = [Settings::class, Asana::class, UserData::class, Token::class, ActionState::class, PurchaseItem::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDBDao(): DBDao
}

