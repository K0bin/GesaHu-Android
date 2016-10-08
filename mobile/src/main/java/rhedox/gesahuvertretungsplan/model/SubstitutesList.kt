package rhedox.gesahuvertretungsplan.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate
import java.lang.reflect.Type

/**
 * Created by robin on 01.10.2016.
 */

data class SubstitutesList(@SerializedName("Hinweise") val announcement: String, @SerializedName("Stunden") val substitutes: List<Substitute>, @SerializedName("Datum") val date: LocalDate) {
    val hasSubstitutes: Boolean
        get() = substitutes.size > 0;

    val hasAnnouncement: Boolean
        get() = announcement.trim().length > 0 && announcement.trim() != "keine";
}
