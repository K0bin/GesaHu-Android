package rhedox.gesahuvertretungsplan.model.api.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormatterBuilder
import java.lang.reflect.Type

/**
 * Created by robin on 08.01.2017.
 */
class LocalTimeDeserializer : JsonDeserializer<LocalTime> {
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

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalTime? {
        if(json == null || json.asString.isNullOrBlank())
            return null

        return LocalTime.parse(json.asString, formatter);
    }
}
