package rhedox.gesahuvertretungsplan.model.api.deserializer

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormatterBuilder
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.api.Exam
import java.lang.reflect.Type

/**
 * Created by robin on 08.01.2017.
 */
class ExamDeserializer(context: Context): JsonDeserializer<Exam> {
    private val resolver = AbbreviationResolver(context.applicationContext)
    private val secondsParser = DateTimeFormatterBuilder()
            .appendLiteral(':')
            .appendSecondOfMinute(2)
            .toParser()


    private val formatter = DateTimeFormatterBuilder()
            .appendHourOfDay(2)
            .appendLiteral(':')
            .appendMinuteOfHour(2)
            .appendOptional(secondsParser)
            .toFormatter()

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Exam {
        val jsonObject = json.asJsonObject

        val date = context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)
        val subject = resolver.resolveSubject(jsonObject.get("Fach").asString)
        val course = jsonObject.get("Kurs").asString
        val examinee = jsonObject.get("Prüfling").asString
        val recorder =  resolver.resolveTeacher(jsonObject.get("Protokoll").asString)
        val examiner =  resolver.resolveTeacher(jsonObject.get("Prüfer").asString)
        val chair =  resolver.resolveTeacher(jsonObject.get("Vorsitz").asString)
        val timeString = jsonObject.get("Zeit").asString
        val timeParts = timeString.split('-')
        val begin: LocalTime
        val duration: Duration?
        if (timeParts.isNotEmpty() && timeParts[0].isNotBlank()) {
            begin = LocalTime.parse(timeParts[0].replace('.', ':'), formatter)
            duration = if (timeParts.size > 1 && timeParts[1].isNotBlank()) {
                val end = LocalTime.parse(timeParts[1].replace('.', ':'), formatter)
                val durationMillis = end.millisOfDay - begin.millisOfDay
                Duration(durationMillis.toLong())
            } else {
                null
            }
        } else {
            begin = LocalTime()
            duration = null
        }

        val allowAudience = jsonObject.get("Zuschauer erlaubt").asBoolean
        val room = jsonObject.get("Raum").asString

        return Exam(date, subject, course, recorder, examiner, examinee, room, chair, begin, duration, allowAudience)
    }
}
