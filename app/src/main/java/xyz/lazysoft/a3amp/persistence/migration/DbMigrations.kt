package xyz.lazysoft.a3amp.persistence.migration

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object DbMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE presets ADD COLUMN amp_model integer")
          //  database.execSQL("ALTER TABLE presets ADD COLUMN group_id integer")
        }
    }
}