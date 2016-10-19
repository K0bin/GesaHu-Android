package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase

/**
 * Created by robin on 19.10.2016.
 */
sealed class Announcements private constructor() {
    companion object {
        const val name = "announcements";
        const val columnId = "id";
        const val columnDate = "date"
        const val columnText = "text";

        val availableColumns = setOf(
                columnId, columnDate, columnText)

        fun onCreate(db: SQLiteDatabase) {
            val sql = """CREATE TABLE $name
            (
                $columnId INTEGER PRIMARY KEY AUTOINCREMENT,
                $columnDate INTEGER,
                $columnText TEXT
            );
            """;
            db.execSQL(sql);
        }

        fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        }

        fun clear(db: SQLiteDatabase) {
            db.execSQL("DELETE FROM ${Substitutes.name} WHERE 1;");
        }
    }
}