package rhedox.gesahuvertretungsplan.model.api.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder
import org.joda.time.format.DateTimePrinter
import java.lang.reflect.Type

/**
 * Created by robin on 08.10.2016.
 */

class DateTimeDeserializer : JsonDeserializer<DateTime> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DateTime {
        if(json == null)
            return DateTime();

        val formatter = DateTimeFormatterBuilder()
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

        return DateTime.parse(json.asString, formatter);
    }
}