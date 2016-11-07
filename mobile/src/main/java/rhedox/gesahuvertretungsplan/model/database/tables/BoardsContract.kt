package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider

/**
 * Created by robin on 30.10.2016.
 */
object BoardsContract {
    const val avatarFileName = "avatar.jpg";
    const val name = "boards";
    const val columnId = "id";
    const val columnName = "name"

    val uri = Uri.Builder()
            .scheme("content")
            .authority(BoardsContentProvider.authority)
            .build();

    val columns = setOf(
            columnId, columnName)

    fun onCreate(db: SQLiteDatabase) {
        val sql = """CREATE TABLE $name
            (
                $columnId INTEGER PRIMARY KEY AUTOINCREMENT,
                $columnName TEXT
            );
            """;
        db.execSQL(sql);
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    fun clear(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM ${BoardsContract.name} WHERE 1;");
    }
}
