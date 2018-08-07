package rhedox.gesahuvertretungsplan.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDate

/**
 * Created by robin on 01.08.17.
 */
@Entity(tableName = Supervision.tableName)
data class Supervision(val date: LocalDate,
                       val time: String,
                       val teacher: String,
                       val substitute: String,
                       val location: String,
                       val isRelevant: Boolean,
                       @PrimaryKey(autoGenerate = true) val id: Long? = null) {

    companion object {
        const val tableName = "supervisions"
    }
}