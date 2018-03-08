package rhedox.gesahuvertretungsplan.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.joda.time.LocalDate

/**
 * Created by robin on 01.08.17.
 */
@Entity(tableName = Supervision.tableName)
data class Supervision(@ColumnInfo(name = "date") val date: LocalDate,
                       @ColumnInfo(name = "time") val time: String,
                       @ColumnInfo(name = "teacher") val teacher: String,
                       @ColumnInfo(name = "substitute") val substitute: String,
                       @ColumnInfo(name = "location") val location: String,
                       @ColumnInfo(name = "isRelevant") val isRelevant: Boolean,
                       @ColumnInfo(name = "rowid") @PrimaryKey(autoGenerate = true) val id: Long? = null) {

    companion object {
        const val tableName = "supervisions"
    }
}