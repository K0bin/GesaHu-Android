package rhedox.gesahuvertretungsplan.model.api.deserializer

import android.content.Context
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.api.BoardInfo
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.model.database.Mark
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract
import rhedox.gesahuvertretungsplan.util.Html
import java.lang.reflect.Type

/**
 * Created by robin on 08.01.2017.
 */
class BoardDeserializer: JsonDeserializer<BoardInfo> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): BoardInfo? {
        val jsonObject = json.asJsonObject;

        val marks = context.deserialize<Array<Mark>>(jsonObject.get("Noten"), Array<Mark>::class.java).toList()
        val lessons = context.deserialize<Array<Lesson>>(jsonObject.get("Stunden"), Array<Lesson>::class.java).toList()
        val name = jsonObject.get("Board").asString
        val markStr = jsonObject.get("Endnote").asString
        val mark = if (markStr.isNotBlank() && markStr != "-") markStr else null
        val markRemark = jsonObject.get("Endnote_Bemerkung").asString
        val missedLessons = jsonObject.get("Fehlstunden_gesamt").asInt
        val missedLessonsWithSickNotes = jsonObject.get("Fehlstunden_entschuldigt").asInt
        val totalLessonsStr = jsonObject.get("Unterrichtsstunden_gesamt").asString
        val totalLessons = if (totalLessonsStr.isNotBlank()) totalLessonsStr.toInt() else 0

        val board = Board(name, mark, markRemark, missedLessons, missedLessonsWithSickNotes, totalLessons)

        return BoardInfo(board, lessons, marks)
    }
}
