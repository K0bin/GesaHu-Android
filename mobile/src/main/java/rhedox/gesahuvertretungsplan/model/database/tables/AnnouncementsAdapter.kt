package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Substitute

/**
 * Created by robin on 19.10.2016.
 */
sealed class AnnouncementsAdapter private constructor(){
    companion object {
        fun toContentValues(text: String, date: LocalDate): ContentValues {
            val values = ContentValues();
            values.put(Announcements.columnText, text)
            values.put(Announcements.columnDate, date.toDateTime(LocalTime(0)).millis);
            return values;
        }
    }
}