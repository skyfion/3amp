package xyz.lazysoft.a3amp.persistence

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import xyz.lazysoft.a3amp.components.utils.WorkerThread
import javax.inject.Singleton

@Module
class RoomModule(application: Application) {

    private val dataBase = Room
            .databaseBuilder(application, AppDatabase::class.java, "3amp-db")
            .build()

    @Singleton
    @Provides
    fun providesRoomDatabase(): AppDatabase {
        return dataBase
    }

    @Singleton
    @Provides
    fun providesAmpPresetsDao(): AmpPresetDao {
        return dataBase.presetDao()
    }


}