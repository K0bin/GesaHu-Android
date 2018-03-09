package rhedox.gesahuvertretungsplan.model.api.deserializer

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.Supervision
import rhedox.gesahuvertretungsplan.model.api.SubstitutesList
import rhedox.gesahuvertretungsplan.model.database.Announcement
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 19.10.2016.
 */
class SubstitutesListDeserializer(context: Context) : JsonDeserializer<SubstitutesList> {
    private val resolver = AbbreviationResolver(context.applicationContext);

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): SubstitutesList {
        val jsonObject = json.asJsonObject;

        val announcement = jsonObject.get("Hinweise").asString
        val date = context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)

        val substitutes = mutableListOf<Substitute>()
        val jsonSubstitutes = jsonObject.getAsJsonArray("Stunden")
        for (jsonSubstitute in jsonSubstitutes) {
            substitutes.add(deserializeSubstitute(date, jsonSubstitute.asJsonObject))
        }

        val supervisions = mutableListOf<Supervision>()
        val jsonSupervisions = jsonObject.getAsJsonArray("Stunden")
        for (jsonSupervision in jsonSupervisions) {
            supervisions.add(deserializeSupervision(date, jsonSupervision.asJsonObject))
        }

        return SubstitutesList(Announcement(date, announcement), substitutes, date, supervisions)
    }

    private fun deserializeSubstitute(date: LocalDate, jsonObject: JsonObject): Substitute {
        var subjectAbbr = Html.decode(jsonObject.get("Fach").asString.trim());
        if (subjectAbbr == "---") {
            subjectAbbr = "";
        }
        val subject = resolver.resolveSubject(subjectAbbr);
        var _class = Html.decode(jsonObject.get("Klasse").asString.trim())
        if (_class == "---") {
            _class = "";
        }
        var teacherAbbr = Html.decode(jsonObject.get("Lehrer").asString.trim());
        if (teacherAbbr == "---") {
            teacherAbbr = "";
        }
        val teacher = resolver.resolveTeacher(teacherAbbr);
        var substituteAbbr = Html.decode(jsonObject.get("Vertretungslehrer").asString.trim());
        if (substituteAbbr == "---") {
            substituteAbbr = "";
        }
        val substitute = resolver.resolveTeacher(substituteAbbr);
        val hint = Html.decode(jsonObject.get("Hinweis").asString.trim());
        var room = Html.decode(jsonObject.get("Raum").asString.trim());
        if (room == "---") {
            room = "";
        }
        val isRelevant = jsonObject.get("relevant").asString.toLowerCase() == "true";

        //Bindestrich workaround
        val beginStr = jsonObject.get("Stundeanfang").asString.replace("-","").trim();
        val begin = beginStr.toInt();
        val endStr = jsonObject.get("Stundeende").asString.replace("-","").trim();
        val end = endStr.toInt();

        return Substitute(date, begin, (end - begin) + 1, subject, _class, teacher, substitute, room, hint, isRelevant);
    }

    private fun deserializeSupervision(date: LocalDate, jsonObject: JsonObject): Supervision {
        val time = Html.decode(jsonObject.get("Zeitraum").asString.trim());
        val teacherAbbr = Html.decode(jsonObject.get("Lehrer").asString.trim());
        val teacher = resolver.resolveTeacher(teacherAbbr);
        val substituteAbbr = Html.decode(jsonObject.get("Vertretungslehrer").asString.trim());
        val substitute = resolver.resolveTeacher(substituteAbbr);
        val _location = Html.decode(jsonObject.get("Ort").asString.trim());
        val location = if (_location == "---") "" else _location
        val isRelevant = jsonObject.get("relevant").asString.toLowerCase() == "true";

        return Supervision(date, time, teacher, substitute, location, isRelevant);
    }
}
