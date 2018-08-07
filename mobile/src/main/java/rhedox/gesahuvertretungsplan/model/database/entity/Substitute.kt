package rhedox.gesahuvertretungsplan.model.database.entity

import androidx.annotation.IntDef
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.LocalDate

/**
 * Created by robin on 01.10.2016.
 */
@Entity(tableName = Substitute.tableName)
data class Substitute(val date: LocalDate,
                      val lessonBegin: Int,
                      val duration: Int,
                      val subject: String,
                      val course: String,
                      val teacher: String,
                      val substitute: String,
                      val room: String,
                      val hint: String,
                      val isRelevant: Boolean,
                      @PrimaryKey(autoGenerate = true) val id: Long? = null) : Comparable<Substitute> {
    companion object {
        const val tableName = "substitutes"
    }

    @Ignore
    @Kind
    val kind: Int;

    @Ignore
    val lessonText: String = if(duration > 1) lessonBegin.toString() + "-" + (lessonBegin + duration - 1).toString() else lessonBegin.toString();

    @Ignore
    val title: String;

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(KindValues.substitute, KindValues.dropped, KindValues.roomChange, KindValues.test, KindValues.regular, flag = false)
    annotation class Kind

    object KindValues {
        const val substitute = 0;
        const val dropped = 1;
        const val roomChange = 2;
        const val test = 3;
        const val regular = 4;
    }

    init {
        val lowerSubstitute = substitute.toLowerCase()
        val lowerHint = hint.toLowerCase()
        kind = if (lowerSubstitute == "eigv. lernen" || lowerHint.contains("eigenverantwortliches arbeiten") || lowerHint.contains("entfällt"))
            KindValues.dropped
        else if ((substitute.isBlank() || substitute == teacher) && lowerHint == "raumänderung")
            KindValues.roomChange
        else if (lowerHint.contains("klausur"))
            KindValues.test
        else if (lowerHint.contains("findet statt"))
            KindValues.regular
        else
            KindValues.substitute

        var titleStr = course
        if (course.isNotBlank() && subject.isNotBlank()) {
            titleStr += " ";
        }
        titleStr += subject;
        title = titleStr;
    }

    override fun compareTo(other: Substitute): Int {
        if (isRelevant) {
            if (!other.isRelevant)
                return -1
            else {
                if (lessonBegin == other.lessonBegin) {
                    if (duration == other.duration) {
                        return if (course == other.course) subject.compareTo(other.subject) else course.compareTo(other.course);
                    } else {
                        return duration - other.duration;
                    }
                }

                return lessonBegin - other.lessonBegin;
            }
        } else {
            if (other.isRelevant)
                return 1
            else {
                if (lessonBegin == other.lessonBegin) {
                    if (duration == other.duration) {
                        return if (course == other.course) subject.compareTo(other.subject) else course.compareTo(other.course);
                    } else {
                        return duration - other.duration;
                    }
                }

                return lessonBegin - other.lessonBegin
            }
        }
    }
}
