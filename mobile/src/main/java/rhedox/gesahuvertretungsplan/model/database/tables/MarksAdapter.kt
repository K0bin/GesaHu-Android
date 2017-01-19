package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import rhedox.gesahuvertretungsplan.model.database.Mark
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 19.01.2017.
 */
object MarksAdapter {
    fun toContentValues(lesson: Mark, boardId: Long? = null): ContentValues {
        val values = ContentValues();
        values.put(MarksContract.Table.columnBoardId, boardId ?: lesson.boardId)
        values.put(MarksContract.Table.columnId, lesson.id)
        values.put(MarksContract.Table.columnDate, lesson.date.unixTimeStamp)
        values.put(MarksContract.Table.columnDescription, lesson.description)
        values.put(MarksContract.Table.columnMark, lesson.mark)
        values.put(MarksContract.Table.columnMarkKind, lesson.markKind)
        values.put(MarksContract.Table.columnKind, lesson.kind)
        values.put(MarksContract.Table.columnAverage, lesson.average)
        values.put(MarksContract.Table.columnLogo, lesson.logo)
        values.put(MarksContract.Table.columnWeighting, lesson.weighting)
        return values;
    }

    fun marksFromCursor(cursor: Cursor): List<Mark> {
        val list = mutableListOf<Mark>();
        if (cursor.count == 0 || cursor.isClosed)
            return list;

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val mark = markFromCursor(cursor)

            if (mark != null)
                list.add(mark);

            cursor.moveToNext()
        }
        return list;
    }

    fun markFromCursor(cursor: Cursor): Mark? {
        if (cursor.count == 0 || cursor.columnCount < 1 || cursor.isClosed)
            return null

        val date = localDateFromUnix(cursor.getInt(cursor.getColumnIndex(MarksContract.Table.columnDate)))
        val description = cursor.getString(cursor.getColumnIndex(MarksContract.Table.columnDescription))
        val mark = cursor.getInt(cursor.getColumnIndex(MarksContract.Table.columnMark))
        val kind = cursor.getString(cursor.getColumnIndex(MarksContract.Table.columnKind))
        val average = cursor.getFloat(cursor.getColumnIndex(MarksContract.Table.columnAverage))
        val markKind = cursor.getLong(cursor.getColumnIndex(MarksContract.Table.columnMarkKind))
        val logo = cursor.getString(cursor.getColumnIndex(MarksContract.Table.columnLogo))
        val weighting = cursor.getFloat(cursor.getColumnIndex(MarksContract.Table.columnWeighting))
        val id = cursor.getLong(cursor.getColumnIndex(MarksContract.Table.columnBoardId))
        val boardsId = cursor.getLong(cursor.getColumnIndex(MarksContract.Table.columnBoardId))

        return Mark(date, description, mark, kind, average, markKind, logo, weighting, id = id, boardId = boardsId)
    }
}