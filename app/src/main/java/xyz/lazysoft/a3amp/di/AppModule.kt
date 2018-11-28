package xyz.lazysoft.a3amp.di

import android.app.Activity
import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val activity: Activity) {

    @Provides
    @Singleton
    fun providesActivity(): Activity {
        return activity
    }

    @Provides
    @Singleton
    fun providesApplication(): Application {
        return activity.application
    }
}