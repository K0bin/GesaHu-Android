package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.unixTimeStamp

/**
 * Created by robin on 19.10.2016.
 */
object AnnouncementAdapter {
    fun toContentValues(text: String, date: LocalDate): ContentValues {
        val values = ContentValues();
        values.put(AnnouncementsContract.columnText, text)
        values.put(AnnouncementsContract.columnDate, date.unixTimeStamp);
        return values;
    }

    fun fromCursor(cursor: Cursor): String {
        if(cursor.count == 0 || cursor.columnCount < 2 || cursor.isClosed)
            return "";

        cursor.moveToFirst()
        return cursor.getString(cursor.getColumnIndex(AnnouncementsContract.columnText));
    }
}
