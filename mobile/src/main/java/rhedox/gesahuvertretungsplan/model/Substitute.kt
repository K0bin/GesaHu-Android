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

    class Deserializer(context: Context) : JsonDeserializer<Substitute> {
        val resolver: AbbreviationResolver;

        init {
            this.resolver = AbbreviationResolver(context.applicationContext);
        }

        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Substitute {
            val jsonObject = json.asJsonObject;

            val subjectAbbr = if(!jsonObject.get("Fach").asString.isNullOrBlank()) Html.decode(jsonObject.get("Fach").asString.trim()) else "";
            val subject = resolver.resolveSubject(subjectAbbr);
            val _class = if(!jsonObject.get("Klasse").asString.isNullOrBlank()) Html.decode(jsonObject.get("Klasse").asString.trim()) else "";
            val teacherAbbr = if(!jsonObject.get("Lehrer").asString.isNullOrBlank()) Html.decode(jsonObject.get("Lehrer").asString.trim()) else "";
            val teacher = resolver.resolveTeacher(teacherAbbr);
            val substituteAbbr = if(!jsonObject.get("Vertretungslehrer").asString.isNullOrBlank()) Html.decode(jsonObject.get("Vertretungslehrer").asString.trim()) else "";
            val substitute = resolver.resolveTeacher(substituteAbbr);
            val hint = if(!jsonObject.get("Hinweis").asString.isNullOrBlank()) Html.decode(jsonObject.get("Hinweis").asString.trim()) else "";
            val room = if(!jsonObject.get("Raum").asString.isNullOrBlank()) Html.decode(jsonObject.get("Raum").asString.trim()) else "";
            val isRelevant = if (jsonObject.get("relevant").asString.toLowerCase() == "true") true else false;

            //Bindestrich workaround
            val beginStr = jsonObject.get("Stundeanfang").asString.replace("-","").trim();
            val begin = beginStr.toInt();
            val endStr = jsonObject.get("Stundeende").asString.replace("-","").trim();
            val end = endStr.toInt();

            return Substitute(begin, end, subject, _class, teacher, substitute, room, hint, isRelevant);
        }

    }
}
