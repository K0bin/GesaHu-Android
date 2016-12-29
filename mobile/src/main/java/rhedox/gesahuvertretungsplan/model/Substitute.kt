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
data class Substitute(val lessonBegin: Int, val duration: Int, val subject: String, val course: String, val teacher: String, val substitute: String, val room: String, val hint: String, val isRelevant: Boolean) : Comparable<Substitute> {
    @Kind val kind: Long;

    val lessonText: String = if(duration > 1) lessonBegin.toString() + "-" + (lessonBegin + duration - 1).toString() else lessonBegin.toString();
        get() = field

    val title: String = course + " " + subject;
        get() = field

    companion object {
        const val KIND_SUBSTITUTE = 0L;
        const val KIND_DROPPED = 1L;
        const val KIND_ROOM_CHANGE = 2L;
        const val KIND_TEST = 3L;
        const val KIND_REGULAR = 4L;


        @Retention(AnnotationRetention.SOURCE)
        @IntDef(KIND_SUBSTITUTE, KIND_DROPPED, KIND_ROOM_CHANGE, KIND_TEST, KIND_REGULAR, flag = true)
        annotation class Kind
    }

    init {
        val lowerSubstitute = substitute.toLowerCase()
        val lowerHint = hint.toLowerCase()
        if (lowerSubstitute == "eigv. lernen" || lowerHint.contains("eigenverantwortliches arbeiten") || lowerHint.contains("entfällt"))
            kind = KIND_DROPPED
        else if ((substitute.isBlank() || substitute == teacher) && lowerHint == "raumänderung")
            kind = KIND_ROOM_CHANGE
        else if (lowerHint.contains("klausur"))
            kind = KIND_TEST
        else if (lowerHint.contains("findet statt"))
            kind = KIND_REGULAR
        else
            kind = KIND_SUBSTITUTE
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
