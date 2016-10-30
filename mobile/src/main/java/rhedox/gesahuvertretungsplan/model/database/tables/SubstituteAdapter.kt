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
sealed class SubstituteAdapter private constructor(){
    companion object {
        fun toContentValues(substitute: Substitute, date: LocalDate): ContentValues {
            val values = ContentValues();
            values.put(Substitutes.columnCourse, substitute.course)
            values.put(Substitutes.columnHint, substitute.hint)
            values.put(Substitutes.columnSubject, substitute.subject)
            values.put(Substitutes.columnLessonBegin, substitute.lessonBegin)
            values.put(Substitutes.columnLessonEnd, substitute.lessonEnd)
            values.put(Substitutes.columnTeacher, substitute.teacher)
            values.put(Substitutes.columnSubstitute, substitute.substitute)
            values.put(Substitutes.columnRoom, substitute.room)
            values.put(Substitutes.columnIsRelevant, if (substitute.isRelevant) 1 else 0)
            //Datum als Unix Timestamp abspeichern, soll sich 2034 jemand anders drum kümmern
            values.put(Substitutes.columnDate, date.unixTimeStamp);
            return values;
        }

        fun fromCursor(cursor: Cursor): Substitute? {
            if(cursor.columnCount < 10 || cursor.isClosed || cursor.count == 0)
                return null;

            val lessonBegin = cursor.getInt(cursor.getColumnIndex(Substitutes.columnLessonBegin))
            val lessonEnd = cursor.getInt(cursor.getColumnIndex(Substitutes.columnLessonEnd))
            val course = cursor.getString(cursor.getColumnIndex(Substitutes.columnCourse))
            val subject = cursor.getString(cursor.getColumnIndex(Substitutes.columnSubject))
            val teacher = cursor.getString(cursor.getColumnIndex(Substitutes.columnTeacher))
            val substitute = cursor.getString(cursor.getColumnIndex(Substitutes.columnSubstitute))
            val room = cursor.getString(cursor.getColumnIndex(Substitutes.columnRoom))
            val hint = cursor.getString(cursor.getColumnIndex(Substitutes.columnHint))
            val isRelevant = cursor.getInt(cursor.getColumnIndex(Substitutes.columnIsRelevant)) == 1

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
}