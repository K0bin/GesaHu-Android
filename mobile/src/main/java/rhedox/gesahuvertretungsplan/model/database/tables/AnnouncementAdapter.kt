package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Substitute

/**
 * Created by robin on 19.10.2016.
 */
sealed class AnnouncementAdapter private constructor(){
    companion object {
        fun toContentValues(text: String, date: LocalDate): ContentValues {
            val values = ContentValues();
            values.put(Announcements.columnText, text)
            values.put(Announcements.columnDate, date.toDateTime(LocalTime(0)).millis);
            return values;
        }

        fun fromCursor(cursor: Cursor): String {
            if(cursor.count == 0 || cursor.isAfterLast || cursor.columnCount < 2)
                return "";

            cursor.moveToFirst()
            return cursor.getString(cursor.getColumnIndex(Announcements.columnText));
        }
    }
}