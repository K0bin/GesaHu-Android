package rhedox.gesahuvertretungsplan.model.api.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder
import java.lang.reflect.Type

/**
 * Created by robin on 08.10.2016.
 */

class DateTimeDeserializer : JsonDeserializer<DateTime?> {
    private val formatter = DateTimeFormatterBuilder()
            .appendYear(4,4)
            .appendLiteral('-')
            .appendMonthOfYear(2)
            .appendLiteral('-')
            .appendDayOfMonth(2)
            .appendLiteral(' ')
            .appendHourOfDay(2)
            .appendLiteral(':')
            .appendMinuteOfHour(2)
            .appendLiteral(':')
            .appendSecondOfMinute(2)
            .toFormatter()

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DateTime? {
        if(json == null || json.asString.isNullOrBlank())
            return null

        return DateTime.parse(json.asString, formatter)
    }
}