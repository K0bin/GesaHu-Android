package rhedox.gesahuvertretungsplan.model.api.json.deserializer

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.api.json.Exam
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 08.01.2017.
 */
class ExamDeserializer(context: Context): JsonDeserializer<Exam> {
    private val resolver = AbbreviationResolver(context.applicationContext);

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Exam {
        val jsonObject = json.asJsonObject;

        val date = context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)
        val subject = resolver.resolveSubject(jsonObject.get("Fach").asString)
        val course = jsonObject.get("Kurs").asString
        val examinee = jsonObject.get("Prüfling").asString
        val recorder =  resolver.resolveTeacher(jsonObject.get("Protokoll").asString)
        val examiner =  resolver.resolveTeacher(jsonObject.get("Prüfer").asString)
        val chair =  resolver.resolveTeacher(jsonObject.get("Vorsitz").asString)
        val time = context.deserialize<LocalTime>(jsonObject.get("Zeit"), LocalTime::class.java)
        val allowAudience = jsonObject.get("Zuschauer erlaubt").asBoolean
        val room = jsonObject.get("Raum").asString

        return Exam(date, subject, course, recorder, examiner, examinee, room, chair, time, allowAudience)
    }
}
