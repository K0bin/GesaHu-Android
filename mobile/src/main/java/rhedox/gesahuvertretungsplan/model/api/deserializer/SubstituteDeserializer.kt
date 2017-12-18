package rhedox.gesahuvertretungsplan.model.api.deserializer

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 19.10.2016.
 */
class SubstituteDeserializer(context: Context) : JsonDeserializer<Substitute> {
    private val resolver = AbbreviationResolver(context.applicationContext);

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Substitute {
        val jsonObject = json.asJsonObject;

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

        return Substitute(begin, (end - begin) + 1, subject, _class, teacher, substitute, room, hint, isRelevant);
    }
}
