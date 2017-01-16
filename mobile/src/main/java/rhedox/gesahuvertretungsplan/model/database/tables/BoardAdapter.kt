package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import rhedox.gesahuvertretungsplan.model.BoardName

/**
 * Created by robin on 19.10.2016.
 */
object BoardAdapter {
    fun toContentValues(board: BoardName): ContentValues {
        val values = ContentValues();
        values.put(BoardsContract.Table.columnName, board.name)
        return values;
    }

    fun fromCursor(cursor: Cursor): BoardName? {
        if (cursor.count == 0 || cursor.columnCount < 1 || cursor.isClosed)
            return null

        return BoardName(cursor.getString(cursor.getColumnIndex(BoardsContract.Table.columnName)));
    }

    fun listFromCursor(cursor: Cursor): List<BoardName> {
        val list = mutableListOf<BoardName>();
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