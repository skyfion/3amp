package xyz.lazysoft.a3amp.persistence

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                    "CREATE TABLE `groups` (`uid` INTEGER, `title` TEXT NOT NULL, PRIMARY KEY(`uid`));" +
                    "PRAGMA foreign_keys=off;" +
                    "BEGIN TRANSACTION;" +
                    "ALTER TABLE presets RENAME TO _presets_old;" +
                    "CREATE TABLE presets (`uid` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`title` TEXT NOT NULL, `group_id` INTEGER, " +
                    "`type_id` INTEGER, " +
                    "`dump` BLOB NOT NULL, " +
                    "FOREIGN KEY(`group_id`) REFERENCES `groups`(`uid`) ON UPDATE NO ACTION ON DELETE NO ACTION );" +
                    "INSERT INTO presets SELECT * FROM _presets_old;" +
                    "COMMIT;" +
                    "PRAGMA foreign_keys=on;")
        }
    }
}