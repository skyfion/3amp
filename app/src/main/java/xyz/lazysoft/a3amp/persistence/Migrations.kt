package xyz.lazysoft.a3amp.persistence

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

// https://www.techonthenet.com/sqlite/foreign_keys/foreign_keys.php
object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `groups` (`uid` INTEGER, `name` TEXT, PRIMARY KEY(`uid`));")
            database.execSQL("ALTER TABLE presets ADD COLUMN group_id INTEGER;")
            database.execSQL("ALTER TABLE presets ADD COLUMN type_id INTEGER;")
            database.execSQL("ALTER TABLE presets ADD FOREIGN KEY (uid) REFERENCES groups(uid);")

        }
    }
}