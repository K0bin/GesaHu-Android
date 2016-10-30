package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase

/**
 * Created by robin on 30.10.2016.
 */
sealed class Boards private constructor() {
    companion object {
    const val name = "boards";
    const val columnId = "id";
    const val columnName = "name"

    val availableColumns = setOf(
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
        db.execSQL("DELETE FROM ${Boards.name} WHERE 1;");
    }
}
}