package rhedox.gesahuvertretungsplan.model.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
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