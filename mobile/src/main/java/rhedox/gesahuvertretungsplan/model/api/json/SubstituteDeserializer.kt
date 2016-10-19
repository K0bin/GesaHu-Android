package rhedox.gesahuvertretungsplan.model.api.json

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