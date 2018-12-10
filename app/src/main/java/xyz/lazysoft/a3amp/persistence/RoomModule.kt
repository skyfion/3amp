package xyz.lazysoft.a3amp.persistence

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.lazysoft.a3amp.persistence.Migrations.MIGRATION_1_2
import javax.inject.Singleton

@Module
class RoomModule(application: Context) {

    private val dataBase = Room
            .databaseBuilder(application, AppDatabase::class.java, "3amp-db")
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providesRoomDatabase(): AppDatabase {
        return dataBase
    }

    @Singleton
    @Provides
    fun providesAmpPresetsDao(): PresetDao {
        return dataBase.presetDao()
    }


}