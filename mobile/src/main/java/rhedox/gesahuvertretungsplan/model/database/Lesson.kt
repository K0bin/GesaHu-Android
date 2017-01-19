package rhedox.gesahuvertretungsplan.model.database

import android.support.annotation.IntDef
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate

/**
 * Created by robin on 19.01.2017.
 */
data class Lesson(val date: LocalDate,
                  val topic: String,
                  val duration: Int,
                  @Status val status: Long,
                  @SerializedName("HA_Inhalt") val homeWork: String,
                  @SerializedName("HA_Datum") val homeWorkDue: LocalDate?,
                  val id: Long? = null,
                  val boardId: Long? = null) {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(StatusValues.present, StatusValues.absent, StatusValues.absentWithSickNote)
    annotation class Status
    object StatusValues {
        const val present = 0L //"anwesend";
        const val absent = 1L //"abwesend";
        const val absentWithSickNote = 2L //"abwesend und entschuldigt"
    }
}