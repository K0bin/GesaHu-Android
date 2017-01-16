package rhedox.gesahuvertretungsplan.model.api.json

import android.support.annotation.StringDef
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate

/**
 * Created by robin on 14.01.2017.
 */
data class Board(@SerializedName("Board") val board: String,
                 @SerializedName("Endnote") val mark: Int,
                 @SerializedName("Endnote_Bemerkung") val markRemark: String,
                 @SerializedName("Fehlstunden_gesamt") val missedLessons: Int,
                 @SerializedName("Fehlstunden_entschuldigt") val missedLessonsWithSickNotes: Int,
                 @SerializedName("Unterrichtsstunden_gesamt") val lessonsTotal: Int,
                 @SerializedName("Noten") val marks: List<Mark>,
                 @SerializedName("Stunden") val lessons: List<Lesson>) {

    data class Mark(@SerializedName("Datum") val date: LocalDate,
                    @SerializedName("Bezeichnung") val description: String,
                    @SerializedName("Note") val mark: Int,
                    @SerializedName("Art") val kind: Kind,
                    @SerializedName("Durchschnitt") val average: Float,
                    @SerializedName("Notenart") val markKind: MarkKind,
                    @SerializedName("Artlogo") val logo: String,
                    @SerializedName("Artwichtung") val weighting: Float) {

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
                      @SerializedName("Status") val status: Status,
                      @SerializedName("HA_Inhalt") val homeWork: String,
                      @SerializedName("HA_Datum") val homeWorkDue: LocalDate) {

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