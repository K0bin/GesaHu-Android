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
object SubstituteAdapter {
    fun toContentValues(substitute: Substitute, date: LocalDate): ContentValues {
        val values = ContentValues();
        values.put(SubstitutesContract.columnCourse, substitute.course)
        values.put(SubstitutesContract.columnHint, substitute.hint)
        values.put(SubstitutesContract.columnSubject, substitute.subject)
        values.put(SubstitutesContract.columnLessonBegin, substitute.lessonBegin)
        values.put(SubstitutesContract.columnLessonEnd, substitute.lessonEnd)
        values.put(SubstitutesContract.columnTeacher, substitute.teacher)
        values.put(SubstitutesContract.columnSubstitute, substitute.substitute)
        values.put(SubstitutesContract.columnRoom, substitute.room)
        values.put(SubstitutesContract.columnIsRelevant, if (substitute.isRelevant) 1 else 0)
        //Datum als Unix Timestamp abspeichern, soll sich 2034 jemand anders drum kümmern
        values.put(SubstitutesContract.columnDate, date.unixTimeStamp);
        return values;
    }

    fun fromCursor(cursor: Cursor): Substitute? {
        if(cursor.columnCount < 10 || cursor.isClosed || cursor.count == 0)
            return null;

        val lessonBegin = cursor.getInt(cursor.getColumnIndex(SubstitutesContract.columnLessonBegin))
        val lessonEnd = cursor.getInt(cursor.getColumnIndex(SubstitutesContract.columnLessonEnd))
        val course = cursor.getString(cursor.getColumnIndex(SubstitutesContract.columnCourse))
        val subject = cursor.getString(cursor.getColumnIndex(SubstitutesContract.columnSubject))
        val teacher = cursor.getString(cursor.getColumnIndex(SubstitutesContract.columnTeacher))
        val substitute = cursor.getString(cursor.getColumnIndex(SubstitutesContract.columnSubstitute))
        val room = cursor.getString(cursor.getColumnIndex(SubstitutesContract.columnRoom))
        val hint = cursor.getString(cursor.getColumnIndex(SubstitutesContract.columnHint))
        val isRelevant = cursor.getInt(cursor.getColumnIndex(SubstitutesContract.columnIsRelevant)) == 1

        return Substitute(lessonBegin,  lessonEnd, subject, course, teacher, substitute, room, hint, isRelevant = isRelevant);
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
