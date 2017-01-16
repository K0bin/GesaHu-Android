package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.api.json.BoardName
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.util.localDateFromUnix

/**
 * Created by robin on 19.10.2016.
 */
object BoardAdapter {
    fun toContentValues(board: BoardName): ContentValues {
        val values = ContentValues();
        values.put(BoardsContract.Table.columnName, board.name)
        return values;
    }

    fun toContentValues(board: Board): ContentValues {
        val values = ContentValues();
        values.put(BoardsContract.Table.columnName, board.name)
        values.put(BoardsContract.Table.columnMark, board.mark)
        values.put(BoardsContract.Table.columnMarkRemark, board.markRemark)
        values.put(BoardsContract.Table.columnLessonsTotal, board.lessonsTotal)
        values.put(BoardsContract.Table.columnMissedLessons, board.missedLessons)
        values.put(BoardsContract.Table.columnMissedLessonsWithSickNotes, board.missedLessonsWithSickNotes)
        return values;
    }

    fun nameFromCursor(cursor: Cursor): String? {
        if (cursor.count == 0 || cursor.columnCount < 1 || cursor.isClosed)
            return null

        return cursor.getString(cursor.getColumnIndex(BoardsContract.Table.columnName));
    }

    fun namesFromCursor(cursor: Cursor): List<String> {
        val list = mutableListOf<String>();
        if (cursor.count == 0 || cursor.isClosed)
            return list;

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val board = BoardAdapter.nameFromCursor(cursor)

            if (board != null)
                list.add(board);

            cursor.moveToNext()
        }
        return list;
    }

    fun boardFromCursor(boardCursor: Cursor, lessonsCursor: Cursor, marksCursor: Cursor) {

    }

    fun markFromCursor(cursor: Cursor): Board.Mark? {
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

        return Board.Mark(date, description, mark, kind, average, markKind, logo, weighting, id = id)
    }

    fun lessonFromCursor(cursor: Cursor): Board.Lesson? {
        if (cursor.count == 0 || cursor.columnCount < 1 || cursor.isClosed)
            return null

        val date = localDateFromUnix(cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnDate)))
        val topic = cursor.getString(cursor.getColumnIndex(LessonsContract.Table.columnTopic))
        val duration = cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnDuration))
        val status = cursor.getString(cursor.getColumnIndex(LessonsContract.Table.columnStatus))
        val homework = cursor.getString(cursor.getColumnIndex(LessonsContract.Table.columnHomework))
        val homeworkDue = localDateFromUnix(cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnHomeworkDue)))
        val id = cursor.getLong(cursor.getColumnIndex(LessonsContract.Table.columnBoardId))

        return Board.Lesson(date, topic, duration, status, homework, homeworkDue, id = id)
    }
}