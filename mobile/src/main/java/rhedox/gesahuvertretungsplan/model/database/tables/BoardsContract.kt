package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider

/**
 * Created by robin on 30.10.2016.
 */
@Suppress("unused")
object BoardsContract {
    const val avatarFileName = "avatar.jpg";

    object Table {
        const val name = "boards";
        const val columnId = "rowid";
        const val columnName = "name"
        const val columnMark = "mark"
        const val columnMarkRemark = "markRemark"
        const val columnLessonsTotal = "lessonsTotal"
        const val columnMissedLessons = "missedLessons"
        const val columnMissedLessonsWithSickNotes = "missedLessonsWithSickNotes"

        val columns = setOf(
                columnId, columnName, columnMark, columnMarkRemark, columnLessonsTotal, columnMissedLessons, columnMissedLessonsWithSickNotes)
    }

    val uri: Uri = Uri.Builder()
            .scheme("content")
            .authority(BoardsContentProvider.authority)
            .build();

    fun onCreate(db: SQLiteDatabase) {
        val sql = """CREATE TABLE ${Table.name}
            (
                ${Table.columnName} TEXT UNIQUE,
                ${Table.columnMark} TEXT,
                ${Table.columnMarkRemark} TEXT,
                ${Table.columnLessonsTotal} INTEGER,
                ${Table.columnMissedLessons} INTEGER,
                ${Table.columnMissedLessonsWithSickNotes} INTEGER
            );
            """;
        db.execSQL(sql);
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(newVersion >= 5 && oldVersion < 5) {
            db.execSQL("ALTER TABLE ${Table.name} RENAME TO ${Table.name}_old")
            onCreate(db)
            db.execSQL("INSERT INTO ${Table.name} (${Table.columnId}, ${Table.columnName}) " +
                    "SELECT ${Table.columnId}, ${Table.columnName} " +
                    "FROM ${Table.name}_old")

            db.execSQL("DROP TABLE ${Table.name}_old")
        }

        if (newVersion >= 6 && oldVersion < 6) {
            db.execSQL("ALTER TABLE ${Table.name} RENAME TO ${Table.name}_old")
            onCreate(db)
            db.execSQL("INSERT INTO ${Table.name} (${Table.columnId}, ${Table.columnName}, ${Table.columnMark}, ${Table.columnMarkRemark}, ${Table.columnLessonsTotal}, ${Table.columnMissedLessons}, ${Table.columnMissedLessonsWithSickNotes}) " +
                    "SELECT ${Table.columnId}, ${Table.columnName}, ${Table.columnMark}, ${Table.columnMarkRemark}, ${Table.columnLessonsTotal}, ${Table.columnMissedLessons}, ${Table.columnMissedLessonsWithSickNotes} " +
                    "FROM ${Table.name}_old")

            db.execSQL("DROP TABLE ${Table.name}_old")
        }
    }

    fun clear(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM ${BoardsContract.Table.name} WHERE 1;");
    }

    fun uriWithId(id: Long): Uri {
        return Uri.withAppendedPath(uri, id.toString())
    }
}
