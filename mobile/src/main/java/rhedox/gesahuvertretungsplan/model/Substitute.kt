package rhedox.gesahuvertretungsplan.model

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 01.10.2016.
 */
data class Substitute(val lessonBegin: Int, val lessonEnd: Int, val subject: String, val course: String, val teacher: String, val substitute: String, val room: String, val hint: String, val isRelevant: Boolean) : Comparable<Substitute> {
    val kind: Kind;

    val lessonText: String
        get() = if(lessonBegin != lessonEnd) lessonBegin.toString() + "-" + lessonEnd.toString() else lessonBegin.toString();

    val title: String
        get() = course + " " + subject;

    init {
        val lowerSubstitute = substitute.toLowerCase()
        val lowerHint = hint.toLowerCase()
        if (lowerSubstitute == "eigv. lernen" || lowerHint.contains("eigenverantwortliches arbeiten") || lowerHint.contains("entfällt"))
            kind = Kind.Dropped
        else if ((substitute.isBlank() || substitute == teacher) && lowerHint == "raumänderung")
            kind = Kind.RoomChange
        else if (lowerHint.contains("klausur"))
            kind = Kind.Test
        else if (lowerHint.contains("findet statt"))
            kind = Kind.Regular
        else
            kind = Kind.Substitute
    }

    override fun compareTo(other: Substitute): Int {
        if (isRelevant) {
            if (!other.isRelevant)
                return -1
            else {
                if (lessonBegin - other.lessonBegin == 0) {
                    return lessonEnd - other.lessonEnd;
                }

                return lessonBegin - other.lessonBegin;
            }
        } else {
            if (other.isRelevant)
                return 1
            else {
                if (lessonBegin - other.lessonBegin == 0) {
                    return lessonEnd - other.lessonEnd;
                }

                return lessonBegin - other.lessonBegin
            }
        }
    }

    enum class Kind {
        Substitute,
        Dropped,
        RoomChange,
        Test,
        Regular
    }
}
