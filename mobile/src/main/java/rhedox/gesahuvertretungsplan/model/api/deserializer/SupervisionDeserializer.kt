package rhedox.gesahuvertretungsplan.model.api.deserializer

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.Supervision
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 19.10.2016.
 */
class SupervisionDeserializer(context: Context) : JsonDeserializer<Supervision> {
    private val resolver = AbbreviationResolver(context.applicationContext);

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Supervision {
        val jsonObject = json.asJsonObject;

        val time = Html.decode(jsonObject.get("Zeitraum").asString.trim());
        val teacherAbbr = Html.decode(jsonObject.get("Lehrer").asString.trim());
        val teacher = resolver.resolveTeacher(teacherAbbr);
        val substituteAbbr = Html.decode(jsonObject.get("Vertretungslehrer").asString.trim());
        val substitute = resolver.resolveTeacher(substituteAbbr);
        val _location = Html.decode(jsonObject.get("Ort").asString.trim());
        val location = if (_location == "---") "" else _location
        val isRelevant = jsonObject.get("relevant").asString.toLowerCase() == "true";

        return Supervision(time, teacher, substitute, location, isRelevant);
    }
}
