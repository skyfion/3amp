package xyz.lazysoft.a3amp.persistence

import android.arch.persistence.room.Room
import android.content.Context
import javax.inject.Inject

class RepositoryImpl() : Repository {
    @Inject lateinit var context: Context
    private val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "3amp"
    ).build()

    override fun dataBase(): AppDatabase {
        return db
    }
}