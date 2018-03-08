package rhedox.gesahuvertretungsplan.model.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.Supervision
import java.lang.reflect.Type

/**
 * Created by robin on 01.10.2016.
 */

data class SubstitutesList(val announcement: String, val substitutes: List<Substitute>, val date: LocalDate, val supervisions: List<Supervision>) {
    val hasSubstitutes: Boolean
        @JvmName("hasSubstitutes")
        get() = substitutes.isNotEmpty();

    val hasAnnouncement: Boolean
        @JvmName("hasAnnouncement")
        get() = announcement.isNotEmpty() && announcement.trim() != "keine";

    val hasSupervisions: Boolean
        @JvmName("hasSupervisions")
        get() = supervisions.isNotEmpty()
}
