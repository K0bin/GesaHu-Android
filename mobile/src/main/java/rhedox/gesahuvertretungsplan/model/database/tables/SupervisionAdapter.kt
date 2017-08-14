package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.model.Supervision
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 19.10.2016.
 */
object SupervisionAdapter {
    fun toContentValues(supervision: Supervision, date: LocalDate): ContentValues {
        val values = ContentValues();
        values.put(SupervisionsContract.Table.columnTime, supervision.time)
        values.put(SupervisionsContract.Table.columnTeacher, supervision.teacher)
        values.put(SupervisionsContract.Table.columnSubstitute, supervision.substitute)
        values.put(SupervisionsContract.Table.columnLocation, supervision.location)
        values.put(SupervisionsContract.Table.columnIsRelevant, if (supervision.isRelevant) 1 else 0)
        //Datum als Unix Timestamp abspeichern, soll sich 2034 jemand anders drum kümmern
        values.put(SupervisionsContract.Table.columnDate, date.unixTimeStamp);
        return values;
    }

    fun fromCursor(cursor: Cursor): Supervision? {
        if(cursor.columnCount < 7 || cursor.isClosed || cursor.count == 0)
            return null;

        val id = cursor.getLong(cursor.getColumnIndex(SupervisionsContract.Table.columnId))
        val time = cursor.getString(cursor.getColumnIndex(SupervisionsContract.Table.columnTime))
        val teacher = cursor.getString(cursor.getColumnIndex(SupervisionsContract.Table.columnTeacher))
        val supervision = cursor.getString(cursor.getColumnIndex(SupervisionsContract.Table.columnSubstitute))
        val location = cursor.getString(cursor.getColumnIndex(SupervisionsContract.Table.columnLocation))
        val isRelevant = cursor.getInt(cursor.getColumnIndex(SupervisionsContract.Table.columnIsRelevant)) == 1

        return Supervision(time, teacher, supervision, location, isRelevant, id = id);
    }

    fun listFromCursor(cursor: Cursor): List<Supervision> {
        val list = mutableListOf<Supervision>();
        if(cursor.count == 0 || cursor.isClosed)
            return list;

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val supervision = fromCursor(cursor)

            if(supervision != null)
                list.add(supervision);

            cursor.moveToNext()
        }
        return list;
    }
}
