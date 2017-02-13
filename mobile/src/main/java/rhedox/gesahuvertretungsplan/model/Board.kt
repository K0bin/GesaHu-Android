package rhedox.gesahuvertretungsplan.model

import android.support.annotation.IntDef
import android.support.annotation.StringDef
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate

/**
 * Created by robin on 14.01.2017.
 */
data class Board(val name: String,
                 val mark: String?,
                 val markRemark: String,
                 val missedLessons: Int,
                 val missedLessonsWithSickNotes: Int,
                 val lessonsTotal: Int,
                 val id: Long? = null)