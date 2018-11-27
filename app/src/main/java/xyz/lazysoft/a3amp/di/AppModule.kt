package xyz.lazysoft.a3amp.di

import dagger.Binds
import dagger.Module
import xyz.lazysoft.a3amp.persistence.Repository
import xyz.lazysoft.a3amp.persistence.RepositoryImpl
import javax.inject.Singleton


@Module
interface AppModule {

    @Singleton
    @Binds
    fun repository(repository: RepositoryImpl): Repository


}