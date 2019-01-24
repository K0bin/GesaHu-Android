package rhedox.gesahuvertretungsplan.model.api.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.api.BoardInfo
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import java.lang.reflect.Type

/**
 * Created by robin on 08.01.2017.
 */
class BoardDeserializer: JsonDeserializer<BoardInfo> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): BoardInfo? {
        val jsonObject = json.asJsonObject

        val name = jsonObject.get("Board").asString
        val markStr = jsonObject.get("Endnote").asString
        val mark = if (markStr.isNotBlank() && markStr != "-") markStr else null
        val markRemark = jsonObject.get("Endnote_Bemerkung").asString
        val missedLessons = jsonObject.get("Fehlstunden_gesamt").asInt
        val missedLessonsWithSickNotes = jsonObject.get("Fehlstunden_entschuldigt").asInt
        val totalLessonsStr = jsonObject.get("Unterrichtsstunden_gesamt").asString
        val totalLessons = if (totalLessonsStr.isNotBlank()) totalLessonsStr.toInt() else 0

        val marks = mutableListOf<Mark>()
        val jsonMarks = jsonObject.getAsJsonArray("Noten")
        for (jsonMark in jsonMarks) {
            marks.add(deserializeMark(name, jsonMark.asJsonObject, context))
        }

        val lessons = mutableListOf<Lesson>()
        val jsonLessons = jsonObject.getAsJsonArray("Stunden")
        for (jsonLesson in jsonLessons) {
            lessons.add(deserializeLesson(name, jsonLesson.asJsonObject, context))
        }

        val board = Board(name, mark, markRemark, missedLessons, missedLessonsWithSickNotes, totalLessons)

        return BoardInfo(board, lessons, marks)
    }

    private fun deserializeLesson(boardName: String, jsonObject: JsonObject, context: JsonDeserializationContext): Lesson {
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

        return Lesson(date, topic, duration, status, homework, homeworkDue, boardName)
    }

    private fun deserializeMark(boardName: String, jsonObject: JsonObject, context: JsonDeserializationContext): Mark {
        val date = context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)
        val description = jsonObject.get("Bezeichnung").asString
        val markStr = jsonObject.get("Note").asString
        val mark = if (markStr.isNotBlank() && markStr != "-") markStr else null
        val kind = jsonObject.get("Art").asString
        val markKindStr = jsonObject.get("Notenart").asString
        @Mark.MarkKind val markKind = when (markKindStr) {
            "Gruppennote" -> Mark.MarkKindValues.groupMark
            "Einzelnote" -> Mark.MarkKindValues.mark
            else -> Mark.MarkKindValues.unknown
        }
        val averageStr = jsonObject.get("Durchschnitt").asString
        val average = if (averageStr.isNotBlank()) averageStr.toFloat() else null
        val logo = jsonObject.get("Artlogo").asString
        val weightingStr = jsonObject.get("Artwichtung").asString
        val weighting = if (weightingStr.isNotBlank()) weightingStr.toFloat() else null

        return Mark(date, description, mark, kind, average, markKind, logo, weighting, boardName)
    }
}
