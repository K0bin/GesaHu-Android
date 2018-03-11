package rhedox.gesahuvertretungsplan.model.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.IntDef
import android.support.annotation.StringDef
import org.joda.time.LocalDate

/**
 * Created by robin on 19.01.2017.
 */
@Entity(tableName = Mark.tableName,
        foreignKeys = [ForeignKey(entity = Board::class, parentColumns = ["name"], childColumns = ["boardName"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE, deferred = false)],
        indices = [(Index(name = "markBoardName", value = ["boardName"], unique = false))])
data class Mark(val date: LocalDate,
                val description: String,
                val mark: String?,
                @Kind val kind: String,
                val average: Float?,
                @MarkKind val markKind: Int,
                val logo: String,
                val weighting: Float?,
                val boardName: String,
                @PrimaryKey(autoGenerate = true) val id: Int? = null) {

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

    companion object {
        const val tableName = "marks"
    }
}