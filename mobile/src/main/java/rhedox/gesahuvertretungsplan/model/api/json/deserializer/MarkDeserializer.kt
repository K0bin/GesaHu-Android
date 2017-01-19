package rhedox.gesahuvertretungsplan.model.api.json.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.model.database.Mark
import java.lang.reflect.Type

/**
 * Created by robin on 19.01.2017.
 */
class MarkDeserializer: JsonDeserializer<Mark> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Mark {
        val jsonObject = json.asJsonObject;
        val date = context.deserialize<LocalDate>(jsonObject.get("Datum"), LocalDate::class.java)
        val description = jsonObject.get("Bezeichnung").asString
        val markStr = jsonObject.get("Note").asString
        val mark = if (markStr.isNotBlank() && markStr != "-") markStr.toInt() else null;
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

        return Mark(date, description, mark, kind, average, markKind, logo, weighting)
    }
}
