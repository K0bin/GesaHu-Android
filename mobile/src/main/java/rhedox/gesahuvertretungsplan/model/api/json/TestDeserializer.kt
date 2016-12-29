package rhedox.gesahuvertretungsplan.model.api.json

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 19.10.2016.
 */
class TestDeserializer() : JsonDeserializer<Test> {
    private val localDateDeserializer = LocalDateDeserializer()

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Test {
        val jsonObject = json.asJsonObject;

        val remark = Html.decode(jsonObject.get("Bemerkung").asString.trim())
        val date = localDateDeserializer.deserialize(jsonObject.get("Bemerkung"), LocalDate::class.java, context)
        val subject = Html.decode(jsonObject.get("Fach").asString.trim())
        val course = Html.decode(jsonObject.get("Klasse").asString.trim())
        val year = Html.decode(jsonObject.get("Klassenstufe").asString.trim()).toInt()
        val teacher = Html.decode(jsonObject.get("Lehrer").asString.trim())
        val lessons = Html.decode(jsonObject.get("Stunden").asString.trim()).replace(".", "")

        val begin: Int;
        val duration: Int;

        val lessonParts = lessons.split('-')
        if(lessonParts.isNotEmpty()) {
            begin = lessonParts[0].toInt()
            if (lessonParts.size > 1) {
                val end = lessonParts[1].toInt()
                duration = (end - begin) + 1
            } else {
                duration = 1;
            }
        } else {
            begin = -1;
            duration = -1;
        }

        return Test(remark, date, subject, course, year, teacher, begin, duration)
    }
}