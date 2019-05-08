package xyz.lazysoft.a3amp.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.lazysoft.a3amp.AmpApplication
import javax.inject.Singleton


@Module
class AppModule(private val app: AmpApplication) {

    @Provides
    @Singleton
    fun context(): Context {
        return app.applicationContext
    }


    @Provides
    @Singleton
    fun providesApplication(): Application {
        return app
    }
}