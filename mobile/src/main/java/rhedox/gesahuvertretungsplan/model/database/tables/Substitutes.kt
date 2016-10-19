package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase

/**
 * Created by robin on 19.10.2016.
 */
sealed class Substitutes private constructor() {
    companion object {
        const val name = "substitutes";
        const val columnId = "id";
        const val columnDate = "date"
        const val columnLessonBegin = "lessonBegin";
        const val columnLessonEnd = "lessonEnd";
        const val columnSubject = "subject";
        const val columnCourse = "course";
        const val columnTeacher = "teacher";
        const val columnSubstitute = "substitute";
        const val columnRoom = "room";
        const val columnHint = "hint";
        const val columnIsRelevant = "isRelevant";

        val availableColumns = setOf(
            columnId, columnDate, columnCourse, columnLessonBegin, columnLessonEnd, columnSubject, columnTeacher, columnSubstitute, columnRoom, columnRoom, columnHint, columnIsRelevant)

        fun onCreate(db: SQLiteDatabase) {
            val sql = """CREATE TABLE $name
            (
                $columnId INTEGER PRIMARY KEY AUTOINCREMENT,
                $columnDate INTEGER,
                $columnLessonBegin INTEGER,
                $columnLessonEnd INTEGER,
                $columnSubject TEXT,
                $columnCourse TEXT,
                $columnTeacher TEXT,
                $columnSubstitute TEXT,
                $columnRoom TEXT,
                $columnHint TEXT,
                $columnIsRelevant INTEGER
            );
            """;
            db.execSQL(sql);
        }

        fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        }

        fun clear(db: SQLiteDatabase) {
            db.execSQL("DELETE FROM $name WHERE 1;");
        }
    }
}