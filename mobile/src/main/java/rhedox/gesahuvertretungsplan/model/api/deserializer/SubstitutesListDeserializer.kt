package rhedox.gesahuvertretungsplan.model.api.deserializer

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.api.SubstitutesList
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 19.10.2016.
 */
class SubstitutesListDeserializer(context: Context) : JsonDeserializer<SubstitutesList> {
    private val resolver = AbbreviationResolver(context.applicationContext)

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): SubstitutesList {
        val jsonObject = json.asJsonObject
        val date = context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)

        val announcementStr = jsonObject.get("Hinweise").asString
        val announcement = Announcement(date, if(announcementStr.trim() != "keine") announcementStr else "")

        val substitutes = mutableListOf<Substitute>()
        val jsonSubstitutes = jsonObject.getAsJsonArray("Stunden")
        for (jsonSubstitute in jsonSubstitutes) {
            substitutes.add(deserializeSubstitute(date, jsonSubstitute.asJsonObject))
        }

        val supervisions = mutableListOf<Supervision>()
        val jsonSupervisions = jsonObject.getAsJsonArray("Aufsichten")
        for (jsonSupervision in jsonSupervisions) {
            supervisions.add(deserializeSupervision(date, jsonSupervision.asJsonObject))
        }

        return SubstitutesList(announcement, substitutes, date, supervisions)
    }

    private fun deserializeSubstitute(date: LocalDate, jsonObject: JsonObject): Substitute {
        var subjectAbbr = Html.decode(jsonObject.get("Fach").asString.trim())
        if (subjectAbbr == "---") {
            subjectAbbr = ""
        }
        val subject = resolver.resolveSubject(subjectAbbr)
        var schoolClass = Html.decode(jsonObject.get("Klasse").asString.trim())
        if (schoolClass  == "---") {
            schoolClass  = ""
        }
        var teacherAbbr = Html.decode(jsonObject.get("Lehrer").asString.trim())
        if (teacherAbbr == "---") {
            teacherAbbr = ""
        }
        val teacher = resolver.resolveTeacher(teacherAbbr)
        var substituteAbbr = Html.decode(jsonObject.get("Vertretungslehrer").asString.trim())
        if (substituteAbbr == "---") {
            substituteAbbr = ""
        }
        val substitute = resolver.resolveTeacher(substituteAbbr)
        val hint = Html.decode(jsonObject.get("Hinweis").asString.trim())
        var room = Html.decode(jsonObject.get("Raum").asString.trim())
        if (room == "---") {
            room = ""
        }
        val isRelevant = jsonObject.get("relevant").asString.toLowerCase() == "true"

        //Bindestrich workaround
        val beginStr = jsonObject.get("Stundeanfang").asString.replace("-","").trim()
        val begin = beginStr.toInt()
        val endStr = jsonObject.get("Stundeende").asString.replace("-","").trim()
        val end = endStr.toInt()

        return Substitute(date, begin, (end - begin) + 1, subject, schoolClass , teacher, substitute, room, hint, isRelevant)
    }

    private fun deserializeSupervision(date: LocalDate, jsonObject: JsonObject): Supervision {
        val time = Html.decode(jsonObject.get("Zeitraum").asString.trim())
        val teacherAbbr = Html.decode(jsonObject.get("Lehrer").asString.trim())
        val teacher = resolver.resolveTeacher(teacherAbbr)
        val substituteAbbr = Html.decode(jsonObject.get("Vertretungslehrer").asString.trim())
        val substitute = resolver.resolveTeacher(substituteAbbr)
        val locationStr = Html.decode(jsonObject.get("Ort").asString.trim())
        val location = if (locationStr == "---") "" else locationStr
        val isRelevant = jsonObject.get("relevant").asString.toLowerCase() == "true"

        return Supervision(date, time, teacher, substitute, location, isRelevant)
    }
}
