package xyz.lazysoft.a3amp.persistence

import androidx.room.*

@Dao
interface PresetDao {
    @Query("SELECT * FROM presets")
    fun getAll(): List<AmpPreset>

    @Query("SELECT * FROM presets WHERE uid IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<AmpPreset>

    @Query("SELECT * FROM presets WHERE uid = (:id)")
    fun loadById(id: Int): AmpPreset?

    @Query("SELECT * FROM presets WHERE title LIKE :title")
    fun findByTitle(title: String): List<AmpPreset>

    @Insert
    fun insertAll(vararg presets: AmpPreset)

    @Delete
    fun delete(user: AmpPreset)

    @Insert
    fun insert(preset: AmpPreset)

    @Update
    fun update(preset: AmpPreset)

    @Insert
    fun insertGroup(group: AmpPresetGroup): Long

    @Query("SELECT * FROM groups")
    fun getAllGroups(): List<AmpPresetGroup>
}


@Database(entities = [AmpPreset::class, AmpPresetGroup::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
}