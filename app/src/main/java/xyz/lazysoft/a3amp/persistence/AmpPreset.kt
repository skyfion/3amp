package xyz.lazysoft.a3amp.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "presets",
        foreignKeys = [ForeignKey(entity = AmpPresetGroup::class,
                parentColumns = ["uid"], childColumns = ["group_id"])])
data class AmpPreset(
        @PrimaryKey(autoGenerate = true) var uid: Int? = null,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "group_id") var group: Int? = null,
        @ColumnInfo(name = "type_id") var type: Int? = null,
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "dump") var dump: ByteArray?
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
        if (type != other.type) return false
        if (dump != null) {
            if (other.dump == null) return false
            if (!dump!!.contentEquals(other.dump!!)) return false
        } else if (other.dump != null) return false

        return true
    }

}