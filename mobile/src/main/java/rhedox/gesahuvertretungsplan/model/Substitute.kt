package rhedox.gesahuvertretungsplan.model

import android.content.Context
import android.support.annotation.IntDef
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import rhedox.gesahuvertretungsplan.broadcastReceiver.SubstitutesAlarmReceiver
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 01.10.2016.
 */
data class Substitute(val lessonBegin: Int,
                      val duration: Int,
                      val subject: String,
                      val course: String,
                      val teacher: String,
                      val substitute: String,
                      val room: String,
                      val hint: String,
                      val isRelevant: Boolean,
                      val id: Long? = null) : Comparable<Substitute> {

    @Kind val kind: Long;

    val lessonText: String = if(duration > 1) lessonBegin.toString() + "-" + (lessonBegin + duration - 1).toString() else lessonBegin.toString();
        get() = field

    val title: String = course + " " + subject;
        get() = field

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(KindValues.substitute, KindValues.dropped, KindValues.roomChange, KindValues.test, KindValues.regular, flag = false)
    annotation class Kind

    object KindValues {
        const val substitute = 0L;
        const val dropped = 1L;
        const val roomChange = 2L;
        const val test = 3L;
        const val regular = 4L;
    }

    init {
        val lowerSubstitute = substitute.toLowerCase()
        val lowerHint = hint.toLowerCase()
        if (lowerSubstitute == "eigv. lernen" || lowerHint.contains("eigenverantwortliches arbeiten") || lowerHint.contains("entfällt"))
            kind = KindValues.dropped
        else if ((substitute.isBlank() || substitute == teacher) && lowerHint == "raumänderung")
            kind = KindValues.roomChange
        else if (lowerHint.contains("klausur"))
            kind = KindValues.test
        else if (lowerHint.contains("findet statt"))
            kind = KindValues.regular
        else
            kind = KindValues.substitute
    }

    override fun compareTo(other: Substitute): Int {
        if (isRelevant) {
            if (!other.isRelevant)
                return -1
            else {
                if (lessonBegin - other.lessonBegin == 0) {
                    return duration - other.duration;
                }

                return lessonBegin - other.lessonBegin;
            }
        } else {
            if (other.isRelevant)
                return 1
            else {
                if (lessonBegin - other.lessonBegin == 0) {
                    return duration - other.duration;
                }

                return lessonBegin - other.lessonBegin
            }
        }
    }
}
