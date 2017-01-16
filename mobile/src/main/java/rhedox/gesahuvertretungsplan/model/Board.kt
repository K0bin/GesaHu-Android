package rhedox.gesahuvertretungsplan.model

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
                 val id: Int? = null) {

    data class Mark(val date: LocalDate,
                    val description: String,
                    val mark: Int?,
                    @Kind val kind: String,
                    val average: Float?,
                    @MarkKind val markKind: String,
                    val logo: String,
                    val weighting: Float?,
                    val id: Int? = null) {

        @Retention(AnnotationRetention.SOURCE)
        @StringDef(KindValues.test, KindValues.monthlyOral, KindValues.testOrComplexTask)
        annotation class Kind
        object KindValues {
            const val test = "Klausur";
            const val monthlyOral = "mündliche monatliche Kursnote"
            const val testOrComplexTask = "Klausur/komplexe HA"
        }

        @Retention(AnnotationRetention.SOURCE)
        @StringDef(MarkKindValues.groupMark)
        annotation class MarkKind
        object MarkKindValues {
            const val groupMark = "Gruppennote";
            const val mark = "Einzelnote";
        }
    }

    data class Lesson(@SerializedName("Datum") val date: LocalDate,
                      @SerializedName("Stundenthema") val topic: String,
                      @SerializedName("Dauer") val duration: Int,
                      @SerializedName("Status") @Status val status: String,
                      @SerializedName("HA_Inhalt") val homeWork: String,
                      @SerializedName("HA_Datum") val homeWorkDue: LocalDate?,
                      val id: Int? = null) {

        @Retention(AnnotationRetention.SOURCE)
        @StringDef(StatusValues.present, StatusValues.absent, StatusValues.absentWithSickNote)
        annotation class Status
        object StatusValues {
            const val present = "anwesend";
            const val absent = "abwesend";
            const val absentWithSickNote = "abwesend und entschuldigt"
        }
    }

}