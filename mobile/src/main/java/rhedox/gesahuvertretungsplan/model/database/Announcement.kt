package rhedox.gesahuvertretungsplan.model.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.time.LocalDate

/**
 * Created by robin on 09.03.2018.
 */
@Entity(tableName = Announcement.tableName)
data class Announcement(@ColumnInfo(name = "date") val date: LocalDate,
                        @ColumnInfo(name = "text") val text: String,
                        @ColumnInfo(name = "rowid") @PrimaryKey(autoGenerate = true) val id: Long? = null) {

    companion object {
        const val tableName = "announcements"
    }
}