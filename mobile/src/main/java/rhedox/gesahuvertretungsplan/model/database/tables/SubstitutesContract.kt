package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.unixTimeStamp

/**
 * Created by robin on 19.10.2016.
 */
object SubstitutesContract {
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
    const val datePath = "date"
    const val path = "substitutes"

    val uri = Uri.Builder()
            .scheme("content")
            .authority(SubstitutesContentProvider.authority)
            .path(path)
            .build();
    val dateUri = Uri.withAppendedPath(uri, datePath)

    val columns = setOf(
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

    fun uriWithDate(date: LocalDate): Uri {
        return Uri.withAppendedPath(dateUri, date.unixTimeStamp.toString())
    }
    fun uriWithSeconds(seconds: Int): Uri {
        return Uri.withAppendedPath(dateUri, seconds.toString())
    }
    fun uriWithId(id: Long): Uri {
        return Uri.withAppendedPath(uri, id.toString())
    }
}
