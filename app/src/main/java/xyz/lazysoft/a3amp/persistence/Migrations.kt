package xyz.lazysoft.a3amp.persistence

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `groups` (`uid` INTEGER, `title` TEXT NOT NULL, PRIMARY KEY(`uid`));")
            database.execSQL("ALTER TABLE presets ADD COLUMN group_id INTEGER;")
            // adding FOREIGN KEY
            database.beginTransaction()
            try {
                database.execSQL("ALTER TABLE `presets` RENAME TO _presets_old;")
                database.execSQL("CREATE TABLE `presets` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "`title` TEXT NOT NULL, " +
                            "`amp_model` INTEGER, " +
                            "`group_id` INTEGER, " +
                            "`dump` BLOB NOT NULL, " +
                            "FOREIGN KEY(`group_id`) REFERENCES `groups`(`uid`) ON UPDATE NO ACTION ON DELETE NO ACTION );")
                database.execSQL("INSERT INTO presets(title, dump) SELECT title, dump FROM _presets_old;")
                database.execSQL("DROP TABLE _presets_old;")
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }

        }
    }
}