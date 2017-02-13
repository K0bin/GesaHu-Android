package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.model.database.Mark
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 19.10.2016.
 */
object BoardsAdapter {

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

    fun boardsFromCursor(cursor: Cursor): List<Board> {
        val list = mutableListOf<Board>();
        if (cursor.count == 0 || cursor.isClosed)
            return list;

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val board = BoardsAdapter.boardFromCursors(cursor)

            if (board != null)
                list.add(board);

            cursor.moveToNext()
        }
        return list;
    }

    fun boardFromCursors(boardCursor: Cursor): Board? {
        if (boardCursor.count == 0 || boardCursor.columnCount < 1 || boardCursor.isClosed)
            return null

        val name = boardCursor.getString(boardCursor.getColumnIndex(BoardsContract.Table.columnName))
        val mark = boardCursor.getString(boardCursor.getColumnIndex(BoardsContract.Table.columnMark))
        val markRemark = boardCursor.getString(boardCursor.getColumnIndex(BoardsContract.Table.columnMarkRemark))
        val missedLessons = boardCursor.getInt(boardCursor.getColumnIndex(BoardsContract.Table.columnMissedLessons))
        val missedLessonsWithSickNotes = boardCursor.getInt(boardCursor.getColumnIndex(BoardsContract.Table.columnMissedLessonsWithSickNotes))
        val lessonsTotal = boardCursor.getInt(boardCursor.getColumnIndex(BoardsContract.Table.columnLessonsTotal))
        val id = boardCursor.getLong(boardCursor.getColumnIndex(BoardsContract.Table.columnId))

        return Board(name, mark, markRemark, missedLessons, missedLessonsWithSickNotes, lessonsTotal, id)
    }
}