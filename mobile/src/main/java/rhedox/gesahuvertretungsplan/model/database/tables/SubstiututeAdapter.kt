package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Substitute

/**
 * Created by robin on 19.10.2016.
 */
sealed class SubstiututeAdapter private constructor(){
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
            values.put(Substitutes.columnDate, date.toDateTime(LocalTime(0)).millis);
            return values;
        }
    }
}