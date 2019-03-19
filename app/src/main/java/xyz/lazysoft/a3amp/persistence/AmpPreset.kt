package xyz.lazysoft.a3amp.persistence

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "presets",
        foreignKeys = [ForeignKey(entity = AmpPresetGroup::class,
                parentColumns = ["uid"], childColumns = ["group_id"],
                onDelete = CASCADE)])
data class AmpPreset(
        @PrimaryKey(autoGenerate = true) var uid: Int? = null,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "group_id") var group: Int? = null,
        @ColumnInfo(name = "amp_model") var model: Int? = null,
        @NonNull @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "dump") var dump: ByteArray
) {

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (uid ?: 0)
        return result
    }

    override fun toString(): String {
        return title
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AmpPreset

        if (uid != other.uid) return false
        if (title != other.title) return false
        if (group != other.group) return false
        if (model != other.model) return false
        if (!dump.contentEquals(other.dump)) return false

        return true
    }
}