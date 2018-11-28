package xyz.lazysoft.a3amp.di

import android.app.Application
import dagger.Component
import org.jetbrains.annotations.Nullable
import xyz.lazysoft.a3amp.MainActivity
import xyz.lazysoft.a3amp.persistence.AmpPresetDao
import xyz.lazysoft.a3amp.persistence.AppDatabase
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RoomModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun ampPresetDao(): AmpPresetDao
    fun dataBase(): AppDatabase
    fun application(): Application
}