package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import rhedox.gesahuvertretungsplan.model.api.json.BoardName
import rhedox.gesahuvertretungsplan.model.Board

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

    fun markFromCursor(cursor: Cursor) {

    }
}