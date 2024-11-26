package ru.yogago.goyoga.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.yogago.goyoga.model.MainModel
import ru.yogago.goyoga.service.Api
import ru.yogago.goyoga.service.DBDao
import ru.yogago.goyoga.service.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRepository(dao: DBDao, api: Api, firebaseAuth: FirebaseAuth): Repository {
        return Repository(dao, api, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideMainModel(repository: Repository): MainModel {
        return MainModel(repository)
    }
}