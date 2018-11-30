package xyz.lazysoft.a3amp.persistence

import android.arch.persistence.room.*

@Dao
interface AmpPresetDao {
    @Query("SELECT * FROM presets")
    fun getAll(): List<AmpPreset>

    @Query("SELECT * FROM presets WHERE uid IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<AmpPreset>

    @Query("SELECT * FROM presets WHERE title LIKE :title")
    fun findByTitle(title: String): List<AmpPreset>

    @Insert
    fun insertAll(vararg presets: AmpPreset)

    @Delete
    fun delete(user: AmpPreset)

    @Insert
    fun insert(preset: AmpPreset)
}

//class AmpPresetRepositrory(dao: AmpPresetDao) {
//
//    fun geAll() {
//
//    }
//}

@Database(entities = [AmpPreset::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun presetDao(): AmpPresetDao
}