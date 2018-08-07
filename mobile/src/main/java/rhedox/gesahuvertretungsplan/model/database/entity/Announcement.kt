package rhedox.gesahuvertretungsplan.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDate

/**
 * Created by robin on 09.03.2018.
 */
@Entity(tableName = Announcement.tableName)
data class Announcement(val date: LocalDate,
                        val text: String,
                        @PrimaryKey(autoGenerate = true) val id: Long? = null) {

    companion object {
        const val tableName = "announcements"
    }
}