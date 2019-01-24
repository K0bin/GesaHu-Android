package rhedox.gesahuvertretungsplan.model.database.entity

import androidx.annotation.IntDef
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.joda.time.LocalDate

/**
 * Created by robin on 19.01.2017.
 */
@Entity(tableName = Lesson.tableName,
        foreignKeys = [ForeignKey(entity = Board::class, parentColumns = ["name"], childColumns = ["boardName"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE, deferred = false)],
        indices = [(Index(name = "lessonBoardName", value = ["boardName"], unique = false))])
data class Lesson(val date: LocalDate,
                  val topic: String,
                  val duration: Int,
                  @Status val status: Int,
                  val homework: String?,
                  val homeworkDue: LocalDate?,
                  val boardName: String,
                  @PrimaryKey(autoGenerate = true) val id: Int? = null) {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(StatusValues.present, StatusValues.absent, StatusValues.absentWithSickNote)
    annotation class Status
    object StatusValues {
        const val present = 0 //"anwesend"
        const val absent = 1 //"abwesend"
        const val absentWithSickNote = 2 //"abwesend und entschuldigt"
    }

    companion object {
        const val tableName = "lessons"
    }
}