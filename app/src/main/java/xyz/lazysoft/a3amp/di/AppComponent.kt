package xyz.lazysoft.a3amp.di

import android.content.Context
import dagger.Component
import xyz.lazysoft.a3amp.AmpApplication
import xyz.lazysoft.a3amp.MainActivity
import xyz.lazysoft.a3amp.PresetsActivity
import xyz.lazysoft.a3amp.amp.Amp
import xyz.lazysoft.a3amp.amp.AmpModule
import xyz.lazysoft.a3amp.persistence.PresetDao
import xyz.lazysoft.a3amp.persistence.AppDatabase
import xyz.lazysoft.a3amp.persistence.RoomModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RoomModule::class, AmpModule::class])
interface AppComponent {

    fun inject(application: AmpApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(presetsActivity: PresetsActivity)

    fun ampPresetDao(): PresetDao
    fun dataBase(): AppDatabase
    fun amp(): Amp
    fun context(): Context
}