package xyz.lazysoft.a3amp

import android.arch.persistence.room.Room
import android.content.Context
import android.support.test.runner.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lazysoft.a3amp.amp.Constants
import xyz.lazysoft.a3amp.persistence.AmpPreset
import xyz.lazysoft.a3amp.persistence.AmpPresetDao
import xyz.lazysoft.a3amp.persistence.AppDatabase
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var presetDao: AmpPresetDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java).build()
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
        val preset = AmpPreset(title = "test", dump = Constants.byteArrayOf(0x00, 0x01, 0xff))
        presetDao.insert(preset)
        val byName = presetDao.findByTitle("test")
        assertThat(byName[0], equalTo(preset))
    }
}