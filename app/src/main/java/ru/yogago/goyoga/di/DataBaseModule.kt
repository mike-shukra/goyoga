package ru.yogago.goyoga.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.yogago.goyoga.service.AppDatabase
import ru.yogago.goyoga.service.DBDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "databaseNew"
        ).build()
    }

    @Provides
    fun provideDao(database: AppDatabase): DBDao {
        return database.getDBDao()
    }

}