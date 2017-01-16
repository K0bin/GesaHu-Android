package rhedox.gesahuvertretungsplan.model.api.json

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 08.01.2017.
 */
class BoardDeserializer: JsonDeserializer<Board> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Board? {
        val jsonObject = json.asJsonObject;

        val marks = context.deserialize<Array<Board.Mark>>(jsonObject.get("Noten"), Array<Board.Mark>::class.java).toList()
        val lessons = context.deserialize<Array<Board.Lesson>>(jsonObject.get("Stunden"), Array<Board.Lesson>::class.java).toList()
        val name = jsonObject.get("Board").asString
        val markStr = jsonObject.get("Endnote").asString
        val mark = if (markStr.isNotBlank() && markStr != "-") markStr.toInt() else null
        val markRemark = jsonObject.get("Endnote_Bemerkung").asString
        val missedLessons = jsonObject.get("Fehlstunden_gesamt").asInt
        val missedLessonsWithSickNotes = jsonObject.get("Fehlstunden_entschuldigt").asInt
        val totalLessons = jsonObject.get("Unterrichtsstunden_gesamt").asInt

        return Board(name, mark, markRemark, missedLessons, missedLessonsWithSickNotes, totalLessons, marks, lessons)
    }

    class MarkDeserializer: JsonDeserializer<Board.Mark> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Board.Mark {
            val jsonObject = json.asJsonObject;
            val date = context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)
            val description = jsonObject.get("Bezeichnung").asString
            val markStr = jsonObject.get("Note").asString
            val mark = if (markStr.isNotBlank() && markStr != "-") markStr.toInt() else null;
            val kind = jsonObject.get("Art").asString
            val markKindStr = jsonObject.get("Notenart").asString
            @Board.Mark.MarkKind val markKind = when (markKindStr) {
                "Gruppennote" -> Board.Mark.MarkKindValues.groupMark
                "Einzelnote" -> Board.Mark.MarkKindValues.mark
                else -> Board.Mark.MarkKindValues.unknown
            }
            val averageStr = jsonObject.get("Durchschnitt").asString
            val average = if (averageStr.isNotBlank()) averageStr.toFloat() else null
            val logo = jsonObject.get("Artlogo").asString
            val weightingStr = jsonObject.get("Artwichtung").asString
            val weighting = if (weightingStr.isNotBlank()) weightingStr.toFloat() else null

            return Board.Mark(date, description, mark, kind, average, markKind, logo, weighting)
        }
    }

    class LessonDeserializer : JsonDeserializer<Board.Lesson> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Board.Lesson {
            val jsonObject = json.asJsonObject;

            val date = context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)
            val topic = jsonObject.get("Stundenthema").asString
            val duration = jsonObject.get("Dauer").asInt
            val statusStr = jsonObject.get("Status").asString
            @Board.Lesson.Status val status = when (statusStr) {
                "anwesend" ->
                        Board.Lesson.StatusValues.present
                "abwensend" ->
                        Board.Lesson.StatusValues.absent
                "abwesend und entschuldigt" ->
                        Board.Lesson.StatusValues.absentWithSickNote
                else ->
                    Board.Lesson.StatusValues.present
            }
            val homework = jsonObject.get("HA_Inhalt").asString
            val homeworkDue = context.deserialize<LocalDate>(jsonObject.get("HA_Datum"), LocalDate::class.java)

            return Board.Lesson(date, topic, duration, status, homework, homeworkDue)
        }
    }
}
