package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import rhedox.gesahuvertretungsplan.model.Board

/**
 * Created by robin on 19.10.2016.
 */
object BoardAdapter {
    fun toContentValues(board: Board): ContentValues {
        val values = ContentValues();
        values.put(BoardsContract.Table.columnName, board.name)
        return values;
    }

    fun fromCursor(cursor: Cursor): Board? {
        if (cursor.count == 0 || cursor.columnCount < 1 || cursor.isClosed)
            return null

        return Board(cursor.getString(cursor.getColumnIndex(BoardsContract.Table.columnName)));
    }

    fun listFromCursor(cursor: Cursor): List<Board> {
        val list = mutableListOf<Board>();
        if (cursor.count == 0 || cursor.isClosed)
            return list;

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val board = BoardAdapter.fromCursor(cursor)

            if (board != null)
                list.add(board);

            cursor.moveToNext()
        }
        return list;
    }
}