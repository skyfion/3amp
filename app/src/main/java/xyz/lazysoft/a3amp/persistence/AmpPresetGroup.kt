package xyz.lazysoft.a3amp.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class AmpPresetGroup(
        @PrimaryKey(autoGenerate = true) var uid: Int? = null,
        @ColumnInfo(name = "title") var title: String) {
    override fun toString(): String {
        return title
    }
}
