package rhedox.gesahuvertretungsplan.model

import android.support.annotation.IntDef
import android.support.annotation.StringDef
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate

/**
 * Created by robin on 14.01.2017.
 */
data class Board(val name: String,
                 val mark: Int?,
                 val markRemark: String,
                 val missedLessons: Int,
                 val missedLessonsWithSickNotes: Int,
                 val lessonsTotal: Int,
                 val marks: List<Mark>,
                 val lessons: List<Lesson>,
                 val id: Long? = null) {

    data class Mark(val date: LocalDate,
                    val description: String,
                    val mark: Int?,
                    @Kind val kind: String,
                    val average: Float?,
                    @MarkKind val markKind: Long,
                    val logo: String,
                    val weighting: Float?,
                    val id: Long? = null) {

        @Retention(AnnotationRetention.SOURCE)
        @StringDef(KindValues.test, KindValues.monthlyOral, KindValues.testOrComplexTask)
        annotation class Kind
        object KindValues {
            const val test = "Klausur";
            const val monthlyOral = "mündliche monatliche Kursnote"
            const val testOrComplexTask = "Klausur/komplexe HA"
        }

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(MarkKindValues.unknown, MarkKindValues.groupMark, MarkKindValues.mark)
        annotation class MarkKind
        object MarkKindValues {
            const val unknown = 0L;
            const val mark = 1L;
            const val groupMark = 2L;
        }
    }

    data class Lesson(val date: LocalDate,
                      val topic: String,
                      val duration: Int,
                      @Status val status: Long,
                      @SerializedName("HA_Inhalt") val homeWork: String,
                      @SerializedName("HA_Datum") val homeWorkDue: LocalDate?,
                      val id: Long? = null) {

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(StatusValues.present, StatusValues.absent, StatusValues.absentWithSickNote)
        annotation class Status
        object StatusValues {
            const val present = 0L //"anwesend";
            const val absent = 1L //"abwesend";
            const val absentWithSickNote = 2L //"abwesend und entschuldigt"
        }
    }

}