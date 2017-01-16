package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 19.10.2016.
 */
object SubstituteAdapter {
    fun toContentValues(substitute: Substitute, date: LocalDate): ContentValues {
        val values = ContentValues();
        values.put(SubstitutesContract.Table.columnCourse, substitute.course)
        values.put(SubstitutesContract.Table.columnHint, substitute.hint)
        values.put(SubstitutesContract.Table.columnSubject, substitute.subject)
        values.put(SubstitutesContract.Table.columnLessonBegin, substitute.lessonBegin)
        values.put(SubstitutesContract.Table.columnDuration, substitute.duration)
        values.put(SubstitutesContract.Table.columnTeacher, substitute.teacher)
        values.put(SubstitutesContract.Table.columnSubstitute, substitute.substitute)
        values.put(SubstitutesContract.Table.columnRoom, substitute.room)
        values.put(SubstitutesContract.Table.columnIsRelevant, if (substitute.isRelevant) 1 else 0)
        //Datum als Unix Timestamp abspeichern, soll sich 2034 jemand anders drum kümmern
        values.put(SubstitutesContract.Table.columnDate, date.unixTimeStamp);
        return values;
    }

    fun fromCursor(cursor: Cursor): Substitute? {
        if(cursor.columnCount < 10 || cursor.isClosed || cursor.count == 0)
            return null;

        val id = cursor.getInt(cursor.getColumnIndex(SubstitutesContract.Table.columnId))
        val lessonBegin = cursor.getInt(cursor.getColumnIndex(SubstitutesContract.Table.columnLessonBegin))
        val duration = cursor.getInt(cursor.getColumnIndex(SubstitutesContract.Table.columnDuration))
        val course = cursor.getString(cursor.getColumnIndex(SubstitutesContract.Table.columnCourse))
        val subject = cursor.getString(cursor.getColumnIndex(SubstitutesContract.Table.columnSubject))
        val teacher = cursor.getString(cursor.getColumnIndex(SubstitutesContract.Table.columnTeacher))
        val substitute = cursor.getString(cursor.getColumnIndex(SubstitutesContract.Table.columnSubstitute))
        val room = cursor.getString(cursor.getColumnIndex(SubstitutesContract.Table.columnRoom))
        val hint = cursor.getString(cursor.getColumnIndex(SubstitutesContract.Table.columnHint))
        val isRelevant = cursor.getInt(cursor.getColumnIndex(SubstitutesContract.Table.columnIsRelevant)) == 1

        return Substitute(lessonBegin, duration, subject, course, teacher, substitute, room, hint, isRelevant, id = id);
    }

    fun listFromCursor(cursor: Cursor): List<Substitute> {
        val list = mutableListOf<Substitute>();
        if(cursor.count == 0 || cursor.isClosed)
            return list;

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val substitute = fromCursor(cursor)

            if(substitute != null)
                list.add(substitute);

            cursor.moveToNext()
        }
        return list;
    }
}
