package rhedox.gesahuvertretungsplan.model.api.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import java.lang.reflect.Type

/**
 * Created by robin on 08.10.2016.
 */

class LocalDateDeserializer : JsonDeserializer<LocalDate?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate? {
        if(json == null || json.asString.isNullOrBlank())
            return null

        return LocalDate.parse(json.asString);
    }
}