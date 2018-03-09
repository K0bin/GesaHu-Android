package rhedox.gesahuvertretungsplan.model.database

import android.support.annotation.IntDef
import android.support.annotation.StringDef
import org.joda.time.LocalDate

/**
 * Created by robin on 19.01.2017.
 */
data class Mark(val date: LocalDate,
                val description: String,
                val mark: String?,
                @Kind val kind: String,
                val average: Float?,
                @MarkKind val markKind: Int,
                val logo: String,
                val weighting: Float?,
                val id: Long? = null,
                val boardId: Long? = null) {

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(KindValues.test, KindValues.monthlyOral, KindValues.testOrComplexTask, KindValues.oral, KindValues.empty)
    annotation class Kind
    object KindValues {
        const val test = "Klausur";
        const val monthlyOral = "mündliche monatliche Kursnote"
        const val testOrComplexTask = "Klausur/komplexe HA"
        const val oral = "Mündlich"
        const val empty = "";
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(MarkKindValues.unknown, MarkKindValues.groupMark, MarkKindValues.mark)
    annotation class MarkKind
    object MarkKindValues {
        const val unknown = 0;
        const val mark = 1;
        const val groupMark = 2;
    }
}