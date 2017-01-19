package rhedox.gesahuvertretungsplan.model.api.json.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.Lesson
import java.lang.reflect.Type

/**
 * Created by robin on 19.01.2017.
 */
class LessonDeserializer : JsonDeserializer<Lesson> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Lesson {
        val jsonObject = json.asJsonObject;

        val date = context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)
        val topic = jsonObject.get("Stundenthema").asString
        val duration = jsonObject.get("Dauer").asInt
        val statusStr = jsonObject.get("Status").asString
        @Lesson.Status val status = when (statusStr) {
            "anwesend" ->
                Lesson.StatusValues.present
            "abwensend" ->
                Lesson.StatusValues.absent
            "abwesend und entschuldigt" ->
                Lesson.StatusValues.absentWithSickNote
            else ->
                Lesson.StatusValues.present
        }
        val homework = jsonObject.get("HA_Inhalt").asString
        val homeworkDue = context.deserialize<LocalDate>(jsonObject.get("HA_Datum"), LocalDate::class.java)

        return Lesson(date, topic, duration, status, homework, homeworkDue)
    }
}