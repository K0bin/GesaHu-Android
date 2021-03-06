package rhedox.gesahuvertretungsplan.model.api.deserializer

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.api.Test
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

/**
 * Created by robin on 19.10.2016.
 */
class TestDeserializer(context: Context) : JsonDeserializer<Test> {
    private val resolver = AbbreviationResolver(context.applicationContext)

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Test {
        val jsonObject = json.asJsonObject

        var date: LocalDate? = try {
            context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)
        } catch (e: IllegalArgumentException) {
            Crashlytics.logException(e)
            null
        }

        if (date == null) {
            if (!jsonObject.get("Datum").toString().isBlank()) {
                Crashlytics.logException(Exception("Malformatted date: ${jsonObject.get("Datum")}"))
            }
            date = LocalDate.now()
        }

        val remark = Html.decode(jsonObject.get("Bemerkung").asString.trim())
        val subject = resolver.resolveSubject(Html.decode(jsonObject.get("Fach").asString.trim()))
        val course = Html.decode(jsonObject.get("Klasse").asString.trim())
        val year = Html.decode(jsonObject.get("Klassenstufe").asString.trim()).toInt()
        val teacher = resolver.resolveTeacher(Html.decode(jsonObject.get("Lehrer").asString.trim()))
        val lessons = Html.decode(jsonObject.get("Stunden").asString.trim())

        val begin: Int?
        val duration: Int?

        val lessonParts = lessons.replace(".", "")
                .replace(":", "")
                .replace(" ", "")
                .replace(',', '-')
                .replace('/', '-')
                .replace('&', '-')
                .replace('+', '-')
                .split('-')
        if(lessonParts.isNotEmpty() && lessonParts[0].isNotBlank()) {
            begin = lessonParts[0].toInt()
            duration = if (lessonParts.size > 1 && lessonParts[1].isNotBlank()) {
                val end = lessonParts[1].toInt()
                (end - begin) + 1
            } else {
                1
            }
        } else {
            begin = null
            duration = null
        }

        return Test(remark, date!!, subject, course, year, teacher, begin, duration)
    }
}
