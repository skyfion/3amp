package xyz.lazysoft.a3amp

import androidx.room.Room
import androidx.test.InstrumentationRegistry.getContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lazysoft.a3amp.persistence.*
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@LargeTest
class SimpleEntityReadWriteTest {
    private lateinit var presetDao: PresetDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = getContext()
        db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java)
                .addMigrations(Migrations.MIGRATION_1_2)
                .build()
        presetDao = db.presetDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val preset = AmpPreset(title = "test", dump = arrayListOf(0x00, 0x65, 0x01, 0xff)
                .map {it.toByte()}.toByteArray())
        val group = AmpPresetGroup(title = "test")
        presetDao.insertGroup(group)
        val groups = presetDao.getAllGroups()
        assert(groups.size == 1)
        assert(groups[0].title == "test")

        presetDao.insert(preset)
        val fromBd = presetDao.findByTitle("test")[0]
        assert(fromBd.title == "test")
      //  assertArrayEquals(fromBd.dump, (0x00, 0x65, 0x01, 0xff)) todo
    }
}