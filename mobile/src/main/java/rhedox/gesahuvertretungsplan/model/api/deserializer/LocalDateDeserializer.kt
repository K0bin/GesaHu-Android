package rhedox.gesahuvertretungsplan.model.api.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder
import java.lang.reflect.Type

/**
 * Created by robin on 08.10.2016.
 */

class LocalDateDeserializer : JsonDeserializer<LocalDate?> {
    private val formatter = DateTimeFormatterBuilder()
            .appendYear(4, 4)
            .appendMonthOfYear(2)
            .appendDayOfMonth(2)
            .toFormatter()

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate? {
        if(json == null || json.asString.isNullOrBlank())
            return null

        return LocalDate.parse(json.asString, formatter);
    }
}
